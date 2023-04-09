package com.achat.app.services;

import com.achat.app.utils.Utils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

public class UserService {
    public static UserService userService;
    public FirebaseService fbService;
    private UserService() {
        this.fbService = FirebaseService.getInstance();
        this.fbService.authentication.signOut();
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
        }
    }

    public void goOffline() {
        if (this.isUserLoggedIn()) {
            this.fbService.userDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    public FirebaseUser getCurrentUser() {
        return this.fbService.authentication.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return this.fbService.authentication.getCurrentUser() != null;
    }

    public void goOnline() {
        if (this.isUserLoggedIn()) {
            this.fbService.userDatabase.child("online").setValue(true);
        }
    }
}
