package com.he172006.onlineclothesshop.entity;

public class Shipping {
    private int shippingId;
    private int orderId;
    private String trackingNumber;
    private String shippingStatus;
    private String deliveryDate;

    // Default constructor
    public Shipping() {
    }

    // Constructor with all fields
    public Shipping(int shippingId, int orderId, String trackingNumber, String shippingStatus, String deliveryDate) {
        this.shippingId = shippingId;
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.shippingStatus = shippingStatus;
        this.deliveryDate = deliveryDate;
    }

    // Getters and Setters
    public int getShippingId() {
        return shippingId;
    }

    public void setShippingId(int shippingId) {
        this.shippingId = shippingId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}