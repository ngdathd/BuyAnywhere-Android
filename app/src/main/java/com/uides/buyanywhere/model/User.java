
package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable{
    public static final String ID = "Id";
    public static final String FACEBOOK_ID = "FbId";
    public static final String NAME = "Name";
    public static final String ACCESS_TOKEN = "AccessToken";

    @SerializedName(ID)
    @Expose
    private String id;
    @SerializedName(FACEBOOK_ID)
    @Expose
    private String facebookID;
    @SerializedName(NAME)
    @Expose
    private String name;
    @SerializedName(ACCESS_TOKEN)
    @Expose
    private String accessToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
