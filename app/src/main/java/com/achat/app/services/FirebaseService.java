package com.achat.app.services;
import com.achat.app.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService {
    private static FirebaseService firebaseServices;

    public FirebaseDatabase firebaseDatabase;
    public FirebaseAuth authentication;

    public DatabaseReference currentUserDatabase;
    public DatabaseReference messageNotificationsDB;

    public String uid = "";

    private FirebaseService() {
        this.getAuthentication();
        this.getFirebaseDatabase();
        this.firebaseDatabase.setPersistenceEnabled(true);
    }
    public static FirebaseService getInstance() {
        if (!Utils.isTruthy(FirebaseService.firebaseServices)) {
            FirebaseService.firebaseServices = new FirebaseService();
            FirebaseService.firebaseServices.getUid(true);
        }
        return FirebaseService.firebaseServices;
    }

    public String getUid(boolean force) {
        if (force || !Utils.isTruthy(this.uid)) {
            this.uid = this.authentication.getCurrentUser().getUid();
        }
        return this.uid;
    }

    public FirebaseAuth getAuthentication(boolean force) {
        if ((!Utils.isTruthy((FirebaseService.firebaseServices.authentication))) || force) {
            FirebaseService.firebaseServices.getAuthentication();
        }
        return FirebaseService.firebaseServices.authentication;
    }

    public DatabaseReference getCurrentUserDatabase(boolean force) {
        if (!Utils.isTruthy(this.currentUserDatabase) || force) {
            this.currentUserDatabase = this.getFirebaseDatabase().getReference()
                    .child("Users")
                    .child(this.getUid(true));
        }
        return this.currentUserDatabase;
    }

    public DatabaseReference getUserDatabaseRef(String userId) {
        return this.firebaseDatabase.getReference()
                .child("Users")
                .child(userId);
    }

    public DatabaseReference getRootRef() {
        return this.firebaseDatabase.getReference();
    }

    public DatabaseReference getMessageNotificationsDB(boolean force) {
        if (!Utils.isTruthy(this.messageNotificationsDB) || force) {
            this.messageNotificationsDB = this.getFirebaseDatabase().getReference()
                    .child("MessageNotifications");
        }
        return this.messageNotificationsDB;
    }

    public DatabaseReference getMessagesDbRef(String userId1, String userId2) {
        return this.firebaseDatabase.getReference()
                .child("messages")
                .child(userId1)
                .child(userId2);
    }

    private FirebaseAuth getAuthentication() {
        this.authentication = FirebaseAuth.getInstance();
        return this.authentication;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        return this.firebaseDatabase;
    }

    public Task updateTrainData(HashMap keyMessageMap) {
        return this.currentUserDatabase.child("auto_reply_data").setValue(keyMessageMap);
    }

    // gets reference of current user auto reply collection
    public DatabaseReference getAutoreplyDataRef() {
        return this.currentUserDatabase.child("auto_reply_data");
    }

    // gets default message reference from autoreply collection
    public DatabaseReference getDefaultMessageRef() {
        return this.getAutoreplyDataRef().child("default_message");
    }

    // ToDo replace getToken with getInstanceId
    public String getDeviceToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public DatabaseReference getCurrentUserFriendRequestsDB() {
        return this.getFirebaseDatabase().getReference().child("Friend_req").child(this.getUid(false));
    }

    public DatabaseReference getAllUserFriendRequestsDB() {
        return this.getFirebaseDatabase().getReference().child("Friend_req");
    }

    public DatabaseReference getUsersDb() {
        return this.getFirebaseDatabase().getReference().child("Users");
    }

    public DatabaseReference getFriendsDb() {
        return this.getFirebaseDatabase().getReference().child("Friends");
    }

    public DatabaseReference getCurrentUserFriendsDb() {
        return this.getFriendsDb().child(this.getUid(false));
    }

    public void deleteChat(String userId) {
        DatabaseReference chatRef = this.getMessagesDbRef(this.getUid(false), userId);
        chatRef.setValue(null);
    }
}
