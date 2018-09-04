package com.example.restaurantdemo.server;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurantdemo.common.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenuAsyncTask extends AsyncTask<Void, Void, String> {
    private final Context context;

    public MenuAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL("");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }

            conn.disconnect();
        } catch (Exception ex) {
            Log.e(AppLog.TAG, ex.getMessage());
        }

        JSONObject json = null;
        try {
            json = new JSONObject(sb.toString());
        } catch (JSONException ex) {
            Log.e(AppLog.TAG, ex.getMessage());
        }

        return json.toString();
    }

}
