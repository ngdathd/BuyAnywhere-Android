package com.uides.buyanywhere.model;

/**
 * Created by TranThanhTung on 08/12/2017.
 */

public class Order {
    private String user;
    private String address;
    private String phone;
    private int quantity;

    public Order(String user, String address, String phone, int quantity) {
        this.user = user;
        this.address = address;
        this.phone = phone;
        this.quantity = quantity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
}
