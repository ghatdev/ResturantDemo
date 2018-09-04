package com.example.restaurantdemo.cp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.restaurantdemo.common.Const;

public class CardDBHelper extends SQLiteOpenHelper {
    private static String DB_NAME;
    private static final int DB_VERSION = 1;

    public CardDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        SharedPreferences mPref = context.getSharedPreferences( "Setting", Context.MODE_PRIVATE );
        DB_NAME = mPref.getString(Const.DB_NAME, "/myrest.db");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createItemSql =  "CREATE TABLE IF NOT EXISTS ITEM_TX ("
                + "IDX INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + "CATEGORY1 TEXT NOT NULL, CATEGORY2 TEXT NOT NULL, ITEM_NO TEXT NOT NULL, "
                + "TITLE TEXT, DESCRIPTION TEXT, PRICE INTEGER, IMG TEXT, INS TEXT);";

        sqLiteDatabase.execSQL(createItemSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
