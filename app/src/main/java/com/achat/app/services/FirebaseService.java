package com.achat.app.services;
import com.achat.app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {
    private static FirebaseService firebaseServices;

    public FirebaseDatabase firebaseDatabase;
    public FirebaseAuth authentication;

    public DatabaseReference currentUserDatabase;
    public DatabaseReference messageNotificationsDB;

    private FirebaseService() {
        this.getAuthentication();
        this.getFirebaseDatabase();
        this.firebaseDatabase.setPersistenceEnabled(true);
    }
    public static FirebaseService getInstance() {
        if (!Utils.isTruthy(FirebaseService.firebaseServices)) {
            FirebaseService.firebaseServices = new FirebaseService();
        }
        return FirebaseService.firebaseServices;
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
                    .child(this.authentication.getCurrentUser().getUid());
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
}
