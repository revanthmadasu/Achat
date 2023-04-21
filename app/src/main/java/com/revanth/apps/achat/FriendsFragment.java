package com.revanth.apps.achat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.achat.app.model.User;
import com.achat.app.services.FirebaseService;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */

public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase,mUsersDatabase;
    private FirebaseAuth mAuth;

    private FirebaseService fbService;
    private String mCurrent_user_id;

    private View mMainView;
    private FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends,FriendsViewHolder> mfriendsRecyclerViewAdapter;
    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth=FirebaseAuth.getInstance();
        this.fbService = FirebaseService.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();

//        mFriendsDatabase=FirebaseDatabase.getInstance().getReference()
//                .child("Friends").child(mCurrent_user_id);
        this.mFriendsDatabase = this.fbService.getCurrentUserFriendsDb();
        this.mFriendsDatabase.keepSynced(true);

//        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        this.mUsersDatabase = this.fbService.getUsersDb();
        this.mUsersDatabase.keepSynced(true);

        mFriendsList=(RecyclerView)mMainView.findViewById(R.id.friends_list);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        options=new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFriendsDatabase,Friends.class).build();
        mfriendsRecyclerViewAdapter=new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                holder.setDate(model.getDate());

                final String list_user_id=getRef(position).getKey();


                mUsersDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //String username1=dataSnapshot.child("name").get
                        Log.d("FriendsFragment: mUser",dataSnapshot.child(list_user_id).child("name").getValue().toString());
                        final String username=dataSnapshot.child(list_user_id).child("name").getValue().toString();
                        String thumb_image=dataSnapshot.child(list_user_id).child("thumb_image").getValue().toString();
                        holder.setName(username);
                        holder.setUserImage(thumb_image,getContext());

                        String userOnline=dataSnapshot.child(list_user_id).child("online").getValue().toString();
                        holder.setUserOnline(userOnline);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


             holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                 @Override
                 public boolean onLongClick(View v) {

                     CharSequence options[] = new CharSequence[]{"Open Profile","auto-reply: Friend","auto-reply: Family","auto-reply: Both","auto-reply: None"};

                     final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                     builder.setTitle("Select Options");
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, final int i) {

                             //Click Event for each item.
                             if(i==0)
                             {
                                 Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                 profileIntent.putExtra("user_id", list_user_id);
                                 startActivity(profileIntent);
                                 Log.d("FriendsFragment","case 0");
                             }
                             else {
                                 mUsersDatabase.child(mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         List<String> friends, family, both, none;
                                         User user = dataSnapshot.getValue(User.class);

                                         switch (i) {
                                             case 1:
                                                 user.addUserToList(list_user_id, "friends");
                                                 break;
                                             case 2:
                                                 user.addUserToList(list_user_id, "family");
                                                 break;
                                             case 3:
                                                 user.addUserToList(list_user_id, "both");
                                                 break;
                                             case 4:
                                                 user.addUserToList(list_user_id, "none");
                                                 break;
                                         }

                                         fbService.getCurrentUserDatabase(false).child("friends").setValue(user.getFriends());
                                         fbService.getCurrentUserDatabase(false).child("family").setValue(user.getFamily());
                                         fbService.getCurrentUserDatabase(false).child("both").setValue(user.getBoth());
                                         fbService.getCurrentUserDatabase(false).child("none").setValue(user.getNone());
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });
                             }
                         }
                     });
                     builder.show();
                     return true;
                 }

             });

             holder.mView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                     chatIntent.putExtra("user_id", list_user_id);
                     //chatIntent.putExtra("user_name", username);
                     startActivity(chatIntent);
                 }
             });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_single_layout,viewGroup,false);
                Log.d("Friendsfragment","In Create");
                return new FriendsViewHolder(view);
            }
        };
        mfriendsRecyclerViewAdapter.startListening();
        mFriendsList.setAdapter(mfriendsRecyclerViewAdapter);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mfriendsRecyclerViewAdapter!=null) {
            mfriendsRecyclerViewAdapter.startListening();
        }
    }
}
