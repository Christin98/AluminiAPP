/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 1                                                                          *
 ******************************************************************************/

package com.project.major.alumniapp.models;



public class Jobs {

    String id;
    String companyImage;
    String companyName;
    String jobProfile;
    String jobDescription;
    String lastDate;
    String experience;
    String location;

    public Jobs(){

    }

    public Jobs(String id, String companyImage, String companyName, String jobProfile, String jobDescription, String lastDate, String experience, String location) {
        this.id = id;
        this.companyImage = companyImage;
        this.companyName = companyName;
        this.jobProfile = jobProfile;
        this.jobDescription = jobDescription;
        this.lastDate = lastDate;
        this.experience = experience;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyImage() {
        return companyImage;
    }

    public void setCompanyImage(String companyImage) {
        this.companyImage = companyImage;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobProfile() {
        return jobProfile;
    }

    public void setJobProfile(String jobProfile) {
        this.jobProfile = jobProfile;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
