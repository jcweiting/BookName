package com.example.second_book_exchange;

import java.io.Serializable;

public class UserBasicData implements Serializable {

    private String nickName;
    private String account;
    private String userUid;
    private String tel;
    private String email;
    private long id;
    private String userPhotoUrl;    //上傳到網路上的照片, 拿網址, 所以是String
    private int follow;
    private int follower;
    private int bookCount;
    private String bankCode;
    private String bankAccount;
    private String bankName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserBasicData(String bankCode, String bankAccount, String bankName) {
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
    }

    public UserBasicData(){

    }

    public UserBasicData(String nickName, String account, String userUid, String tel, String email, String userPhotoUrl, int follow, int follower, int bookCount) {
        this.nickName = nickName;
        this.account = account;
        this.userUid = userUid;
        this.tel = tel;
        this.email = email;
        this.userPhotoUrl = userPhotoUrl;
        this.follow = follow;
        this.follower = follower;
        this.bookCount = bookCount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }
}
