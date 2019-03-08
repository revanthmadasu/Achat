package com.revanth.apps.achat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class AlarmActionClass extends BroadcastReceiver {
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private String mReceiverUser;
    private DatabaseReference mMessageDatabase,mMessageNotificationDatabase;
    @Override
    public void onReceive(Context context, Intent intent) {
        mAuth=FirebaseAuth.getInstance();

        mCurrentUser=mAuth.getCurrentUser().getUid();
        mReceiverUser=intent.getStringExtra("receiverId");

        mMessageDatabase=FirebaseDatabase.getInstance().getReference().child("messages");
        mMessageNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("MessageNotifications");

        String messagePushId= mMessageDatabase.child(mCurrentUser).child(mReceiverUser).push().getKey();





        /*DatabaseReference user_message_push = mRootRef.child("messages")
                .child(mCurrentUserId).child(mChatUser).push();
        String push_id = user_message_push.getKey();

        Map messageMap = new HashMap();
        messageMap.put("message", message);
        messageMap.put("seen", false);
        messageMap.put("type", "text");
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", mCurrentUserId);

        Map messageUserMap = new HashMap();
        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

        mChatMessageView.setText("");

        mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
        mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);
        mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("lastMessageKey").setValue(push_id);


        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(false);
        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);
        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("lastMessageId").setValue(push_id);

        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError != null) {

                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                } else {
                    Map messageNotificationMap = new HashMap();
                    messageNotificationMap.put("from", mCurrentUserId);
                    messageNotificationMap.put("type", "message");
                    DatabaseReference messageNotificationDatabase = mMessageNotificationsDatabase.child(mChatUser).push();
                    messageNotificationDatabase.updateChildren(messageNotificationMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("ChatNotificationError", databaseError.getMessage());
                            }
                        }
                    });
                }

            }
        });
        */
    }
}
