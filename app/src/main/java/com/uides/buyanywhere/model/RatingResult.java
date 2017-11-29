package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public class RatingResult {
    @SerializedName("countRating")
    @Expose
    private int ratingCount;
    @SerializedName("averageRating")
    @Expose
    private int averageRating;

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
}
