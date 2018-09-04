package com.example.restaurantdemo.cp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.common.DBCheckoutHelper;

public class CardContentProvider extends ContentProvider {
    private static final int CARDS = 1;
    private static final int CARD_ID = 2;

    private static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CardColumns.AUTHORITY, "cards", CARD_ID);
        uriMatcher.addURI(CardColumns.AUTHORITY, "card", CARDS);

    }

    private CardDBHelper dbHelper;

    public boolean onCreate() {
        dbHelper = new CardDBHelper(getContext());
        return true;
    }

    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CARDS:
                return CardColumns.CONTENT_DIR_TYPE;
            case CARD_ID:
                return CardColumns.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }


    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SharedPreferences mPref = getContext().getSharedPreferences( "Setting", Context.MODE_PRIVATE );
        DBCheckoutHelper db = new DBCheckoutHelper(mPref.getString(Const.DB_NAME, "/myrest.db"));
        db.openDatabase();

        if (uriMatcher.match(uri) == CARDS || uriMatcher.match(uri) == CARD_ID) {
            Cursor cursor = db.queryCardInfo4CP();

//            db.closeDatabase();
            return cursor;

        } else {
            db.closeDatabase();
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
