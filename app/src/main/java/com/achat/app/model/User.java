package com.achat.app.model;

import com.achat.app.services.FirebaseService;
import com.achat.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String status;
    private String image;
    private String thumb_image;
    private String device_token;
    private HashMap<String, HashMap<String, String>> auto_reply_data;
    private Object online; // variable to store online status or timestamp

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name) {
        this.name = name;
        this.status = "Hey. I'm using AChat";
        this.image = "default";
        this.thumb_image = "default";
        this.device_token = FirebaseService.getInstance().getDeviceToken();
        this.auto_reply_data = new HashMap<String, HashMap<String, String>>();
        this.online = true;
    }

    public User(String name, String status, String image, String thumb_image, String device_token,
                HashMap<String, HashMap<String, String>> auto_reply_data,Object online) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
        this.device_token = device_token;
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

    public HashMap<String, HashMap<String, String>> getAuto_reply_data() {
        return auto_reply_data;
    }

    public void setAuto_reply_data(HashMap<String, HashMap<String, String>> auto_reply_data) {
        this.auto_reply_data = auto_reply_data;
    }

    public boolean isOnline() {
        if (online instanceof Boolean) {
            return (Boolean) online;
        } else if (online instanceof Long) {
            long timestamp = (Long) online;
            long currentTimestamp = System.currentTimeMillis();
            return (currentTimestamp - timestamp) < 100; // check if within timeout threshold
        } else {
            return false;
        }
    }

    public void setOnline(Object online) {
        this.online = online;
    }

    public void addUserToList(String userId, String listName) {
        if (!Utils.isTruthy(this.auto_reply_data)) {
            this.auto_reply_data = new HashMap<>();
        }
        if (!Utils.isTruthy(this.auto_reply_data.get("user_categories"))) {
            this.auto_reply_data.put("user_categories", new HashMap<>());
        }
        this.auto_reply_data.get("user_categories").put(userId, listName);
    }
}

