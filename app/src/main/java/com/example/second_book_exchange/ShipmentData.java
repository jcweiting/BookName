package com.example.second_book_exchange;

public class ShipmentData {

    private String shipment;
    private int price;

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ShipmentData(String shipment, int price) {
        this.shipment = shipment;
        this.price = price;
    }

    public ShipmentData() {
    }
}
