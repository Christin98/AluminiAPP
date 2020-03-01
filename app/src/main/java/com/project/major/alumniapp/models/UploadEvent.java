package com.project.major.alumniapp.models;

public class UploadEvent {

    public String id;
    public String name;
    public String desc;
    public String loc;
    public String email;
    public String date_added;
    public String user_id;
    public String url;

    public UploadEvent() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public UploadEvent(String id, String name, String desc, String loc, String email, String date_added, String user_id, String url) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.loc = loc;
        this.email = email;
        this.date_added = date_added;
        this.user_id = user_id;
        this.url = url;
    }

    public UploadEvent(String id, String name, String desc, String loc, String email, String date_added, String user_id) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.loc = loc;
        this.email = email;
        this.date_added = date_added;
        this.user_id = user_id;
        this.url = null;
    }
}
