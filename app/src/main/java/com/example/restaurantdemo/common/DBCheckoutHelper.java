package com.example.restaurantdemo.common;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.restaurantdemo.model.CardModel;

public class DBCheckoutHelper {
    public static final String TAG = "MyRestaurantDB";

    private String databaseFile = null;
    private SQLiteDatabase db;

    public DBCheckoutHelper(String databaseFile) {
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

    public void println(String msg) {
        Log.d(TAG, msg);
    }

    public CardModel queryCardInfo(String email) {
        String aSQL = "select EMAIL, CARDNO, CARDHOLDER, CARDEXPIRE, CARDCVC"
                + " from CARD_TX"
                + " where EMAIL=?";

        String[] args = {email};

        Cursor cursor = db.rawQuery(aSQL, args);

        int recordCount = cursor.getCount();
        println("cursor count : " + recordCount + "\n");

        if (recordCount > 0) {
            cursor.moveToNext();

            CardModel card = new CardModel(cursor.getString (0), cursor.getString (1), cursor.getString (2),
                    cursor.getString (3), cursor.getString (4));

            return card;
        } else {
            cursor.close();
            return null;
        }
    }

    public Cursor queryCardInfo4CP() {
        String aSQL = "select EMAIL, CARDNO, CARDHOLDER, CARDEXPIRE, CARDCVC"
                + " from CARD_TX";

        Cursor cursor = db.rawQuery(aSQL, null);
        return cursor;
    }

    public void saveCard(CardModel card) {
        CardModel dbCard = queryCardInfo(card.getEmail());

        if (dbCard != null && dbCard.getEmail() != null && dbCard.getEmail() .equals(card.getEmail())) {
            String sqlDelete = "DELETE FROM CARD_TX WHERE EMAIL='" + card.getEmail() + "';";
            db.execSQL(sqlDelete) ;
        }

        try {
            String sqlInsert = "INSERT INTO CARD_TX " + "(EMAIL, CARDNO, CARDHOLDER, CARDEXPIRE, CARDCVC) VALUES ("
                    + "'" + card.getEmail() + "', '" + DecCryptoUtil.ENCRYPT(card.getCardno()) + "', "
                    + "'" + card.getCardholder() + "', '" + card.getCardexpire() + "', "
                    + "'" + card.getCardcvc() + "');";
            db.execSQL(sqlInsert);
        } catch (Exception ex) {
            Log.e("Store Card Info", ex.getMessage());
        }
    }
}
