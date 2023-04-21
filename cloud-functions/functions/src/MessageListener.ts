import { logger, database} from "firebase-functions";
import { database as admin_db, messaging } from "firebase-admin";
import { AchatFirebaseApp } from "./App";

AchatFirebaseApp.getApp();
const achat_db = admin_db()

export const onMessageSent = database.ref('MessageNotifications/{current_user_id}/{message_notification_id}').onWrite((change, context) => {
    logger.log('inside onMessageSent2 ', 'user id: ', context.params.current_user_id, ' message_notification_id: ', context.params.message_notification_id);
    logger.info('change:  ', JSON.stringify(change));
    logger.info('context params:  ', JSON.stringify(context.params));

    
    const current_user_id: string = context.params.current_user_id;
    const message_notification_id: string = context.params.message_notification_id;

    const fromUser = achat_db.ref(`/MessageNotifications/${current_user_id}/${message_notification_id}`).once('value');

    return fromUser.then(fromUserResult => {
        const from_user_id = fromUserResult.val().from;
        logger.info("from user id received: ", from_user_id);
        const userQuery = achat_db.ref(`Users/${from_user_id}/name`).once('value');
        const deviceToken = achat_db.ref(`/Users/${current_user_id}/device_token`).once('value');

        return Promise.all([userQuery, deviceToken]).then(result=>{
            const userName = result[0].val();
            const token_id = result[1].val();
            logger.info("fromUser name received: ", userName, "toUser tokenid received: ", token_id);

            const payload = {
                notification: {
                    title:"New Message",
                    body:`${userName} has sent you a message`,
                    icon:"default",
                    click_action:"com.revanth.apps.achat_TARGET_MESSAGE_NOTIFICATION"
                },
                data: {
                    from_user_id : from_user_id
                }
            };

            return messaging().sendToDevice(token_id,payload).then((response) => {
                const autoReplyDataQuery = achat_db.ref(`Users/${current_user_id}/auto_reply_data`).once('value');
                const onlineQuery = achat_db.ref(`Users/${current_user_id}/online`).once('value');

                const lastMessageIdQuery = achat_db.ref(`Chat/${from_user_id}/${current_user_id}/lastMessageId`).once('value')

                return Promise.all([autoReplyDataQuery, onlineQuery, lastMessageIdQuery])
                    .then((result) => {
                        const [autoReplyData, online, lastMessageId] = result.map(snapshot => snapshot.val());
                        logger.log("Autoreplydata retrieval successful");
                        logger.info("autoreply data", autoReplyData);
                        logger.info("online", online);
                        logger.info("lastMessageId", lastMessageId);

                        // if offline online value would be timestamp
                        if (typeof(online) === 'number') {

                            logger.log("User is offline");
                            let category = autoReplyData[from_user_id];
                            logger.info("User category is", category);

                            if (category) {
                                logger.log("User category is valid");
                                const lastMessageQuery = achat_db.ref(`messages/${from_user_id}/${current_user_id}/${lastMessageId}`).once('value');
                                logger.log("retrieved last message query reference");
                                lastMessageQuery.then(lastMessageSnapshot => {
                                    const lastMessageObj = lastMessageSnapshot.val();
                                    logger.log("retrieved lastMessage Object");
                                    logger.info("last message: ", lastMessageObj?.message);
                                    if (lastMessageObj?.message) {
                                        const lastMessage: string = lastMessageObj?.message;

                                        const fromUserMessageDbRef = achat_db.ref().child("messages/"+from_user_id+"/"+current_user_id)   
                                        const toUserMessageDbRef = achat_db.ref().child("messages/"+current_user_id+"/"+from_user_id)

                                        logger.log("retrieved message buckets of both users");

                                        const sendResponseAutomatically = (message: string) => {
                                            logger.log("sending message -- auto reply");
                                            const messageObject = {
                                                message,
                                                seen:false,
                                                type:"text",
                                                time: admin_db.ServerValue.TIMESTAMP,
                                                from: current_user_id
                                            };
                                            toUserMessageDbRef.push().set(messageObject);
                                            fromUserMessageDbRef.push().set(messageObject);
                                            logger.log("sent message -- auto reply");
                                        }
                                        const getMatchedMessages = (category: string, inputKeys: string[]): string[] => {
                                            let matchedMessages: string[] = [];
                                            [category, 'both'].forEach(cat => {
                                                matchedMessages = [...matchedMessages, ...inputKeys.filter(key => !!autoReplyData[cat][key]).map(key => autoReplyData[cat][key])];
                                            });
                                            return matchedMessages;
                                        };
                                        const inputKeys: string[] = lastMessage.split(" ").filter(key => !!key).map(key => key.toLowerCase());
                                        const matchedMessages = getMatchedMessages(category, inputKeys);
                                        let autoReplyMessage;
                                        if (!matchedMessages.length) {
                                            logger.log("No matched auto response");
                                            autoReplyMessage = autoReplyData["default_message"] || "I'm offline currently.";
                                            logger.info("Selecting default message: ", autoReplyMessage);
                                        } else {
                                            logger.log("Matched auto response");
                                            autoReplyMessage = matchedMessages[0];
                                            logger.info("Selecting AutoResponse message: ", autoReplyMessage);
                                        }
                                        sendResponseAutomatically(autoReplyMessage);
                                    } else {
                                        logger.error("Error in retrieving last message");
                                    }
                                });
                            } else {
                                logger.error("From user is not categorised");
                            }
                        } else if (online === true){
                            logger.log("User is online. autoreply not needed");
                        } else {
                            logger.error("Invalid online value: ", online);
                        }
                    })
                    .catch((e) => {
                        logger.error('error: ',e)
                    });
            });
        });
    });
});
