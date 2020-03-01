/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 1                                                                          *
 ******************************************************************************/

package com.project.major.alumniapp.models;

public class Feeds {
    String id;
    String name;
    String userId;
    String timestamp;
    String caption_text;
    String text_url;
    String feed_image_url;

    public Feeds(){
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }


    public Feeds(String id,String name, String userId, String timestamp, String caption_text, String text_url, String feed_image_url) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.timestamp = timestamp;
        this.caption_text = caption_text;
        this.text_url = text_url;
        this.feed_image_url = feed_image_url;
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

    public String getText_url() {
        return text_url;
    }

    public void setText_url(String text_url) {
        this.text_url = text_url;
    }

    public String getFeed_image_url() {
        return feed_image_url;
    }

    public void setFeed_image_url(String feed_image_url) {
        this.feed_image_url = feed_image_url;
    }
}
