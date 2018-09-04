package com.example.restaurantdemo.common;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.restaurantdemo.model.SizeMenuModel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class DBInitHelper {
    public static final String TAG = "MyRestaurantDB";

    private String databaseFile = null;
    private SQLiteDatabase db;

    public DBInitHelper(String databaseFile) {
        this.databaseFile = databaseFile;
    }

    public void openDatabase() {
        println("creating or opening database [" + databaseFile + "].");
        db = SQLiteDatabase.openDatabase(
                databaseFile, null, SQLiteDatabase.OPEN_READWRITE + SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void closeDatabase() {
        try {
            db.close();
        } catch(Exception ext) {
            ext.printStackTrace();
            println("Exception in closing database : " + ext.toString());
        }
    }

    public void initialize(InputStream is) {
        openDatabase();

        try {
            String createCardSql =  "CREATE TABLE IF NOT EXISTS CARD_TX ("
                    + "IDX INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                    + "EMAIL VARCHAR(50) NOT NULL, "
                    + "CARDNO TEXT NOT NULL, CARDHOLDER TEXT NOT NULL, "
                    + "CARDEXPIRE TEXT NOT NULL, CARDCVC VARCHAR(5) NOT NULL);";
            db.execSQL(createCardSql);

            String createItemSql =  "CREATE TABLE IF NOT EXISTS ITEM_TX ("
                                + "IDX INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                                + "CATEGORY1 TEXT NOT NULL, CATEGORY2 TEXT NOT NULL, ITEM_NO TEXT NOT NULL, "
                                + "TITLE TEXT, DESCRIPTION TEXT, PRICE INTEGER, IMG TEXT, INS TEXT);";
            db.execSQL(createItemSql);

            Cursor c = db.rawQuery("SELECT COUNT(IDX) FROM  ITEM_TX", null);
            c.moveToFirst();
            int count = c.getInt(0);
            if (count == 0) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int i;
                String insertSql = null;

                i = is.read();
                while (i != -1) {
                    byteArrayOutputStream.write(i);
                    i = is.read();
                }

                insertSql = new String(byteArrayOutputStream.toByteArray(), "utf-8");
                is.close();

                db.beginTransaction();
                db.execSQL(insertSql);
                db.setTransactionSuccessful();
                db.endTransaction();
            }

            c.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        closeDatabase();
    }

    public void println(String msg) {
        Log.d(TAG, msg);
    }

    public ArrayList<SizeMenuModel> queryItemData(String category1, String category2) {
        String aSQL = "select IDX, CATEGORY1, CATEGORY2, ITEM_NO, TITLE, DESCRIPTION, PRICE, IMG "
                + " from ITEM_TX"
                + " where CATEGORY1=? and CATEGORY2=?";

        String[] args = {category1, category2};

        Cursor cursor = db.rawQuery(aSQL, args);

        int recordCount = cursor.getCount();
        println("cursor count : " + recordCount + "\n");

        ArrayList<SizeMenuModel> listItem = new ArrayList<>();

        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();

            listItem.add(new SizeMenuModel(cursor.getString (1), cursor.getString (2),
                    cursor.getString (3), ItemArray.ITEMS.get(cursor.getString (7)),
                    cursor.getString (4), "\u20A9" + cursor.getString (6), 1));
        }

        cursor.close();

        return listItem;
    }

    public ArrayList<SizeMenuModel> queryItemDetail(String[] item_nos) {
        String aSQL = "select IDX, CATEGORY1, CATEGORY2, ITEM_NO, TITLE, DESCRIPTION, PRICE, IMG "
                + " from ITEM_TX"
                + " where ITEM_NO IN (%s)";

        StringBuilder sb = new StringBuilder();
        for (String item_no : item_nos) {
            sb.append("'" + item_no + "',");
        }

        Cursor cursor = db.rawQuery(String.format(aSQL, sb.toString().substring(0, sb.toString().length() - 1)), null);

        int recordCount = cursor.getCount();
        println("cursor count : " + recordCount + "\n");

        ArrayList<SizeMenuModel> listItem = new ArrayList<>();

        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();

            listItem.add(new SizeMenuModel(cursor.getString (1), cursor.getString (2),
                        cursor.getString (3), ItemArray.ITEMS.get(cursor.getString (7)),
                    cursor.getString (4), "\u20A9" + cursor.getString (6), 1));
        }

        cursor.close();

        return listItem;
    }
}
