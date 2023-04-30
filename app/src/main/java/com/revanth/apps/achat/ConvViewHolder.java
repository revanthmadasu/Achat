package com.revanth.apps.achat;

import android.content.Context;
import android.graphics.Typeface;
//import android.support.annotation.NonNull;
import androidx.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConvViewHolder extends ViewHolder {
//    View mView;

    public ConvViewHolder(@NonNull View itemView) {
        super(itemView);
//        mView=itemView;
    }

    public void setMessage(String message, boolean isSeen){

        TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
        userStatusView.setText(message);

        if(!isSeen){
            userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
        } else {
            userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
        }

    }

    public void setName(String name){

        TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);

    }

    public void setUserImage(String thumb_image, Context ctx){

        CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
    }

    public void setUserOnline(String online_status) {

        ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

        if (online_status.equals("true")) {

            userOnlineView.setVisibility(View.VISIBLE);

        } else {

            userOnlineView.setVisibility(View.INVISIBLE);

        }
    }

}
