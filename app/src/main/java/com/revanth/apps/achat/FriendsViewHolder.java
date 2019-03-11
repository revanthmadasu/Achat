package com.revanth.apps.achat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {


    View mView;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        mView=itemView;
    }
    public void setDate(String date)
    {
        TextView friendDateView=(TextView)mView.findViewById(R.id.user_single_status);
        friendDateView.setText(date);
    }
    public void setName(String name)
    {
        TextView friendNameView=(TextView)mView.findViewById(R.id.user_single_name);
        friendNameView.setText(name);
    }
    public void setUserImage(String thumb_image,Context ctx)
    {
        CircleImageView friendImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);
        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(friendImageView);
    }
    public void setUserOnline(String online_status)
    {
        ImageView userOnlineView=(ImageView)mView.findViewById(R.id.user_single_online_icon);
        if(online_status.equals("true"))
        {
            userOnlineView.setVisibility(View.VISIBLE);
        }
        else
        {
            userOnlineView.setVisibility(View.INVISIBLE);
        }


    }



}
