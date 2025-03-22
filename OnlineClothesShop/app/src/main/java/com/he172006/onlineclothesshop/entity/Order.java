package com.he172006.onlineclothesshop.entity;
public class Order {
    private int orderId;
    private int accountId;
    private String orderDate;
    private double totalAmount;
    private String status;

    // Default constructor
    public Order() {
    }

    // Constructor with all fields
    public Order(int orderId, int accountId, String orderDate, double totalAmount, String status) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
