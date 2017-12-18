package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public class ShopLocationResult {
    @SerializedName("ShopName")
    @Expose
    private String shopName;
    @SerializedName("Distance")
    @Expose
    private double distance;
    @SerializedName("Lat")
    @Expose
    private double lat;
    @SerializedName("Lon")
    @Expose
    private double lon;
    @SerializedName("Products")
    @Expose
    private List<ProductReview> products;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public List<ProductReview> getProducts() {
        return products;
    }

    public void setProducts(List<ProductReview> products) {
        this.products = products;
    }
}
