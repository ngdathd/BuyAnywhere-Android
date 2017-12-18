package com.uides.buyanywhere.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by TranThanhTung on 18/12/2017.
 */

public class UserOrder {
    public static final String ORDER_DATE = "OrderDate";

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("PreviewUrl")
    @Expose
    private String previewUrl;
    @SerializedName("ProductId")
    @Expose
    private String productID;
    @SerializedName("ProductName")
    @Expose
    private String productName;
    @SerializedName("ShopName")
    @Expose
    private String shopName;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("CategoryName")
    @Expose
    private String categoryName;
    @SerializedName("Phone")
    @Expose
    private String phone;
    @SerializedName("Quantity")
    @Expose
    private int quantity;
    @SerializedName("OrderDate")
    @Expose
    private Date orderDate;
    @SerializedName("ShippedDate")
    @Expose
    private Date shippedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
