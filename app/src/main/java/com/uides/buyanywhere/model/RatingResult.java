package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public class RatingResult implements Serializable{
    @SerializedName("countRating")
    @Expose
    private int ratingCount;
    @SerializedName("averageRating")
    @Expose
    private int averageRating;
    @SerializedName("rating")
    @Expose
    private Feedback userRating;

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    public Feedback getUserRating() {
        return userRating;
    }

    public void setUserRating(Feedback userRating) {
        this.userRating = userRating;
    }

    @Override
    public String toString() {
        return "RatingResult{" +
                "ratingCount=" + ratingCount +
                ", averageRating=" + averageRating +
                ", userRating=" + userRating +
                '}';
    }
}
