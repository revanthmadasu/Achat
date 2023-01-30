package com.revanth.apps.achat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import androidx.annotation.NonNull;
//import android.support.v4.app.Fragment;
import androidx.fragment.app.Fragment;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
//import android.support.v7.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.LinkedList;
import java.util.List;

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
                                 Log.d("revaa friendsFragment","case 0");
                             }
                             else {
                                 mUsersDatabase.child(mCurrent_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         final String friendsCat, familyCat;
                                         friendsCat = dataSnapshot.child("friends_cat").getValue().toString();
                                         familyCat = dataSnapshot.child("family_cat").getValue().toString();
                                         String resString="";

                                         String selectedCat="";
                                         List selectedCatIds;
                                         String category="";

                                         int count = 0;
                                         boolean changeRequired=false;
                                         boolean case3=false;
                                         switch (i) {
                                             case 1:
                                                 insertIntoCategory(friendsCat,"friends_cat");
                                                 return;
                                             case 2:
                                                 insertIntoCategory(familyCat,"family_cat");
                                                 return;
                                             case 3:
                                                 insertIntoCategory(familyCat,"family_cat");
                                                 insertIntoCategory(friendsCat,"friends_cat");
                                                 return;
                                             case 4:
                                                 removeFromCategory(friendsCat,"friends_cat");
                                                 removeFromCategory(familyCat,"family_cat");
                                                 return;
                                         }
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }

                                     public void insertIntoCategory(String selectedCat,String category)
                                     {
                                         boolean changeRequired=false;
                                         String resString="";
                                         List selectedCatIds;
                                         // have some friends
                                         if (!selectedCat.equals("")) {
                                             selectedCatIds = Arrays.asList(selectedCat.split(";;;"));
                                             if (selectedCatIds.contains(list_user_id)) {
                                                 Toast.makeText(getContext(),"Already exists in category",Toast.LENGTH_SHORT);
                                             }
                                             else
                                             {
                                                 resString=selectedCat+list_user_id;
                                                 changeRequired=true;
                                             }
                                         }

                                         // no friends
                                         else
                                         {
                                             resString=list_user_id;
                                             changeRequired=true;
                                         }

                                         //  update to database
                                         if(changeRequired)
                                             mUsersDatabase.child(mCurrent_user_id).child(category).setValue(resString+";;;").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     Toast.makeText(getContext(), "Successfully added to friends category", Toast.LENGTH_SHORT).show();
                                                 }
                                             });

                                     }
                                     public void removeFromCategory(String selectedCat, final String category)
                                     {

                                         List<String> selectedCatIds=new ArrayList(Arrays.asList(selectedCat.split(";;;")));
                                         Log.d("revaa friendsfragment","Received "+selectedCat+" Selected : "+selectedCatIds.toString());
                                         if(selectedCatIds.contains(list_user_id))
                                         {
                                             int index=selectedCatIds.indexOf(list_user_id);
                                             Log.d("revaa friendsfragment","Removing index = "+index);
                                             selectedCatIds.remove(index);
                                             Log.d("revaa friendsfragment","size after removing : "+selectedCatIds.size());
                                             StringBuilder sb=new StringBuilder();
                                             for(String i:selectedCatIds)
                                             {
                                                 sb.append(i);
                                                 sb.append(";;;");
                                             }
                                             //Log.d("revaa friendsfragment","about to remove from friends");
                                             mUsersDatabase.child(mCurrent_user_id).child(category).setValue(sb.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                 @Override
                                                 public void onSuccess(Void aVoid) {
                                                     Toast.makeText(getContext(), "Successfully removed from "+category+" category", Toast.LENGTH_SHORT);
                                                     Log.d("revaa friendsfragment","removed from "+category);
                                                 }
                                             });

                                             Log.d("revaa friendsfragment","done with removing from "+category);
                                         }

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


}
