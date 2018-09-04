package com.example.restaurantdemo.common;

import android.app.Application;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ApplicationShare extends Application {
    private final String TAG = "MYREST-APPLICATION";

    private Map<String, Cart> cartMap = null;

    private String loginEmail = null;
    private String loginName = null;
    private String loginTel = null;

    @Override
    public void onCreate() {
        super.onCreate();
        cartMap = new HashMap<>();
        Log.v(TAG,"--- onCreate() in ---");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.v(TAG,"--- onTerminate() in ---");
    }

    public Cart getCart(String email) {
        if (cartMap.containsKey(email)) {
            return cartMap.get(email);
        } else {
            Cart cart = new Cart(email);
            cartMap.put(email, cart);
            return cart;
        }
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginTel() {
        return loginTel;
    }

    public void setLoginTel(String loginTel) {
        this.loginTel = loginTel;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }
}
