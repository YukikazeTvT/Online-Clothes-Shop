package com.he172006.onlineclothesshop.entity;


public class Review {
    public int id;
    public int productId;
    public float rating;
    public String reviewText;


    public Review(int productId, float rating, String reviewText) {
        this.productId = productId;
        this.rating = rating;
        this.reviewText = reviewText;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getProductId() {
        return productId;
    }


    public void setProductId(int productId) {
        this.productId = productId;
    }


    public float getRating() {
        return rating;
    }


    public void setRating(float rating) {
        this.rating = rating;
    }


    public String getReviewText() {
        return reviewText;
    }


    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}








