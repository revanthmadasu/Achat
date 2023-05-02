package com.revanth.apps.achat;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }
}
