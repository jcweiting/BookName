package com.example.second_book_exchange.api;

public class FriendList {

    private String uid;
    private String photoUrl;

    public FriendList(String uid, String photoUrl) {
        this.uid = uid;
        this.photoUrl = photoUrl;
    }

    public FriendList() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
