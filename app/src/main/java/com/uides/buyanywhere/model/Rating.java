package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TranThanhTung on 27/11/2017.
 */

public class Rating {
    @SerializedName("Rating")
    @Expose
    private int rating;
    @SerializedName("Comment")
    @Expose
    private String comment;

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
}
