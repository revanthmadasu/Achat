package com.revanth.apps.achat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class AChat extends Application implements Application.ActivityLifecycleCallbacks{
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(mAuth.getCurrentUser().getUid());
            /*mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {
                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        }
    }
    public void onStop()
    {
        Log.d("revaa","application stopped");
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d("revaa","application created");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("revaa","application started");

    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("revaa","application resumed");
        if(mAuth.getCurrentUser()!=null)
        mUserDatabase.child("online").setValue(true);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("revaa","application paused");
        if(mAuth.getCurrentUser()!=null)
        mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("revaa","application stopped");

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d("revaa","application destroyed");
    }
}
