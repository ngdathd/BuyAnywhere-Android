package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by TranThanhTung on 02/12/2017.
 */

public class Feedback implements Serializable{
    public static final String CREATED_DATE = "CreatedDate";

    @SerializedName("AvatarUrl")
    @Expose
    private String avatarUrl;
    @SerializedName("OwnerName")
    @Expose
    private String ownerName;
    @SerializedName("Rating")
    @Expose
    private int rating;
    @SerializedName("Comment")
    @Expose
    private String comment;
    @SerializedName(CREATED_DATE)
    @Expose
    private Date createdDate;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
