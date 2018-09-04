package com.example.restaurantdemo.cp;

import android.net.Uri;
import android.provider.BaseColumns;

public class CardColumns implements BaseColumns {
    // Authority
    public static final String AUTHORITY = "com.example.restaurantdemo";
    // URI path
    public static final String PATH = "card";
    // ContentURI
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);
    public static final Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY + "/" + PATH + "s");
    // Content Type I (1)
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.example.card";
    // Content Type II (n)
    public static final String CONTENT_DIR_TYPE = "vnd.android.cursor.dir/vnd.example.card";
    // Table name
    public static final String TABLE = "CARD_TX";
    // Column
    public static final String EMAIL = "email";
    public static final String CARDNO = "cardno";
    public static final String CARDHOLDER = "cardholder";
    public static final String CARDEXPIRE = "cardexpire";
    public static final String CARDCVC = "cardcvc";

    private CardColumns() {}
}
