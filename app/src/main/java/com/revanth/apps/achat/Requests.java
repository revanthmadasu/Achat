package com.revanth.apps.achat;

import android.util.Log;

public class Requests {
    private String user_name, user_status, user_thumb_image,request_type;

    public Requests(String user_name, String user_status, String user_thumb_image) {
        this.user_name = user_name;
        this.user_status = user_status;
        this.user_thumb_image = user_thumb_image;
        Log.d("revaa requests","requests instantiated with given values");
    }

    public Requests()
    {
        this.user_name="Default Name";
        this.user_thumb_image="https://firebasestorage.googleapis.com/v0/b/achat-4df50.appspot.com/o/profile_images%2FDOEv3FVSRQbzv7nMKX9b3eqqd0z1.jpg?alt=media&token=806ae57a-ea22-4fc7-872a-3303a17f922f";
        this.user_status="Default Status";
        Log.d("revaa requests","requests instantiated with default values");
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public Requests(String request_type)
    {
        this.request_type=request_type;
        Log.d("revaa requests","requests instantiated with request type");
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }

    @Override
    public String toString() {
        return "Requests{" +
                "user_name='" + user_name + '\'' +
                '}';
    }
}
