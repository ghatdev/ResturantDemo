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

public class SigninAsyncTask extends AsyncTask<String, Void, String> {
    private Context context;

    public SigninAsyncTask(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... params) {
        JSONObject json = null;

        try {
            String result = signin(params[0], params[1], params[2]);
            json = new JSONObject(result);
        } catch (JSONException ex) {
            Log.e(AppLog.TAG, ex.getMessage());
        }

        Log.d(AppLog.TAG, json.toString());

        return json.toString();
    }

    /*
     * Server address: https://myrest-android.herokuapp.com
     * DB URL on the Server: jdbc:mysql://ec2-18-191-145-237.us-east-2.compute.amazonaws.com:3306/myrest
     * DB User ID for TEST: root
     */
    private String signin(String server, String email, String pwd) {
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("email", email.trim());
            params.put("pwd", pwd.trim());

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

            URL url = new URL((server != null ? server : context.getString(R.string.server_url)) + "/signin");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
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
            ex.printStackTrace();
            Log.e(AppLog.TAG, ex.getMessage());
            return null;
        }
    }

}
