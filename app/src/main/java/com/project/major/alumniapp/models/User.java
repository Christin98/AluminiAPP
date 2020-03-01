/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 29                                                                         *
 ******************************************************************************/

package com.project.major.alumniapp.models;

public class User {
    private String createdat;

    private String uid;

    private String phone_verified;

    private String user_image;

    private String user_name;

    private String user_thumb_image;

    private String verified;

    private String email;

    private String search_name;

    public String getCreatedat ()
    {
        return createdat;
    }

    public void setCreatedat (String createdat)
    {
        this.createdat = createdat;
    }

    public String getUid ()
    {
        return uid;
    }

    public void setUid (String uid)
    {
        this.uid = uid;
    }

    public String getPhone_verified ()
    {
        return phone_verified;
    }

    public void setPhone_verified (String phone_verified)
    {
        this.phone_verified = phone_verified;
    }

    public String getUser_image ()
    {
        return user_image;
    }

    public void setUser_image (String user_image)
    {
        this.user_image = user_image;
    }

    public String getUser_name ()
    {
        return user_name;
    }

    public void setUser_name (String user_name)
    {
        this.user_name = user_name;
    }

    public String getUser_thumb_image ()
    {
        return user_thumb_image;
    }

    public void setUser_thumb_image (String user_thumb_image)
    {
        this.user_thumb_image = user_thumb_image;
    }

    public String getVerified ()
    {
        return verified;
    }

    public void setVerified (String verified)
    {
        this.verified = verified;
    }

    public String getemail ()
    {
        return email;
    }

    public void setemail (String email)
    {
        this.email = email;
    }

    public String getSearch_name ()
    {
        return search_name;
    }

    public void setSearch_name (String search_name)
    {
        this.search_name = search_name;
    }

    @Override
    public String toString()
    {
        return "User [createdat = "+createdat+", uid = "+uid+", phone_verified = "+phone_verified+", user_image = "+user_image+", user_name = "+user_name+", user_thumb_image = "+user_thumb_image+", verified = "+verified+", e-mail = "+email+", search_name = "+search_name+"]";
    }
}
