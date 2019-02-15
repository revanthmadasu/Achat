package com.revanth.apps.achat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */

public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase,mUsersDatabase;
    private FirebaseAuth mAuth;

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
        mCurrent_user_id=mAuth.getCurrentUser().getUid();

        mFriendsDatabase=FirebaseDatabase.getInstance().getReference()
                .child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

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
                        Log.d("Rocky mUsers",dataSnapshot.child(list_user_id).child("name").getValue().toString());
                        String username=dataSnapshot.child(list_user_id).child("name").getValue().toString();
                        String thumb_image=dataSnapshot.child(list_user_id).child("thumb_image").getValue().toString();
                        holder.setName(username);
                        holder.setUserImage(thumb_image,getContext());


                        Boolean userOnline=(boolean) dataSnapshot.child(list_user_id).child("online").getValue();
                        holder.setUserOnline(userOnline);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

             /*  holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent= new Intent(FriendsFragment.this.getActivity(),ProfileActivity.class);
                        profileIntent.putExtra("user_id",list_user_id);
                        startActivity(profileIntent);
                    }
                });*/
             holder.mView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                     final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                     builder.setTitle("Select Options");
                     builder.setItems(options, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                           //  String username=dataSnapshot.child(list_user_id).child("name").getValue().toString();

                             //Click Event for each item.
                             if(i == 0){

                                 Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                 profileIntent.putExtra("user_id", list_user_id);
                                 startActivity(profileIntent);

                             }

                             if(i == 1){

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                 chatIntent.putExtra("user_id", list_user_id);
                                // chatIntent.putExtra("user_name", username);
                                 startActivity(chatIntent);

                             }

                         }
                     });

                     builder.show();

                 }

             });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_single_layout,viewGroup,false);
                Log.d("Rocky","In Create");
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
        if(mfriendsRecyclerViewAdapter!=null)
            mfriendsRecyclerViewAdapter.startListening();



    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

        public void setUserOnline(Boolean online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if(online_status==true){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }

}
