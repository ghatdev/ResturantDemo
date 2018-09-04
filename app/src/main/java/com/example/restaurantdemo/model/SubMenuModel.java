package com.example.restaurantdemo.model;

public class SubMenuModel {

    String title, detail, price;
    int imageID;

    public SubMenuModel() {

    }

    public SubMenuModel(String title, String detail, String price, int imageID) {
        this.title = title;
        this.detail = detail;
        this.price = price;
        this.imageID = imageID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
