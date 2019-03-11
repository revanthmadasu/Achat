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
    private String mCurrentUserId;
    private String mReceiverUserId;
    private String message;
    private DatabaseReference mMessageNotificationDatabase,mRootRef;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("revaa scheduled","Alarm triggered");
        mAuth=FirebaseAuth.getInstance();

        mCurrentUserId=mAuth.getCurrentUser().getUid();

        mReceiverUserId=intent.getStringExtra("receiverId");

        mRootRef=FirebaseDatabase.getInstance().getReference();
        mMessageNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("MessageNotifications");

        message=intent.getStringExtra("message");

        String current_user_ref = "messages/" + mCurrentUserId + "/" + mReceiverUserId;
        String chat_user_ref = "messages/" + mReceiverUserId + "/" + mCurrentUserId;

        String messagePushId= mRootRef.child("messages").child(mCurrentUserId).child(mReceiverUserId).push().getKey();

        Map messageMap = new HashMap();
        messageMap.put("message", message);
        messageMap.put("seen", false);
        messageMap.put("type", "text");
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", mCurrentUserId);

        Map messageUserMap = new HashMap();
        messageUserMap.put(current_user_ref + "/" + messagePushId, messageMap);
        messageUserMap.put(chat_user_ref + "/" + messagePushId, messageMap);

        mRootRef.child("Chat").child(mReceiverUserId).child(mCurrentUserId).child("seen").setValue(false);
        mRootRef.child("Chat").child(mReceiverUserId).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);
        mRootRef.child("Chat").child(mReceiverUserId).child(mCurrentUserId).child("lastMessageKey").setValue(messagePushId);


        mRootRef.child("Chat").child(mCurrentUserId).child(mReceiverUserId).child("seen").setValue(false);
        mRootRef.child("Chat").child(mCurrentUserId).child(mReceiverUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);
        mRootRef.child("Chat").child(mCurrentUserId).child(mReceiverUserId).child("lastMessageId").setValue(messagePushId);

        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError != null) {

                    Log.d("CHAT_LOG in scheduled", databaseError.getMessage().toString());

                } else {
                    Map messageNotificationMap = new HashMap();
                    messageNotificationMap.put("from", mCurrentUserId);
                    messageNotificationMap.put("type", "message");
                    DatabaseReference messageNotificationDatabase = mMessageNotificationDatabase.child(mReceiverUserId).push();
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

    }
}
