package com.revanth.apps.achat;

import android.content.DialogInterface;
import androidx.annotation.NonNull;

import com.achat.app.services.FirebaseService;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TrainBot extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextInputEditText keysInput;
    private TextInputEditText messageInput;
    private Button addData;
    private DatabaseReference mCurrentUserDatabase;
    private FirebaseService fbService;
    private Toolbar mToolbar;
    private Spinner spinner;
    private Button mDefaultbtn;
    private TextInputEditText mDefaultMsg;
    private static final String[] paths = {"Friends", "Family","Both"};
    private String selectedCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_bot);

        this.fbService = FirebaseService.getInstance();

        keysInput=(TextInputEditText)findViewById(R.id.bot_keys_input);
        messageInput=(TextInputEditText)findViewById(R.id.bot_messages_input);
        addData=(Button)findViewById(R.id.bot_add_button);
        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        // mToolbar.setTitle("aChat - Add Automatic Replies");
        setSupportActionBar(mToolbar);
        // getSupportActionBar().setTitle("aChat - Add Automatic Replies");
        // category dropdown
        spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TrainBot.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        mDefaultbtn=(Button) findViewById(R.id.bot_default_button);
        mDefaultMsg=(TextInputEditText) findViewById(R.id.bot_default_input);

        mCurrentUserDatabase = this.fbService.getCurrentUserDatabase(false);

        mDefaultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dMessage=mDefaultMsg.getText().toString();
                mCurrentUserDatabase.child("default_msg").setValue(dMessage);

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

                        // binding input values
                        String keysInputString=keysInput.getText().toString();
                        String responseInputMessage=messageInput.getText().toString();

                        fbService.getAutoreplyDataRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, HashMap<String, String>> keyMessagesMap = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                                String[] allKeys = keysInputString.split(",");
                                keyMessagesMap.put(selectedCategory, new HashMap<String, String>());
                                for (String key: allKeys) {
                                    String trimmed_key = key.trim().toLowerCase();
                                    keyMessagesMap.get(selectedCategory).put(trimmed_key, responseInputMessage);
                                }
                                // ToDo - add onComplete Listener
                                fbService.updateTrainData(keyMessagesMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("TrainBot", "Fetch autoreply data failed");
                            }
                        });
                        // populating hashmap
                        HashMap<String, HashMap<String, String>> keyMessagesMap = new HashMap<String, HashMap<String, String>>();

                        fbService.updateTrainData(keyMessagesMap);

                        Log.d("TrainBot: keys = ",keysInDbArray.toString());
                        Log.d("TrainBot: responses = ",messagesInDb.toString());
                        Log.d("TrainBot:associations=",associations.toString());

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
                        Log.d("TrainBot","newKeyIndex is"+newKeyIndex);
                        int selectedCategoryPosition=spinner.getSelectedItemPosition()+1;
                        for(int i=0;i<keysInputArray.length;i++)
                        {
                            int matchedKeyIndex=-1;
                            for(int j=0;j<keysInDbArray.length;++j)
                            {
                                if(keysInputArray[i].trim().equalsIgnoreCase(keysInDbArray[j]))
                                {
                                    matchedKeyIndex=j;
                                    Log.d("TrainBot","key found in db matched index = "+matchedKeyIndex);
                                    associations.append(";"+matchedKeyIndex+":"+messageMatchedIndex+":"+selectedCategoryPosition);
                                }
                            }
                            if(matchedKeyIndex==-1)
                            {
                                Log.d("TrainBot","key not found in db new index = "+newKeyIndex);
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
                this.selectedCategory = "friends";
                break;
            case 1:
                this.selectedCategory = "family";
                break;
            case 2:
                this.selectedCategory = "both";
                break;
            default:
                this.selectedCategory = "unknown";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
