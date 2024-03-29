package com.revanth.apps.achat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.achat.app.services.FirebaseService;
import com.achat.app.services.UserService;

import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLogin_btn;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;

    private FirebaseService fbService;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.fbService = FirebaseService.getInstance();
        this.userService = UserService.getInstance();

        this.mAuth = this.fbService.getAuthentication(false);

         mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("LOGIN");

        mLoginProgress = new ProgressDialog(this);

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
        this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this,"Successfully logged In",Toast.LENGTH_LONG).show();
                    mLoginProgress.dismiss();

                    FirebaseUser user = userService.getCurrentUser(true);
                    mUserDatabase = fbService.getCurrentUserDatabase(true);
                    fbService.getAuthentication(true);

                    updateUI(user);

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();//getToken() is depreciated method. have to work on it

                    mUserDatabase.child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Log.d("Login Activity", "Successfully added device token");
                            startActivity(mainIntent);
                            finish();
                        }
                    });
                } else {
                    mLoginProgress.hide();

                    String task_result = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Error : " + task_result, Toast.LENGTH_LONG).show();
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
