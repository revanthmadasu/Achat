package com.revanth.apps.achat;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolder extends RecyclerView.ViewHolder {
    View mView;
    public UsersViewHolder(View itemView)
    {
        super(itemView);

        mView=itemView;
        Log.d("UserViewHolder","UserViewHolder instantiated");
    }
    public void setDisplayName(String name)
    {
        TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);
    }
    public void setUserStatus(String status)
    {
        TextView userStatusView=(TextView)mView.findViewById(R.id.user_single_status);
        userStatusView.setText(status);
    }
    public void setUserImage(String thumb_image,Context ctx)
    {
        CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.user_single_image);
        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
    }
}
