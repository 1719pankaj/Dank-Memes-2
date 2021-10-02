package com.example.dankmemes2;

public class Users {  // we need this model class to crate database having these below mentioned field

    String uid;
    String name;
    String phoneNo;
    String address;
    String email;
    String imageUri;

    public Users() {

    }

    public Users(String uid, String name, String phoneNo, String address, String imageUri) {
        this.uid = uid;
        this.name = name;
        this.phoneNo = phoneNo;
        this.address = address;
        this.imageUri = imageUri;
    }

    public Users(String uid, String name, String phoneNo, String address, String email, String imageUri) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.phoneNo = phoneNo;
        this.email = email;
        this.imageUri = imageUri;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
