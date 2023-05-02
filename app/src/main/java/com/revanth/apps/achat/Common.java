package com.revanth.apps.achat;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.achat.app.model.User;
import com.achat.app.services.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Common {
    public static void showChatOptions(ViewHolder holder, String list_user_id, Fragment fragment) {
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                CharSequence options[] = new CharSequence[]{"Open Profile","auto-reply: Friend","auto-reply: Family","auto-reply: Both","auto-reply: None"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {

                        //Click Event for each item.
                        if(i==0)
                        {
                            Intent profileIntent = new Intent(fragment.getContext(), ProfileActivity.class);
                            profileIntent.putExtra("user_id", list_user_id);
                            fragment.startActivity(profileIntent);
                            Log.d("FriendsFragment","case 0");
                        }
                        else {
                            FirebaseService fbService = FirebaseService.getInstance();
                            fbService.getCurrentUserDatabase(false).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    fbService.updateTrainData(user.getAuto_reply_data());
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
    }
}
