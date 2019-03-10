package com.revanth.apps.achat;

import android.util.Log;

public class Users {
    public String name;
    public String image;
    public String status;
    public String thumb_image;

    public Users()
    {
        this.name="Default Name";
        this.image="https://firebasestorage.googleapis.com/v0/b/achat-4df50.appspot.com/o/profile_images%2FDOEv3FVSRQbzv7nMKX9b3eqqd0z1.jpg?alt=media&token=806ae57a-ea22-4fc7-872a-3303a17f922f";
        this.status="Default Status";
        this.thumb_image="Default ThumbImage";
    }
    public Users(String name,String image,String status,String thumb_image) {
        this.name=name;
        this.image = image;
        this.status=status;
        this.thumb_image=thumb_image;
        Log.d("rockstar","User instantiated");
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Users{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", status='" + status + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                '}';
    }
}
