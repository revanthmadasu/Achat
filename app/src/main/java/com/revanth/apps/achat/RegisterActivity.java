package com.revanth.apps.achat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout nDisplayName;
    private TextInputLayout nEmail;
    private TextInputLayout nPassword;
    private Button nCreateBtn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create an Account");
        nDisplayName= (TextInputLayout) findViewById(R.id.textInputLayout4);
        nEmail= (TextInputLayout) findViewById(R.id.textInputLayout6);
        nPassword= (TextInputLayout) findViewById(R.id.textInputLayout7);
        nCreateBtn= (Button) findViewById(R.id.reg_create_btn);

        nCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name= nDisplayName.getEditText().getText().toString();
                String email= nEmail.getEditText().getText().toString().trim();
                String password= nPassword.getEditText().getText().toString().trim();
                register_user(display_name,email,password);

            }
        });
    }

    private void register_user(String display_name, String email, String password) {
        Toast.makeText(RegisterActivity.this,"email is "+email+" password is "+password,Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent mainIntent= new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            //  Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(RegisterActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                            Toast.makeText(RegisterActivity.this,nEmail.getEditText().getText().toString(),Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }
}