package com.example.restaurantdemo.server;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.common.AppLog;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class GpsAsyncTask extends AsyncTask<Double, Void, Void> {
    String serverUrl = null;
    private Context context;

    public GpsAsyncTask(Context context) {
        this.context = context;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    protected Void doInBackground(Double... loc) {
        try {
//            Location location = gps[0].getLocation();

            Map<String, String> params = new LinkedHashMap<>();
            params.put("latitude", String.valueOf(loc[0]));
            params.put("longitude", String.valueOf(loc[1]));

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }

                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            String urlStr = context.getString(R.string.server_url) != null ? context.getString(R.string.server_url) : serverUrl;
            URL url = new URL(urlStr + "/geo");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.getOutputStream().write(postDataBytes);

            int responseCode = conn.getResponseCode();
            StringBuilder sb = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("GPS Tracker", "OK");
            }

            conn.disconnect();
            Log.d("GPS Tracker", new String(postDataBytes));
        } catch (Exception ex) {
            Log.e(AppLog.TAG, ex.getMessage());
        }

        return null;
    }

}
