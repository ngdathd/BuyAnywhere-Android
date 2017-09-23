
package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfo implements Serializable{

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("FbId")
    @Expose
    private String fbId;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("AccessToken")
    @Expose
    private String accessToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
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

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", fbId='" + fbId + '\'' +
                ", name='" + name + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
