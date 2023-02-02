package com.revanth.apps.achat;

import android.content.DialogInterface;
//import android.support.annotation.NonNull;
import androidx.annotation.NonNull;
//import android.support.design.widget.TextInputEditText;

import com.google.android.material.textfield.TextInputEditText;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrainBot extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextInputEditText keysInput;
    private TextInputEditText messageInput;
    private Button addData;
    private DatabaseReference mCurrentUserDatabase;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private String mCurrentUsetId;
    private Spinner spinner;
    private Button mDefaultbtn;
    private TextInputEditText mDefaultMsg;
    private static final String[] paths = {"Friends", "Family","Both"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_bot);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUsetId=mAuth.getUid();
        keysInput=(TextInputEditText)findViewById(R.id.bot_keys_input);
        messageInput=(TextInputEditText)findViewById(R.id.bot_messages_input);
        addData=(Button)findViewById(R.id.bot_add_button);
        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("aChat - Add Automatic Replies");
        spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TrainBot.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        mDefaultbtn=(Button) findViewById(R.id.bot_default_button);
        mDefaultMsg=(TextInputEditText) findViewById(R.id.bot_default_input);
        mDefaultbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String dMessage=mDefaultMsg.getText().toString();
                mCurrentUserDatabase.child("default_msg").setValue(dMessage);
            }

        });

        mCurrentUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUsetId);
        mDefaultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               mDefaultMsg.setText(" ");
                AlertDialog alertDialog =  new AlertDialog.Builder(TrainBot.this).create();
                alertDialog.setTitle("aChat Automatic Replies");
                alertDialog.setMessage("Default Message has been set");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                                  }

                                       });
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
                        int selectedCategoryPosition=spinner.getSelectedItemPosition()+1;
                        for(int i=0;i<keysInputArray.length;i++)
                        {
                            int matchedKeyIndex=-1;
                            for(int j=0;j<keysInDbArray.length;++j)
                            {
                                if(keysInputArray[i].trim().equalsIgnoreCase(keysInDbArray[j]))
                                {
                                    matchedKeyIndex=j;
                                    Log.d("revaa bot","key found in db matched index = "+matchedKeyIndex);
                                    associations.append(";"+matchedKeyIndex+":"+messageMatchedIndex+":"+selectedCategoryPosition);
                                }
                            }
                            if(matchedKeyIndex==-1)
                            {
                                Log.d("revaa bot","key not found in db new index = "+newKeyIndex);
                                keysInDb.append(","+keysInputArray[i]);
                                associations.append(";"+newKeyIndex+":"+messageMatchedIndex+":"+selectedCategoryPosition);
                                ++newKeyIndex;
                            }
                        }
                        mCurrentUserDatabase.child("keys").setValue(keysInDb.toString());
                        mCurrentUserDatabase.child("responses").setValue(messagesInDb.toString());
                        mCurrentUserDatabase.child("associations").setValue(associations.toString());
                        messageInput.setText(" ");
                        keysInput.setText(" ");
                        AlertDialog alertDialog = new AlertDialog.Builder(TrainBot.this).create();
                        alertDialog.setTitle("aChat Automatic Replies");
                        alertDialog.setMessage("Automatic Reply added successfully");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                break;
            case 1:
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
