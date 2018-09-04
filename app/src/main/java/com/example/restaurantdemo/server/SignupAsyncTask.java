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
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SignupAsyncTask extends AsyncTask<String, Void, String> {
    private Context context;

    public SignupAsyncTask(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... params) {
        JSONObject json = null;

        try {
            String result = signup(params);
            json = new JSONObject(result);
        } catch (JSONException ex) {
            Log.e(AppLog.TAG, ex.getMessage());
        }

        Log.d(AppLog.TAG, json.toString());

        return json.toString();
    }

    private String signup(String... params) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("email", params[0]);
            map.put("pwd", params[1]);
            map.put("name", params[2]);
            map.put("tel", params[3]);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : map.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }

                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }

            JSONObject json = new JSONObject(map);
            URL url = new URL(context.getString(R.string.server_url) + "/signup");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintStream ps = new PrintStream(conn.getOutputStream());
            ps.print(json.toString());
            ps.close();

            int responseCode = conn.getResponseCode();
            StringBuilder sb = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } else {
                Log.e(AppLog.TAG, "NG=" + responseCode);
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
