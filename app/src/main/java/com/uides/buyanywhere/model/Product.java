
package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Product implements Serializable{
    public static final String ID = "Id";
    public static final String SHOP_NAME = "ShopName";
    public static final String CATEGORY_NAME = "CategoryName";
    public static final String NAME = "Name";
    public static final String QUANTITY = "Quantity";
    public static final String CURRENT_PRICE = "CurrentPrice";
    public static final String ORIGIN_PRICE = "OriginPrice";
    public static final String PREVIEW_URL = "PreviewUrl";
    public static final String DESCRIPTIVE_IMAGE_URL = "DescriptiveImageUrl";
    public static final String DESCRIPTION = "Description";
    public static final String CREATED_DATE = "CreatedDate";
    public static final String RATING = "Rating";
    private static final String RATING_COUNT = "RatingCount";

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
    @SerializedName(DESCRIPTIVE_IMAGE_URL)
    @Expose
    private List<String> descriptiveImageUrl;
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
    @SerializedName(DESCRIPTION)
    @Expose
    private String description;
    @SerializedName(RATING_COUNT)
    @Expose
    private int ratingCount;

    private boolean isAddedToCart;

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

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public List<String> getDescriptiveImageUrl() {
        return descriptiveImageUrl;
    }

    public void setDescriptiveImageUrl(List<String> descriptiveImageUrl) {
        this.descriptiveImageUrl = descriptiveImageUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAddedToCart() {
        return isAddedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        isAddedToCart = addedToCart;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
