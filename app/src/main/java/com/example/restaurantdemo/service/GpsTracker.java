package com.example.restaurantdemo.service;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.restaurantdemo.server.GpsAsyncTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GpsTracker extends Service implements LocationListener {

  private final Context mContext;

  boolean isGPSEnabled = false;
  boolean isNetworkEnabled = false;

  boolean isGetLocation = false;

  Location location;
  double lat; // 위도
  double lon; // 경도

  private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
  private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

  protected LocationManager locationManager;

  private static final AsyncHttpClient client = new AsyncHttpClient();

  public GpsTracker() {
    this.mContext = null;
  }

  public GpsTracker(Context context) {
    this.mContext = context;
    getLocation();
  }

  @TargetApi(23)
  public Location getLocation() {
    if ( Build.VERSION.SDK_INT >= 23 &&
          ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
          ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return null;
    }

    try {
      locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
      isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

      isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

      if (!isGPSEnabled && !isNetworkEnabled) {
      } else {
        this.isGetLocation = true;
        if (isNetworkEnabled) {
          locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME_BW_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

          if (locationManager != null) {
            location = locationManager
              .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
              lat = location.getLatitude();
              lon = location.getLongitude();
            }
          }
        }

        if (isGPSEnabled) {
          if (location == null) {
            locationManager.requestLocationUpdates(
              LocationManager.GPS_PROVIDER,
              MIN_TIME_BW_UPDATES,
              MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (locationManager != null) {
              location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
              if (location != null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
              }
            }
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return location;
  }

  public void stopUsingGPS(){
    if(locationManager != null){
      locationManager.removeUpdates(GpsTracker.this);
    }
  }

  public double getLatitude(){
    if(location != null){
      lat = location.getLatitude();
    }
    return lat;
  }

  public double getLongitude(){
    if(location != null){
      lon = location.getLongitude();
    }
    return lon;
  }

  public boolean isGetLocation() {
    return this.isGetLocation;
  }

  public void showSettingsAlert(){
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

    alertDialog.setTitle("GPS 사용유무셋팅");
    alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. \n 설정창으로 가시겠습니까?");

    // OK 를 누르게 되면 설정창으로 이동합니다.
    alertDialog.setPositiveButton("Settings",
      new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          mContext.startActivity(intent);
        }
      });
    // Cancle 하면 종료 합니다.
    alertDialog.setNegativeButton("Cancel",
      new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
        }
      });

    alertDialog.show();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    double lat = intent.getDoubleExtra("latitude", 0.0);
    double lng = intent.getDoubleExtra("longitude", 0.0);

    GpsAsyncTask gpsService =  new GpsAsyncTask(getApplicationContext());
    gpsService.execute(lat, lng);

    return START_STICKY;
  }

  public void sendGeoInfo(String serverUrl) {
    Location location = getLocation();

    Log.d("GPS Tracker", "Latitude=" + String.valueOf(location.getLatitude()));
    Log.d("GPS Tracker", "Longitude=" + String.valueOf(location.getLongitude()));


    RequestParams reqParams = new RequestParams();
    reqParams.put("latitude", String.valueOf(location.getLatitude()));
    reqParams.put("longitude", String.valueOf(location.getLongitude()));
    client.get(serverUrl + "/geo", reqParams, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("GPS Tracker", "OK");
      }

      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d("GPS Tracker", "OK");
      }

      @Override
      protected Object parseResponse(byte[] responseBody) throws JSONException {
        return super.parseResponse(responseBody);
      }
    });
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }

  public void onLocationChanged(Location location) {
    // TODO Auto-generated method stub

  }

  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub

  }

  public void onProviderEnabled(String provider) {
    // TODO Auto-generated method stub

  }

  public void onProviderDisabled(String provider) {
    // TODO Auto-generated method stub

  }
}
