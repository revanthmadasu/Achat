package com.revanth.apps.achat;

import android.util.Log;

public class Friends {
    public String date;

    public Friends()
    {
        this.date="DefaultDate";
    }
    public Friends(String date) {
        Log.d("Rocky","Creating Users");
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
