import { logger } from "firebase-functions";
import { database as admin_db, } from "firebase-admin";

const achat_db = admin_db()

export const autoReply = (data: any) => {
    const { current_user_id, from_user_id } = data;
    const autoReplyDataQuery = achat_db.ref(`Users/${current_user_id}/auto_reply_data`).once('value');
    const onlineQuery = achat_db.ref(`Users/${current_user_id}/online`).once('value');

    const lastMessageIdQuery = achat_db.ref(`Chat/${from_user_id}/${current_user_id}/lastMessageId`).once('value')

    return Promise.all([autoReplyDataQuery, onlineQuery, lastMessageIdQuery])
        .then((result) => {
            const [autoReplyData, online, lastMessageId] = result.map(snapshot => snapshot.val());
            const response = {
                status: 0,
                message: ""
            };
            logger.log("Autoreplydata retrieval successful");
            logger.info("autoreply data", autoReplyData);
            logger.info("online", online);
            logger.info("lastMessageId", lastMessageId);

            // if offline online value would be timestamp
            if (typeof(online) === 'number') {

                logger.log("User is offline");
                let category = autoReplyData?.user_categories?.[from_user_id];
                logger.info("User category is", category);

                if (category && category != 'none') {
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
                            const getMatchedMessages = (category: string[], inputKeys: string[]): string[] => {
                                let matchedMessages: string[] = [];
                                [...category, 'both'].forEach(cat => {
                                    matchedMessages = [...matchedMessages, ...inputKeys.filter(key => !!(autoReplyData[cat] && autoReplyData[cat][key])).map(key => autoReplyData[cat][key])];
                                });
                                return matchedMessages;
                            };
                            const inputKeys: string[] = lastMessage.split(" ").filter(key => !!key).map(key => key.toLowerCase());
                            const matchedMessages = getMatchedMessages(category != 'both' ? [category] : ['friend', 'family'], inputKeys);
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
                            response.status = 500;
                            response.message = "Error in retrieving last message";
                        }
                    });
                } else if (category == 'none') {
                    logger.log("Autoreply disabled for this user");
                    response.status = 200;
                    response.message = "Autoreply disabled for this user";
                } else {
                    logger.error("From user is not categorised");
                    response.status = 500;
                    response.message = "From user is not categorised";
                }
            } else if (online === true){
                logger.log("User is online. autoreply not needed");
                response.status = 200;
                response.message = "User is online. autoreply not needed";
            } else {
                logger.error("Invalid online value: ", online);
                response.status = 500;
                response.message = `Invalid online value: ${online}`;
            }
            return response;
        })
        .catch((e) => {
            logger.error('error: ',e)
            return e;
        });
}