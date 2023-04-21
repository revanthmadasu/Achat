package com.revanth.apps.achat;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.achat.app.services.FirebaseService;
import com.achat.app.services.UserService;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView myRequestsList;
    private View myMainView,mProfileView;
    private DatabaseReference mFriendRequestsDatabase,mAllFriendRequestsDatabase,mUsersDatabase;
    private FirebaseAuth mAuth;
    String mCurrentUserId;

    private FirebaseService fbService;
    private UserService userService;

    private DatabaseReference mFriendDatabase;
    private Button acceptBtn;
    private Button declineBtn;
    private FirebaseRecyclerOptions<Requests> options;
    FirebaseRecyclerAdapter<Requests,RequestViewHolder> mReqsRecyclerViewAdapter;

    public RequestsFragment() {
        this.fbService = FirebaseService.getInstance();
        this.userService = UserService.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        myRequestsList = (RecyclerView)myMainView.findViewById(R.id.requests_list);
        mProfileView=inflater.inflate(R.layout.activity_profile, container, false);



        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mFriendRequestsDatabase = this.fbService.getCurrentUserFriendRequestsDB();


        mAllFriendRequestsDatabase = this.fbService.getAllUserFriendRequestsDB();


        mUsersDatabase = this.fbService.getUsersDb();

        mFriendDatabase = this.fbService.getFriendsDb();

        myRequestsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestsList.setLayoutManager(linearLayoutManager);



        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("RequestsFragment", "In Start");
        options = new FirebaseRecyclerOptions.Builder<Requests>().setQuery(mFriendRequestsDatabase, Requests.class).build();

        mReqsRecyclerViewAdapter= new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(options)
        {

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_request_all_users_layout,viewGroup,false);
                Log.d("RequestsFragment","In Create");
                return new RequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull final Requests model) {
                final String list_user_id=getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String requestType=model.getRequest_type();
                        String userName=dataSnapshot.child("name").getValue().toString();
                        String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                        String status=dataSnapshot.child("status").getValue().toString();

                        Log.d("RequestsFragment","request type: "+requestType);

                        holder.setThumb_user_image(thumb_image,getContext());
                        holder.setUser_Status(status);
                        holder.setUserName(userName);

                        if(!requestType.equals("sent")) {
                            // accept friend request
                            holder.mView.findViewById(R.id.request_accept_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                    mFriendDatabase.child(mCurrentUserId).child(list_user_id).child("date").setValue(currentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendDatabase.child(list_user_id).child(mCurrentUserId).child("date").setValue(currentDate)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mAllFriendRequestsDatabase.child(mCurrentUserId).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            mAllFriendRequestsDatabase.child(list_user_id).child(mCurrentUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    Button mProfileSendReqBtn = (Button) mProfileView.findViewById(R.id.profile_send_req_btn);
                                                                                    mProfileSendReqBtn.setEnabled(true);
                                                                                    mProfileSendReqBtn.setText("Unfriend");

                                                                                    Button mDeclineButton = (Button) mProfileView.findViewById(R.id.profile_decline_btn);
                                                                                    mDeclineButton.setVisibility(View.INVISIBLE);
                                                                                    mDeclineButton.setEnabled(false);

                                                                                    Toast.makeText(getContext(), "Successfully added friend", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });

                            holder.mView.findViewById(R.id.request_decline_btn).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAllFriendRequestsDatabase.child(mCurrentUserId).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAllFriendRequestsDatabase.child(list_user_id).child(mCurrentUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Button mProfileSendReqBtn = (Button) mProfileView.findViewById(R.id.profile_send_req_btn);
                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mProfileSendReqBtn.setText("Send Friend Request");

                                                    Button mDeclineButton = (Button) mProfileView.findViewById(R.id.profile_decline_btn);
                                                    mDeclineButton.setVisibility(View.INVISIBLE);
                                                    mDeclineButton.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            Button acceptButton=holder.mView.findViewById(R.id.request_accept_btn);
                            acceptButton.setVisibility(View.INVISIBLE);
                            acceptButton.setEnabled(false);

                            Button cancelButton=holder.mView.findViewById(R.id.request_decline_btn);
                            cancelButton.setText("Cancel");
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAllFriendRequestsDatabase.child(mCurrentUserId).child(list_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAllFriendRequestsDatabase.child(list_user_id).child(mCurrentUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Button mProfileSendReqBtn = (Button) mProfileView.findViewById(R.id.profile_send_req_btn);
                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mProfileSendReqBtn.setText("Send Friend Request");

                                                    Button mDeclineButton = (Button) mProfileView.findViewById(R.id.profile_decline_btn);
                                                    mDeclineButton.setVisibility(View.INVISIBLE);
                                                    mDeclineButton.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        mReqsRecyclerViewAdapter.startListening();
        myRequestsList.setAdapter(mReqsRecyclerViewAdapter);

    }
}

