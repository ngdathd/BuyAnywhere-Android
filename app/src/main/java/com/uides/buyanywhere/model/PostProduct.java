package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TranThanhTung on 26/11/2017.
 */

public class PostProduct {
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ShopId")
    @Expose
    private String shopId;
    @SerializedName("CurrentPrice")
    @Expose
    private long currentPrice;
    @SerializedName("OriginPrice")
    @Expose
    private long originPrice;
    @SerializedName("PreviewUrl")
    @Expose
    private String previewUrl;
    @SerializedName("CategoryId")
    @Expose
    private String categoryId;
    @SerializedName("Quantity")
    @Expose
    private int quantity;
    @SerializedName("DescriptiveImageUrl")
    @Expose
    private List<String> descriptiveImageUrl;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getDescriptiveImageUrl() {
        return descriptiveImageUrl;
    }

    public void setDescriptiveImageUrl(List<String> descriptiveImageUrl) {
        this.descriptiveImageUrl = descriptiveImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
