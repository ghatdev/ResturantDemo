package com.example.restaurantdemo.model;

public class MenuModel {
    int menuId;
    String title, detail;
    int imageID;

    public MenuModel() {

    }

    public MenuModel(int menuId, String title, String detail, int imageID) {
        this.menuId = menuId;
        this.title = title;
        this.detail = detail;
        this.imageID = imageID;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
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

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
