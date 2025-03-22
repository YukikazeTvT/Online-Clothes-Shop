package com.he172006.onlineclothesshop.entity;
public class Account {
    private int accountId;
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String role;

    // Default constructor
    public Account() {
    }

    // Constructor with all fields
    public Account(int accountId, String name, String email, String password, String address, String phone, String role) {
        this.accountId = accountId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.role = role;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}