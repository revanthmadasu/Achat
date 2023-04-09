package com.achat.app.services;
import com.achat.app.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {
    private static FirebaseService firebaseServices;
    public DatabaseReference userDatabase;
    public FirebaseDatabase firebaseDatabase;
    public FirebaseAuth authentication;
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

    public DatabaseReference getUserDatabase(boolean force) {
        if (!Utils.isTruthy(this.userDatabase) || force) {
            this.userDatabase = this.getFirebaseDatabase().getReference()
                    .child("Users")
                    .child(this.authentication.getCurrentUser().getUid());
        }
        return this.userDatabase;
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
