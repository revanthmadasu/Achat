package com.revanth.apps.achat;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrainBot extends AppCompatActivity {
    private TextInputLayout keysInput;
    private TextInputLayout messageInput;
    private Button addData;
    private DatabaseReference mUserDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_bot);
        keysInput=(TextInputLayout)findViewById(R.id.bot_keys_input);
        messageInput=(TextInputLayout)findViewById(R.id.bot_messages_input);
        addData=(Button)findViewById(R.id.bot_add_button);

        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keys=keysInput.getEditText().getText().toString();
                String messages=messageInput.getEditText().getText().toString();

            }
        });
    }
}
