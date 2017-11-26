package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TranThanhTung on 22/11/2017.
 */

public class UserProfile implements Serializable{
    private static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String EMAIL = "Email";
    private static final String ADDRESS = "Address";
    private static final String PHONE = "Phone";
    private static final String COVER = "Cover";
    private static final String AVATAR_URL = "AvatarUrl";
    private static final String GENDER = "Gender";

    @SerializedName(ID)
    @Expose
    private String id;
    @SerializedName(NAME)
    @Expose
    private String name = "";
    @SerializedName(EMAIL)
    @Expose
    private String email = "";
    @SerializedName(ADDRESS)
    @Expose
    private String address = "";
    @SerializedName(PHONE)
    @Expose
    private String phone = "";
    @SerializedName(COVER)
    @Expose
    private String coverUrl;
    @SerializedName(AVATAR_URL)
    @Expose
    private String avatarUrl;
    @SerializedName(GENDER)
    @Expose
    private String gender = "";

    private List<Category> favoriteCategories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Category> getFavoriteCategories() {
        return favoriteCategories;
    }

    public void setFavoriteCategories(List<Category> favoriteCategories) {
        this.favoriteCategories = favoriteCategories;
    }
}
