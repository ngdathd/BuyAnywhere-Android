
package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable{
    public static final String ID = "Id";
    public static final String FACEBOOK_ID = "FbId";
    public static final String ACCESS_TOKEN = "AccessToken";
    private static final String SHOP_ID = "ShopId";

    @SerializedName(ID)
    @Expose
    private String id;
    @SerializedName(FACEBOOK_ID)
    @Expose
    private String facebookID;
    @SerializedName(ACCESS_TOKEN)
    @Expose
    private String accessToken;
    @SerializedName(SHOP_ID)//TODO need shop id
    @Expose
    private String shopID;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", facebookID='" + facebookID + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", shopID='" + shopID + '\'' +
                '}';
    }
}
