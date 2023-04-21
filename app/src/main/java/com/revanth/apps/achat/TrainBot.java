package com.revanth.apps.achat;

import android.content.DialogInterface;
import androidx.annotation.NonNull;

import com.achat.app.services.FirebaseService;
import com.achat.app.utils.Utils;
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
                fbService.getDefaultMessageRef().setValue(dMessage);
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
                        // binding input values
                        String keysInputString=keysInput.getText().toString();
                        String responseInputMessage=messageInput.getText().toString();

                        fbService.getAutoreplyDataRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String, HashMap<String, String>> autoReplyMap = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                                if (!Utils.isTruthy(autoReplyMap)) {
                                    autoReplyMap = new HashMap<String, HashMap<String, String>>();
                                }

                                HashMap<String, String> keyMessagesMap = autoReplyMap.get(selectedCategory);
                                if (!Utils.isTruthy(keyMessagesMap)) {
                                    keyMessagesMap = new HashMap<String, String>();
                                }

                                String[] allKeys = keysInputString.split(",");
                                for (String key: allKeys) {
                                    String trimmed_key = key.trim().toLowerCase();
                                    keyMessagesMap.put(trimmed_key, responseInputMessage);
                                }
                                autoReplyMap.put(selectedCategory, keyMessagesMap);
                                // ToDo - add onComplete Listener
                                fbService.updateTrainData(autoReplyMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("TrainBot", "Fetch autoreply data failed");
                            }
                        });
                        // populating hashmap
                        HashMap<String, HashMap<String, String>> keyMessagesMap = new HashMap<String, HashMap<String, String>>();

                        fbService.updateTrainData(keyMessagesMap);

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
