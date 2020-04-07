package com.project.major.alumniapp.models;

public class Event {

    private String id;
    private String event_name;
    private String event_description;
    private String event_location;
    private String event_time;
    private String event_date;
    private String imageurl;
    private String tags;
    private String user_id;
    private String user_name;
    private String date_added;
    private String profileImg;


    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Event(String id, String event_name, String event_description, String event_location, String event_time, String event_date, String imageurl, String tags, String user_id, String user_name, String date_added, String profileImg) {
        this.id = id;
        this.event_name = event_name;
        this.event_description = event_description;
        this.event_location = event_location;
        this.event_time = event_time;
        this.event_date = event_date;
        this.imageurl = imageurl;
        this.tags = tags;
        this.user_id = user_id;
        this.user_name = user_name;
        this.date_added = date_added;
        this.profileImg = profileImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
