package com.he172006.onlineclothesshop.entity;
public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int productId;
    private int quantity;
    private double subtotal;

    // Default constructor
    public OrderDetail() {
    }

    // Constructor with all fields
    public OrderDetail(int orderDetailId, int orderId, int productId, int quantity, double subtotal) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Getters and Setters
    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}