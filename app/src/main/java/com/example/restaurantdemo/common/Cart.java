package com.example.restaurantdemo.common;

import com.example.restaurantdemo.model.SizeMenuModel;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private String email = null;
    private Map<String, SizeMenuModel> cart = null;

    public Cart(String email) {
        this.email = email;
        this.cart = new HashMap<String, SizeMenuModel>();
    }

    public String getEmail() {
        return email;
    }

    public Map<String, SizeMenuModel> getCart() {
        return cart;
    }

    public void addItem(SizeMenuModel item) {
        this.cart.put(item.getItemNo(), item);
    }

    public void updateItem(SizeMenuModel item) {
        if (this.cart.containsKey(item.getItemNo())) {
            if (item.getQty() <= 0) {
                this.cart.remove(item.getItemNo());
            } else {
                SizeMenuModel smm = this.cart.get(item.getItemNo());
                smm.setQty(item.getQty());

                this.cart.put(item.getItemNo(), smm);
            }
        }
    }

    public void clearCart() {
        this.cart.clear();
    }
}
