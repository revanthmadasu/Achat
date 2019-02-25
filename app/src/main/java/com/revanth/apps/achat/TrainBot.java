package com.revanth.apps.achat;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrainBot extends AppCompatActivity {
    private TextInputEditText keysInput;
    private TextInputEditText messageInput;
    private Button addData;
    private DatabaseReference mCurrentUserDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUsetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_bot);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUsetId=mAuth.getUid();
        keysInput=(TextInputEditText)findViewById(R.id.bot_keys_input);
        messageInput=(TextInputEditText)findViewById(R.id.bot_messages_input);
        addData=(Button)findViewById(R.id.bot_add_button);

        mCurrentUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUsetId);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        StringBuilder keysInDb=new StringBuilder(dataSnapshot.child("keys").getValue().toString());
                        StringBuilder messagesInDb=new StringBuilder(dataSnapshot.child("responses").getValue().toString());
                        StringBuilder associations=new StringBuilder(dataSnapshot.child("associations").getValue().toString());

                        String[] keysInDbArray=keysInDb.toString().split(",");
                        String[] responseMessagesInDbArray=messagesInDb.toString().split(";;;");

                        String keysInputString=keysInput.getText().toString();
                        String responseInputMessage=messageInput.getText().toString();

                        Log.d("revaa keys",keysInDbArray.toString());
                        Log.d("revaa responses",messagesInDb.toString());
                        Log.d("revaa associations",associations.toString());

                        int messageMatchedIndex=-1;
                        String[] keysInputArray=keysInputString.split(",");
                        for(int i=0;i<responseMessagesInDbArray.length;++i)
                        {
                            if(responseInputMessage.equalsIgnoreCase(responseMessagesInDbArray[i]))
                            {
                                messageMatchedIndex=i;
                                break;
                            }
                        }
                        if(messageMatchedIndex==-1)
                        {
                            messagesInDb.append(";;;");
                            messagesInDb.append(responseInputMessage);
                        }
                        messageMatchedIndex=responseMessagesInDbArray.length;
                        int newKeyIndex=keysInDbArray.length;
                        Log.d("revaa bot","newKeyIndex is"+newKeyIndex);
                        for(int i=0;i<keysInputArray.length;i++)
                        {
                            int matchedKeyIndex=-1;
                            for(int j=0;j<keysInDbArray.length;++j)
                            {
                                if(keysInputArray[i].equalsIgnoreCase(keysInDbArray[j]))
                                {
                                    matchedKeyIndex=j;
                                    Log.d("revaa bot","key found in db matched index = "+matchedKeyIndex);
                                    associations.append(";"+matchedKeyIndex+":"+messageMatchedIndex);
                                }
                            }
                            if(matchedKeyIndex==-1)
                            {
                                Log.d("revaa bot","key not found in db new index = "+newKeyIndex);
                                keysInDb.append(","+keysInputArray[i]);
                                associations.append(";"+newKeyIndex+":"+messageMatchedIndex);
                                ++newKeyIndex;
                            }
                        }
                        mCurrentUserDatabase.child("keys").setValue(keysInDb.toString());
                        mCurrentUserDatabase.child("responses").setValue(messagesInDb.toString());
                        mCurrentUserDatabase.child("associations").setValue(associations.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
    }
}
