package com.example.restaurantdemo.server;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.common.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubMenuAsyncTask extends AsyncTask<String, Void, String> {
    private Context context;

    public SubMenuAsyncTask(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... params) {
        JSONObject json = null;

        try {
            String result = subMenuList(params[0], params[1]);
            json = new JSONObject(result);
        } catch (JSONException ex) {
            Log.e(AppLog.TAG, ex.getMessage());
        }

        Log.d(AppLog.TAG, json.toString());

        return json.toString();
    }

    private String subMenuList(String category1, String category2) {
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("category1", category1);
            params.put("category2", category2);

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

            URL url = new URL(context.getString(R.string.server_url) + "/subMenuList");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.getOutputStream().write(postDataBytes);

            int responseCode = conn.getResponseCode();
            StringBuilder sb = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }

            conn.disconnect();

            return sb.toString();
        } catch (Exception ex) {
            Log.e(AppLog.TAG, ex.getMessage());
            return null;
        }
    }

}
