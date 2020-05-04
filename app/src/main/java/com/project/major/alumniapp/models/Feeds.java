
package com.project.major.alumniapp.models;

public class Feeds {
    private String id;
    private String name;
    private String userId;
    private String timestamp;
    private String caption_text;
    private String tags;
    private String profile_img;

    public Feeds(){
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }


    public Feeds(String id, String name, String userId, String timestamp, String caption_text, String tags, String profile_img) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.timestamp = timestamp;
        this.caption_text = caption_text;
        this.tags = tags;
        this.profile_img = profile_img;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCaption_text() {
        return caption_text;
    }


    public void setCaption_text(String caption_text) {
        this.caption_text = caption_text;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

}