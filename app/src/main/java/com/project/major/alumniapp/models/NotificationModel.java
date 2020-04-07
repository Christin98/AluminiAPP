
package com.project.major.alumniapp.models;

public class NotificationModel {

    private String type;
    private String name;
    private String date;
    private String id;

    public NotificationModel() {
        //required
    }

//    public NotificationModel(String type, String name, String date) {
//        this.type = type;
//        this.name = name;
//        this.date = date;
//    }

    public NotificationModel(String type, String name, String date, String id) {
        this.type = type;
        this.name = name;
        this.date = date;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
