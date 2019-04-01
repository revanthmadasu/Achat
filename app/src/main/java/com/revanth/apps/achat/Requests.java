package com.revanth.apps.achat;

import android.util.Log;

public class Requests {
    private String request_type;

    public Requests()
    {
        request_type="default";
        Log.d("revaa requests","requests instantiated with default values");
    }

    public Requests(String request_type)
    {
        this.request_type=request_type;
        Log.d("revaa requests","requests instantiated with request type "+request_type);
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

}
