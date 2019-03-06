package com.revanth.apps.achat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private DatabaseReference mUserRef;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    //private Toolbar nToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null)
        {
            mUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

       mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        //nToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
       //setSupportActionBar(nToolbar);
      // getSupportActionBar().setTitle("AChat");
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("revaa","application started");
        if(currentUser==null)
        {
           sendToStart();
        }
        else
        {
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("revaa","application stopped");
         super.onStop();
        //if(currentUser!=null) {
          //  mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            //mUserRef.child("lastSeen")

       // }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        MainActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout_btn)
        {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }
        if(item.getItemId() == R.id.main_settings_btn){


            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId()==R.id.main_all_btn)
        {
            Intent settingsIntent=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(settingsIntent);
        }
        if(item.getItemId()==R.id.main_train_bot_btn)
        {
            Intent trainBotIntent=new Intent(MainActivity.this,TrainBot.class);
            startActivity(trainBotIntent);
        }
        return true;
    }
}