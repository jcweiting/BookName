package com.book.second_book_exchange;

public class SpinnerList {

    private String shipmentWay;
    private int price;

    public SpinnerList(String shipmentWay, int price) {
        this.shipmentWay = shipmentWay;
        this.price = price;
    }

    public String getShipmentWay() {
        return shipmentWay;
    }

    public void setShipmentWay(String shipmentWay) {
        this.shipmentWay = shipmentWay;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
