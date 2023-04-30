package com.revanth.apps.achat;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.achat.app.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputEditText mUserSearchBox;

    private RecyclerView mUsersList;

    private FirebaseRecyclerOptions<Users> options1;
    FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter;

    private DatabaseReference mUsersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        this.mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        this.mUserSearchBox = (TextInputEditText) findViewById(R.id.search_user_textbox);
        this.mUserSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(mUserSearchBox.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("aChat - All Users");
        this.mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList=(RecyclerView)findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);

        this.searchUsers("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter!=null)
            firebaseRecyclerAdapter.startListening();

    }

    protected void onStop()
    {
        if(firebaseRecyclerAdapter!=null)
            firebaseRecyclerAdapter.stopListening();
        super.onStop();
    }

    protected void onResume()
    {
        super.onResume();
        if(firebaseRecyclerAdapter!=null)
            firebaseRecyclerAdapter.startListening();
    }

    private void searchUsers(String userName) {
        Query searchQuery;
        if (Utils.isTruthy(userName)) {
            searchQuery = mUsersDatabase.orderByChild("name").startAt(userName).endAt(userName + "\uf8ff");
        } else {
            searchQuery = mUsersDatabase;
        }
        options1 = new FirebaseRecyclerOptions.Builder<Users>().setQuery(searchQuery,Users.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                Log.d("All UsersActivity",model.toString());
                holder.setDisplayName(model.getName());
                holder.setUserStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image(),getApplicationContext());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent= new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_single_layout,viewGroup,false);
                return new UsersViewHolder(view);
            }

        };

        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        firebaseRecyclerAdapter.startListening();
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }


}
