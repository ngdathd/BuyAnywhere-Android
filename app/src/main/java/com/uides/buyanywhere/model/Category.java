package com.uides.buyanywhere.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TranThanhTung on 02/10/2017.
 */

public class Category implements Serializable , Parcelable{
    public static final String ID = "Id";
    public static final String NAME = "Name";
    public static final String DESCRIPTION = "Description";
    public static final String ICON_URL = "IconUrl";

    @SerializedName(ID)
    private String id;
    @SerializedName(NAME)
    private String name;
    @SerializedName(DESCRIPTION)
    private String description;
    @SerializedName(ICON_URL)
    private String iconUrl;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Category && ((Category) obj).getId().equals(id);
    }

    @Override
    public String toString() {
        return "{\n" +
                "id='" + id + '\'' +
                ",\n name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
