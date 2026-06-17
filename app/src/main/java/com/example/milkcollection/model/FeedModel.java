package com.example.milkcollection.model;

public class FeedModel {

    private String id;
    private String feedId;
    private String uid;
    private String feedName;
    private int quantity;
    private int pricePerKg;
    private int totalAmount;
    private int stockKg;
    private String orderStatus;

    public FeedModel() {
    }

    public FeedModel(
            String id,
            String feedId,
            String uid,
            String feedName,
            int quantity,
            int pricePerKg,
            int totalAmount,
            String orderStatus
    ) {
        this.id = id;
        this.feedId = feedId;
        this.uid = uid;
        this.feedName = feedName;
        this.quantity = quantity;
        this.pricePerKg = pricePerKg;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public String getId() {
        return id;
    }

    public String getFeedId() {
        return feedId;
    }

    public String getUid() {
        return uid;
    }

    public String getFeedName() {
        return feedName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPricePerKg() {
        return pricePerKg;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getStockKg() {
        return stockKg;
    }

    public void setStockKg(int stockKg) {
        this.stockKg = stockKg;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}