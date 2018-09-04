package com.example.restaurantdemo.model;

public class SizeMenuModel {

    private String category1, category2;
    private int imageId;
    private String name, price;
    private int qty;
    private String itemNo;

    public SizeMenuModel(){

    }

    public SizeMenuModel(String category1, String category2, String itemNo, int imageId, String name, String price, int qty){
        this.category1 = category1;
        this.category2 = category2;
        this.itemNo = itemNo;
        this.imageId = imageId;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
