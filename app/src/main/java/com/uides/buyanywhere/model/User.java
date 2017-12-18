
package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable{
    public static final String ID = "Id";
    public static final String FACEBOOK_ID = "FbId";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String CLOUD_TOKEN = "CloudToken";
    private static final String SHOP_ID = "ShopId";
    private static final String NAME = "Name";
    private static final String EMAIL = "Email";
    private static final String AVATAR_URL = "AvatarUrl";
    private static final String COVER = "Cover";

    @SerializedName(ID)
    @Expose
    private String id;
    @SerializedName(ACCESS_TOKEN)
    @Expose
    private String accessToken;
    @SerializedName(CLOUD_TOKEN)
    @Expose
    private String cloudToken;
    @SerializedName(SHOP_ID)
    @Expose
    private String shopID;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("AvatarUrl")
    @Expose
    private String avatarUrl;
    @SerializedName("Cover")
    @Expose
    private String coverUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCloudToken() {
        return cloudToken;
    }

    public void setCloudToken(String cloudToken) {
        this.cloudToken = cloudToken;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}
