package com.example.milkcollection.model;

public class UserModel {

    String uid;
    String firstName, middleName, lastName, mobile, email, accountStatus;

    public UserModel() {}

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getMobile() { return mobile; }
    public String getEmail() { return email; }
    public String getAccountStatus() { return accountStatus; }
}