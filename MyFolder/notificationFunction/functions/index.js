'use strict'

const functions = require('firebase-functions');
const admin=require('firebase-admin');

admin.initializeApp(functions.config().firebase);

const db=admin.database()

exports.sendNotification=functions.database.ref('/Notifications/{user_id}/{notification_id}').onWrite((change,context)=>{

    const user_id=context.params.user_id;
    const notification_id = context.params.notification_id;

    if (!change.after.val()) {
        return console.log('A notification has been deleted from database: ', notification_id);
    }

    const fromUser=admin.database().ref(`/Notifications/${user_id}/${notification_id}`).once('value');
    return fromUser.then(fromUserResult=>{
        const from_user_id=fromUserResult.val().from;

        const userQuery=admin.database().ref(`Users/${from_user_id}/name`).once('value');
        const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

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

            return admin.messaging().sendToDevice(token_id,payload).then(response=>
            {
                return console.log('Request notification sent to '+userName);
            });
        }); 
    });
});
// current_user_id refers to user_id to which message has been sent
exports.sendmessageNotification=functions.database.ref('/MessageNotifications/{current_user_id}/{message_notification_id}').onWrite((change,context)=>{
    const current_user_id=context.params.current_user_id;
    const message_notification_id=context.params.message_notification_id;
    //if(!change.after.val())
    //{

    //}
    const fromUser=admin.database().ref(`/MessageNotifications/${current_user_id}/${message_notification_id}`).once('value');

    
    return fromUser.then(fromUserResult=>{
        const from_user_id=fromUserResult.val().from;
   
        const userQuery=admin.database().ref(`Users/${from_user_id}/name`).once('value');
        const deviceToken = admin.database().ref(`/Users/${current_user_id}/device_token`).once('value');
    
        return Promise.all([userQuery,deviceToken]).then(result=>{
            const userName=result[0].val();
            const token_id=result[1].val();
    
            const payload={
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
    
            return admin.messaging().sendToDevice(token_id,payload).then(response=>
            {
                const keysQuery=admin.database().ref(`Users/${current_user_id}/keys`).once('value')
                const messagesQuery=admin.database().ref(`Users/${current_user_id}/responses`).once('value')
                const onlineQuery=admin.database().ref(`Users/${current_user_id}/online`).once('value')
                const associationsQuery=admin.database().ref(`Users/${current_user_id}/associations`).once('value')
                const familyCatQuery=admin.database().ref(`Users/${current_user_id}/family_cat`).once('value')
                const friendsCatQuery=admin.database().ref(`Users/${current_user_id}/friends_cat`).once('value')
                const defaultMsgQuery=admin.database().ref(`Users/${current_user_id}/default_msg`).once('value')

                const lastMessageIdQuery=admin.database().ref(`Chat/${from_user_id}/${current_user_id}`).once('value')
                return Promise.all([keysQuery,messagesQuery,onlineQuery,associationsQuery,lastMessageIdQuery,familyCatQuery,friendsCatQuery,defaultMsgQuery]).then(result=>{
                    const userKeys=result[0].val()
                    const userResponses=result[1].val()
                    const online=result[2].val()
                    const userAssociations=result[3].val()
                    const lastMessageId=result[4].val().lastMessageId

                    const familyCatIds=result[5].val().split(";;;")
                    const friendsCatIds=result[6].val().split(";;;")

                    const defaultResponseMessage=result[7].val()
                    console.log('default msg is '+defaultResponseMessage)
                    /*
                         category    value
                        * friends       1
                        * family        2
                        * both          3
                        * none          0
                    */ 
                    var category=0
                    var msg=""
                    for(const i in friendsCatIds)   
                    {
                        msg=msg.concat(friendsCatIds[i])
                        msg=msg.concat(" ")
                        if(friendsCatIds[i]===from_user_id)
                        {
                            console.log("success")
                            category=category+1
                        }
                    }

                    for(const i in familyCatIds)
                    {
                        if(familyCatIds[i]===from_user_id)
                            category=category+2
                    }
                    console.log('category is '+category+' friendsIds '+msg+' current id '+from_user_id)
                    var messageDbRef_from=db.ref().child("messages/"+from_user_id+"/"+current_user_id)
                    var messageDbRef_current=db.ref().child("messages/"+current_user_id+"/"+from_user_id)
                    const lastMessageQuery=db.ref(`messages/${from_user_id}/${current_user_id}/${lastMessageId}`).once('value')
                    return Promise.all([lastMessageQuery]).then(result=>{
                        var receivedMessage=result[0].val().message
                        
                        if(online!==true)
                    {
                        var individualPairs=userAssociations.split(";")
                        var responsesInDbArray=userResponses.split(";;;")

                        var keysInDbArray=userKeys.split(",")

                        var receivedMessageKeys=receivedMessage.split(" ")

                        var matchedKeys=[]
                        for(const i in receivedMessageKeys)
                        {
                            for(const j in keysInDbArray)
                            {
                                if(receivedMessageKeys[i]===keysInDbArray[j])
                                {
                                    matchedKeys.push(j)
                                }
                            }
                        }
                        
                        var matchedResponses=[]
                        msg=""
                        for(const pairIndex in individualPairs)
                        {
                            let keyResponsePair=individualPairs[pairIndex].split(":")
                            for(const keyIndex in matchedKeys)
                            {
                                msg=msg.concat(category+':'+keyResponsePair[2]+' ')
                                if(matchedKeys[keyIndex]===keyResponsePair[0]&&(parseInt(keyResponsePair[2],10)===category||category===3))
                                    matchedResponses.push(keyResponsePair[1])
                            }
                        }
                        console.log(msg)
                        var generatedResponseMessage=""
                        for(const responseIndex in matchedResponses)
                        {
                            generatedResponseMessage=generatedResponseMessage.concat(responsesInDbArray[matchedResponses[responseIndex]])
                        }
                        if(generatedResponseMessage==="")
                        {
                            messageDbRef_current.push().set(
                                {
                                    message:defaultResponseMessage,
                                    seen:false,
                                    type:"text",
                                    time:admin.database.ServerValue.TIMESTAMP,
                                    from:current_user_id
                                })
                                messageDbRef_from.push().set(
                                    {
                                        message:defaultResponseMessage,
                                        seen:false,
                                        type:"text",
                                        time:admin.database.ServerValue.TIMESTAMP,
                                        from:current_user_id
                                    }
                                )
                                console.log('default message sent to '+userName)
                            return 
                        }
                        generatedResponseMessage=generatedResponseMessage.concat("-auto reply");
                        messageDbRef_current.push().set(
                            {
                                message:generatedResponseMessage,
                                seen:false,
                                type:"text",
                                time:admin.database.ServerValue.TIMESTAMP,
                                from:current_user_id
                            }
                        )
                        messageDbRef_from.push().set(
                            {
                                message:generatedResponseMessage,
                                seen:false,
                                type:"text",
                                time:admin.database.ServerValue.TIMESTAMP,
                                from:current_user_id
                            }
                        )
                    }
                    return console.log('Message notification sent to '+userName+' online = '+online+' keys = '+userKeys+' last message is '+receivedMessage+" generated response messages is "+generatedResponseMessage);
                    })
                    
                })
                //return console.log('Message notification sent to '+userName);
            });
        }); 
    });
});
