package com.book.second_book_exchange;

import java.io.Serializable;
import java.util.ArrayList;

//要傳值物件,要implements Serializable
public class AddBookBasicData implements Serializable {

    private String bookName;
    private String classify;
    private String description;
    private String qty;
    private String status;
    private String remark;
    private String unitPrice;
    private String totalPrice;
    private String photoUrl;
    private String userEmail;
    private String shipment;      //沒有用到,但因後端已建立好,所以留著變數,否則要重改
    private long time;
    private long id;
    private String uploaderUid;
    private String myUid;
    private int msgCount;
    private boolean selectHeart;

    public AddBookBasicData(String bookName, String classify, String description, String qty, String unitPrice, String totalPrice, String status,  String remark, String uploaderUid, long time, String photoUrl, String userEmail) {
        this.bookName = bookName;
        this.classify = classify;
        this.description = description;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.remark = remark;
        this.uploaderUid = uploaderUid;
        this.time = time;
        this.photoUrl = photoUrl;
        this.userEmail = userEmail;
    }

    public AddBookBasicData(){

    }

    public boolean isSelectHeart() {
        return selectHeart;
    }

    public void setSelectHeart(boolean selectHeart) {
        this.selectHeart = selectHeart;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUploaderUid() {
        return uploaderUid;
    }

    public void setUploaderUid(String uploaderUid) {
        this.uploaderUid = uploaderUid;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
