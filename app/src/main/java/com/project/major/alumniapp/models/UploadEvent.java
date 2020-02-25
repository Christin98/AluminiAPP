package com.project.major.alumniapp.models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public class UploadEvent {

    public String id;
    public String name;
    public String desc;
    public String loc;
    public String email;

    public String url;
    public UploadEvent() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UploadEvent(String id, String name, String desc, String loc, String email, String url) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.loc = loc;
        this.email=email;
        this.url=url;
    }

    public UploadEvent(String id, String name, String desc, String loc, String email) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.loc = loc;
        this.email = email;
        this.url = null;
    }
}
