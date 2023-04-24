package com.revanth.apps.achat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.achat.app.model.User;
import com.achat.app.services.FirebaseService;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout nDisplayName;
    private TextInputLayout nEmail;
    private TextInputLayout nPassword;
    private Button nCreateBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mRegProgress;
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.firebaseService = FirebaseService.getInstance();

        setContentView(R.layout.activity_register);
        Toolbar myToolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(myToolBar);
        mAuth = FirebaseAuth.getInstance();
        mRegProgress = new ProgressDialog(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create an Account");
        nDisplayName= (TextInputLayout) findViewById(R.id.textInputLayout4);
        nEmail= (TextInputLayout) findViewById(R.id.textInputLayout6);
        nPassword= (TextInputLayout) findViewById(R.id.textInputLayout7);
        nCreateBtn= (Button) findViewById(R.id.reg_create_btn);

        nCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name= nDisplayName.getEditText().getText().toString();
                String Email= nEmail.getEditText().getText().toString().trim();
                String Password= nPassword.getEditText().getText().toString().trim();
                register_user(display_name,Email,Password);
            }
        });
    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDatabase = firebaseService.getCurrentUserDatabase(true);
                    User user = new User(display_name);

                    mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Successfully created account",Toast.LENGTH_SHORT).show();
                                mRegProgress.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.e("RegisterActivity", "Unable to create user in firebase");
                            Toast.makeText(RegisterActivity.this, "Cannot create user.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}