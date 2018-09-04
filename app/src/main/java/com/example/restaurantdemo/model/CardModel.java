package com.example.restaurantdemo.model;

public class CardModel {

    String email, cardno, cardholder, cardexpire, cardcvc;

    public CardModel() {

    }

    public CardModel(String cardno, String cardholder, String cardexpire, String cardcvc) {
        this.cardno = cardno;
        this.cardholder = cardholder;
        this.cardexpire = cardexpire;
        this.cardcvc = cardcvc;
    }

    public CardModel(String email, String cardno, String cardholder, String cardexpire, String cardcvc) {
        this.email = email;
        this.cardno = cardno;
        this.cardholder = cardholder;
        this.cardexpire = cardexpire;
        this.cardcvc = cardcvc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getCardholder() {
        return cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public String getCardexpire() {
        return cardexpire;
    }

    public void setCardexpire(String cardexpire) {
        this.cardexpire = cardexpire;
    }

    public String getCardcvc() {
        return cardcvc;
    }

    public void setCardcvc(String cardcvc) {
        this.cardcvc = cardcvc;
    }
}
