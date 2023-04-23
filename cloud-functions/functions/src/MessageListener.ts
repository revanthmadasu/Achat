import { logger, database} from "firebase-functions";
import { database as admin_db, messaging } from "firebase-admin";
import { AchatFirebaseApp } from "./App";
// import { DataSnapshot } from "firebase-functions/v1/database";
import { PubSubService } from "./PubSub";
import { CloudTasksService } from "./CloudTasks";

AchatFirebaseApp.getApp();
// const pubSubService = PubSubService.getPubSubService();
const cloudTasksService = CloudTasksService.getCloudTasksService();
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

        return Promise.all([userQuery, deviceToken]).then((result: any) => {
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
            const messageDetails = {
                current_user_id,
                from_user_id,
            };
            cloudTasksService.addTaskToQueue(messageDetails).then(response => logger.info(`task registered response: ${JSON.stringify(response)}`));
            // pubSubService.publishMessage(messageDetails).then((messageId: string) => logger.log(messageId));
            return messaging().sendToDevice(token_id,payload);
        });
    });
});
