package com.example.restaurantdemo.common;

public class AppLog {
    public static final String TAG = "Restaurant Demo";

    public static final boolean isDebug = true;

    public static final void Log(String tag, String message) {
        if (isDebug) {

            android.util.Log.e(tag, message + "");
        }
    }

    public static final void handleException(String tag, Exception e) {
        if (isDebug) {
            if (e != null) {
                android.util.Log.d(tag, e.getMessage() + "");
                e.printStackTrace();
            }
        }
    }
}
