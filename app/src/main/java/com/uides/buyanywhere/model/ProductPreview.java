package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by TranThanhTung on 19/11/2017.
 */

public class ProductPreview implements Serializable {
    public static final String ID = "Id";
    public static final String SHOP_NAME = "ShopName";
    public static final String CATEGORY_NAME = "CategoryName";
    public static final String NAME = "Name";
    public static final String QUANTITY = "Quantity";
    public static final String CURRENT_PRICE = "CurrentPrice";
    public static final String ORIGIN_PRICE = "OriginPrice";
    public static final String PREVIEW_URL = "PreviewUrl";
    public static final String CREATED_DATE = "CreatedDate";
    public static final String RATING = "Rating";

    @SerializedName(ID)
    @Expose
    private String id;
    @SerializedName(NAME)
    @Expose
    private String name;
    @SerializedName(SHOP_NAME)
    @Expose
    private String shopName;
    @SerializedName(CURRENT_PRICE)
    @Expose
    private long currentPrice;
    @SerializedName(ORIGIN_PRICE)
    @Expose
    private long originPrice;
    @SerializedName(PREVIEW_URL)
    @Expose
    private String previewUrl;
    @SerializedName(CATEGORY_NAME)
    @Expose
    private String categoryName;
    @SerializedName(QUANTITY)
    @Expose
    private int quantity;
    @SerializedName(RATING)
    @Expose
    private int rating;
    @SerializedName(CREATED_DATE)
    @Expose
    private Date createdDate;

    private String shopID;

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

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public long getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(long originPrice) {
        this.originPrice = originPrice;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }
}
