package com.revanth.apps.achat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    //private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLogin_btn;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //  mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");


        mLoginProgress = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLogin_btn = (Button) findViewById(R.id.login_btn);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email="",password="";
                try {
                    email = mLoginEmail.getEditText().getText().toString().trim();
                    password = mLoginPassword.getEditText().getText().toString().trim();
                }
                catch(NullPointerException e)
                {
                    Toast.makeText(LoginActivity.this,"User Name or password cannot be empty",Toast.LENGTH_SHORT).show();
                }
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);

                }

            }
        });


    }



    private void loginUser(String email, final String password) {

        Toast.makeText(LoginActivity.this,"inside loginUser",Toast.LENGTH_LONG).show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this,"Task is successful",Toast.LENGTH_LONG).show();
                    mLoginProgress.dismiss();

                    String TAG="LoginActivity";
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);

                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    /*Toast.makeText(LoginActivity.this,"User = "+current_user_id+" device token = "+deviceToken,Toast.LENGTH_LONG).show();
                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this,"Successful",Toast.LENGTH_LONG).show();
                            startActivity(mainIntent);
                            finish();
                        }
                    });
                     */



                } else {

                    mLoginProgress.hide();

                    String task_result = task.getException().toString();

                    Toast.makeText(LoginActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show();
                    Toast.makeText(LoginActivity.this,"password is "+password, Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    private void updateUI(FirebaseUser user)
    {
        if(user!=null)
        {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Error : User cannot be null" , Toast.LENGTH_LONG).show();
        }
    }
}
