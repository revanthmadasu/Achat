package com.achat.app.services;

import com.achat.app.utils.Utils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

public class UserService {
    public static UserService userService;
    public FirebaseService fbService;

    private FirebaseUser currentUser;
    public String uid;
    private UserService() {
        this.fbService = FirebaseService.getInstance();
        this.getCurrentUser(true);
    }

    public static UserService getInstance() {
        if (!Utils.isTruthy(UserService.userService)) {
            UserService.userService = new UserService();
        }
        return UserService.userService;
    }

    public void signOut() {
        if (this.isUserLoggedIn()) {
            this.goOffline();
            this.fbService.authentication.signOut();
            this.getCurrentUser(true);
        }
    }

    public void goOffline() {
        if (this.isUserLoggedIn()) {
            this.fbService.currentUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    public FirebaseUser getCurrentUser(boolean force) {
        if (!Utils.isTruthy(this.currentUser) || force) {
            this.currentUser = this.fbService.authentication.getCurrentUser();
            this.uid = this.currentUser.getUid();
        }
        return this.currentUser;
    }

    public boolean isUserLoggedIn() {
        return this.fbService.authentication.getCurrentUser() != null;
    }

    public void goOnline() {
        if (this.isUserLoggedIn()) {
            this.fbService.getCurrentUserDatabase(true).child("online").setValue(true);
        }
    }
}
