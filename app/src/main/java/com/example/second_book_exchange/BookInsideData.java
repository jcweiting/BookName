package com.example.second_book_exchange;

import java.io.Serializable;

//要傳值物件,要implements Serializable
public class BookInsideData implements Serializable {

    private String bookName;
    private String qty;
    private String unitPrice;
    private String totalPrice;
    private String photoUrl;
    private String userEmail;
    private long id;
    private String orderId;
    private String uploaderUid;
    private String myUid;
    private boolean isSelectedProduct;

    public BookInsideData(String bookName, String qty, String unitPrice, String totalPrice, String photoUrl, String userEmail, long id, String uploaderUid, String myUid, boolean isSelectedProduct, String orderId) {
        this.bookName = bookName;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.photoUrl = photoUrl;
        this.userEmail = userEmail;
        this.id = id;
        this.uploaderUid = uploaderUid;
        this.myUid = myUid;
        this.isSelectedProduct = isSelectedProduct;
        this.orderId = orderId;
    }

    public BookInsideData() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUploaderUid() {
        return uploaderUid;
    }

    public void setUploaderUid(String uploaderUid) {
        this.uploaderUid = uploaderUid;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public boolean isSelectedProduct() {
        return isSelectedProduct;
    }

    public void setSelectedProduct(boolean selectedProduct) {
        isSelectedProduct = selectedProduct;
    }
}
