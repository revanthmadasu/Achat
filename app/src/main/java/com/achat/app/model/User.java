package com.achat.app.model;

import com.achat.app.services.FirebaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private String name;
    private String status;
    private String image;
    private String thumb_image;
    private String device_token;
    private List<String> friends;
    private List<String> family;
    private HashMap<String, HashMap<String, String>> auto_reply_data;
    private boolean online;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name) {
        this.name = name;
        this.status = "Hey. I'm using AChat";
        this.image = "default";
        this.thumb_image = "default";
        this.device_token = FirebaseService.getInstance().getDeviceToken();
        this.friends = new ArrayList<String>();
        this.family = new ArrayList<String>();
        this.auto_reply_data = new HashMap<String, HashMap<String, String>>();
        this.online = true;
    }

    public User(String name, String status, String image, String thumb_image, String device_token,
                List<String> friends, List<String> family, HashMap<String, HashMap<String, String>> auto_reply_data,
                boolean online) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
        this.device_token = device_token;
        this.friends = friends;
        this.family = family;
        this.auto_reply_data = auto_reply_data;
        this.online = online;
    }

    // Getters and setters for all properties
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getFamily() {
        return family;
    }

    public void setFamily(List<String> family) {
        this.family = family;
    }

    public HashMap<String, HashMap<String, String>> getAuto_reply_data() {
        return auto_reply_data;
    }

    public void setAuto_reply_data(HashMap<String, HashMap<String, String>> auto_reply_data) {
        this.auto_reply_data = auto_reply_data;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}

