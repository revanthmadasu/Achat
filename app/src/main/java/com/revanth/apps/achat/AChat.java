package com.revanth.apps.achat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.achat.app.services.FirebaseService;
import com.achat.app.services.UserService;

import com.google.firebase.auth.FirebaseAuth;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class AChat extends Application implements Application.ActivityLifecycleCallbacks{
    public static FirebaseAuth mAuth;
    public UserService userService;
    public FirebaseService fbService;
    @Override
    public void onCreate() {
        super.onCreate();
        this.fbService = FirebaseService.getInstance();
        this.userService = UserService.getInstance();
        registerActivityLifecycleCallbacks(this);

        //Picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d("Achat Main","application created");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("Achat Main","application started");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("Achat Main","application resumed");
        this.userService.goOnline();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("Achat Main","application paused");
        this.userService.goOffline();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("Achat Main","application stopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d("Achat Main","application destroyed");
    }
}
