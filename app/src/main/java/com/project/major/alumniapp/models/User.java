package com.project.major.alumniapp.models;

public class User {
    private String createdat;
    private String uid;
    private String user_type;
    private String phone_verified;
    private String user_image;
    private String user_name;
    private String user_thumb_image;
    private String verified;
    private String email;
    private String phone;
    private String country;
    private String state;
    private String city;
    private String nav_state;
    private String navodhya;
    private String batch;
    private String token;
    private String profession;
    private String organization;
    private String search_name;
    private String verification_ID;

    public User() {
        //This Constructor is required.
    }

    public User(String createdat, String uid, String user_type, String phone_verified, String user_image, String user_name, String user_thumb_image, String verified, String email, String phone, String country, String state, String city, String nav_state, String navodhya, String batch, String profession, String organization, String search_name) {
        this.createdat = createdat;
        this.uid = uid;
        this.user_type = user_type;
        this.phone_verified = phone_verified;
        this.user_image = user_image;
        this.user_name = user_name;
        this.user_thumb_image = user_thumb_image;
        this.verified = verified;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.state = state;
        this.city = city;
        this.nav_state = nav_state;
        this.navodhya = navodhya;
        this.batch = batch;
        this.profession = profession;
        this.organization = organization;
        this.search_name = search_name;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getPhone_verified() {
        return phone_verified;
    }

    public void setPhone_verified(String phone_verified) {
        this.phone_verified = phone_verified;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_thumb_image() {
        return user_thumb_image;
    }

    public void setUser_thumb_image(String user_thumb_image) {
        this.user_thumb_image = user_thumb_image;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNav_state() {
        return nav_state;
    }

    public void setNav_state(String nav_state) {
        this.nav_state = nav_state;
    }

    public String getNavodhya() {
        return navodhya;
    }

    public void setNavodhya(String navodhya) {
        this.navodhya = navodhya;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }

    public String getVerification_ID() {
        return verification_ID;
    }

    public void setVerification_ID(String verification_ID) {
        this.verification_ID = verification_ID;
    }
}
