package com.example.second_book_exchange;

import java.io.Serializable;
import java.util.ArrayList;

//要傳值物件,要implements Serializable
public class BookOuterData implements Serializable {

    private String userEmail;
    private String uploaderUid;
    private String orderId = "";
    private String myUid;
    private String shipmentWay;
    private long id;
    private String sum;

    private int shipmentFee;

    private boolean isAllSelected;

    private ArrayList<BookInsideData> productLists;


    public BookOuterData(){

    }

    public BookOuterData(String userEmail, String uploaderUid, boolean isAllSelected, ArrayList<BookInsideData> productLists, String myUid,String shipmentWay,int shipmentFee, String orderId, String sum) {
        this.userEmail = userEmail;
        this.uploaderUid = uploaderUid;
        this.isAllSelected = isAllSelected;
        this.productLists = productLists;
        this.myUid = myUid;
        this.shipmentWay = shipmentWay;
        this.shipmentFee = shipmentFee;
        this.orderId = orderId;
        this.sum = sum;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getShipmentWay() {
        return shipmentWay;
    }

    public void setShipmentWay(String shipmentWay) {
        this.shipmentWay = shipmentWay;
    }

    public int getShipmentFee() {
        return shipmentFee;
    }

    public void setShipmentFee(int shipmentFee) {
        this.shipmentFee = shipmentFee;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUploaderUid() {
        return uploaderUid;
    }

    public void setUploaderUid(String uploaderUid) {
        this.uploaderUid = uploaderUid;
    }

    public boolean isAllSelected() {
        return isAllSelected;
    }

    public void setAllSelected(boolean allSelected) {
        isAllSelected = allSelected;
    }

    public ArrayList<BookInsideData> getProductLists() {
        return productLists;
    }

    public void setProductLists(ArrayList<BookInsideData> productLists) {
        this.productLists = productLists;
    }
}
