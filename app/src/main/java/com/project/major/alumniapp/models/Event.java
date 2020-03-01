package com.project.major.alumniapp.models;

public class Event {
    public String id;
    public String ev_name;
    public String desc;
    public String loc;
    public String email;
    public  String url;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEv_name(){
        return ev_name;
    }

    public void setEv_name(String ev_name){
        this.ev_name = ev_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLoc(){
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

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public Event(String id, String event_name, String desc, String loc, String email, String url) {
        this.id = id;
        this.ev_name = event_name;
        this.desc = desc;
        this.email = email;
        this.loc = loc;
        this.url = url;
    }
}
