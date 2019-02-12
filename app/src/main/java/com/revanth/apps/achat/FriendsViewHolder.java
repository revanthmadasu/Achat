package com.revanth.apps.achat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        mView=itemView;
    }
    public void setDate(String date)
    {
        TextView userNameView=(TextView)mView.findViewById(R.id.user_single_status);
        userNameView.setText(date);
    }
}
