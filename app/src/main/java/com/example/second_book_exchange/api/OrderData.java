package com.example.second_book_exchange.api;

import com.example.second_book_exchange.BookOuterData;

import java.util.ArrayList;

public class OrderData {

    private long id;


    private int status;  //判斷目前此訂單的狀態 0:交易中(尚未付款) 1:交易完成
    private String myUid;
    private String orderId;  //訂單編號
    private int role;   //角色 555 : 買家 , 666 : 賣家
    private ArrayList<BookOuterData> checkOutList;

    public OrderData(int status, String myUid, String orderId, int role, ArrayList<BookOuterData> checkOutList) {
        this.status = status;
        this.myUid = myUid;
        this.orderId = orderId;
        this.role = role;
        this.checkOutList = checkOutList;
    }

    public OrderData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public ArrayList<BookOuterData> getCheckOutList() {
        return checkOutList;
    }

    public void setCheckOutList(ArrayList<BookOuterData> checkOutList) {
        this.checkOutList = checkOutList;
    }
}
