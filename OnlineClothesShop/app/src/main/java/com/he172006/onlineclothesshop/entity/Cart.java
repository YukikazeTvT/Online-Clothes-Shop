package com.he172006.onlineclothesshop.entity;

import java.io.Serializable;

public class Cart implements Serializable { // Thêm implements Serializable
    private int cartId;
    private int accountId;
    private int productId;
    private int quantity;
    private String dateAdded;

    // Default constructor
    public Cart() {
    }

    // Constructor with all fields
    public Cart(int cartId, int accountId, int productId, int quantity, String dateAdded) {
        this.cartId = cartId;
        this.accountId = accountId;
        this.productId = productId;
        this.quantity = quantity;
        this.dateAdded = dateAdded;
    }

    // Getters and Setters
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}