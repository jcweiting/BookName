package com.example.second_book_exchange.api;

public class SearchData {

    private String name;

    private String uid;

    public SearchData() {
    }

    public SearchData(String name,String uid) {
        this.name = name;
        this.uid = uid;
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
}
