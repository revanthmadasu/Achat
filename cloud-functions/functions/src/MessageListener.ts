import { logger, database} from "firebase-functions";

export const onMessageSent = database.ref('MessageNotifications/{user_id}/{notification_id}').onWrite((change, context) => {
    logger.log('inside onMessageSent ', 'user id: ', context.params.user_id, ' notification_id: ', context.params.notification_id);
    console.log(`inside onMessageSent, user id: , ${context.params.user_id}, notification_id: ${context.params.notification_id}`);
    return "Successfully called"
});