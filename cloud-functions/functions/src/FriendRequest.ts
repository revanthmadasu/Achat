import { logger, database} from "firebase-functions";
import { AchatFirebaseApp } from "./App";
import { database as admin_db, messaging } from "firebase-admin";

AchatFirebaseApp.getApp();
const achat_db = admin_db()

export const onFriendRequest = database.ref('/Notifications/{user_id}/{notification_id}').onWrite((change,context)=>{
    logger.log('inside onFriendRequest ', 'user id: ', context.params.user_id, ' notification_id: ', context.params.notification_id);

    const user_id=context.params.user_id;
    const notification_id = context.params.notification_id;

    if (!change.after.val()) {
        logger.log('A notification has been deleted from database: ', notification_id);
    }

    const fromUser=achat_db.ref(`/Notifications/${user_id}/${notification_id}`).once('value');
    return fromUser.then(fromUserResult=>{
        const from_user_id=fromUserResult.val().from;

        const userQuery=achat_db.ref(`Users/${from_user_id}/name`).once('value');
        const deviceToken = achat_db.ref(`/Users/${user_id}/device_token`).once('value');

        return Promise.all([userQuery,deviceToken]).then(result=>{
            const userName=result[0].val();
            const token_id=result[1].val();

            const payload={
                notification: {
                    title:"Friend Request",
                    body:`${userName} has sent you friend request`,
                    icon:"default",
                    click_action:"com.revanth.apps.achat_TARGET_NOTIFICATION"
                },
                data: {
                    from_user_id : from_user_id
                }
            };

            return messaging().sendToDevice(token_id,payload).then(response=>
            {
                logger.log('Request notification sent from '+userName);
            });
        }); 
    });
});