/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 1                                                                          *
 ******************************************************************************/

package com.project.major.alumniapp.models;



public class Jobs {

    private String id;
    private String companyImage;
    private String companyName;
    private String jobProfile;
    private String jobDescription;
    private String lastDate;
    private String experience;
    private String location;
    private String userId;
    private String applyLink;

    public Jobs(){

    }

    public Jobs(String id, String companyImage, String companyName, String jobProfile, String jobDescription, String lastDate, String experience, String location, String userId, String applyLink) {
        this.id = id;
        this.companyImage = companyImage;
        this.companyName = companyName;
        this.jobProfile = jobProfile;
        this.jobDescription = jobDescription;
        this.lastDate = lastDate;
        this.experience = experience;
        this.location = location;
        this.userId = userId;
        this.applyLink = applyLink;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplyLink() {
        return applyLink;
    }

    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }
}
