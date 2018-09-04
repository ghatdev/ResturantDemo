package com.example.restaurantdemo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.adapter.DrawerAdapter;
import com.example.restaurantdemo.fragment.BookingFragment;
import com.example.restaurantdemo.fragment.CartFragment;
import com.example.restaurantdemo.fragment.ContactUSFragment;
import com.example.restaurantdemo.fragment.FragmentHandler;
import com.example.restaurantdemo.fragment.MenuFragment;
import com.example.restaurantdemo.fragment.OrderFragment;
import com.example.restaurantdemo.fragment.ProfileFragment;
import com.example.restaurantdemo.service.GpsTracker;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.ItemClickListener {
    private Toolbar toolbar;
    private TextView tvTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private LinearLayout layout;
    private RecyclerView recyclerView;
    private DrawerAdapter drawerAdapter;
    private String[] drawerTitle = {"메뉴", "예약", "장바구니", "프로필", "즐겨찾기", "Contact us"};
    private FirebaseAnalytics firebaseAnalytics;
    private int[] drawerImage = {
            R.drawable.menu,
            R.drawable.booking,
            R.drawable.cart,
            R.drawable.profile,
            R.drawable.favourite,
            R.drawable.contact_us
    };

    private FragmentHandler fragmentHandler;
    private FragmentManager fragmentManager;

    private GpsTracker gps;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private final int PERMISSIONS_ACCESS_CALL_PHONE = 1002;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isAccessCallPhone = false;
    private boolean isPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        layout = (LinearLayout) findViewById(R.id.main_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("메뉴");

        recyclerView = (RecyclerView) findViewById(R.id.navigation_recycler_view);
        drawerAdapter = new DrawerAdapter(this, new String[]{"메뉴", "예약", "장바구니", "프로필", "Contact us"},
                new int[]{R.drawable.menu, R.drawable.booking, R.drawable.cart, R.drawable.profile, R.drawable.contact_us});
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(drawerAdapter);

        drawerAdapter.setClickListener(this);

        fragmentHandler = new FragmentHandler();
        fragmentManager = getFragmentManager();

        toolbar.setVisibility(View.VISIBLE);
        fragmentHandler.addFragment(this, R.id.fragmentLayout, new MenuFragment(), null, null, false, "MenuFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawe_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                layout.setTranslationX(slideOffset * drawerView.getWidth());
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();

            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.setMinimumSessionDuration(20000);
        firebaseAnalytics.setSessionTimeoutDuration(500);

        callPermission();
        getGpsInfo();
    }

    private void getGpsInfo() {
        if (!isPermission) {
            callPermission();
            return;
        }

        gps = new GpsTracker(MainActivity.this);
        if (gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Log.d("GPS Tracker", String.valueOf(latitude));
            Log.d("GPS Tracker", String.valueOf(longitude));

            Intent serviceIntent = new Intent(this, GpsTracker.class);
            serviceIntent.putExtra("latitude", latitude);
            serviceIntent.putExtra("longitude", longitude);
            startService(serviceIntent);
        } else {
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            return;
        }

        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        } else if (requestCode == PERMISSIONS_ACCESS_CALL_PHONE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCallPhone = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation && isAccessCallPhone) {
            isPermission = true;
        }
    }

    private void callPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSIONS_ACCESS_CALL_PHONE);
        } else {
            isPermission = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_order:
                getSupportActionBar().setTitle("주문내역");
                fragmentHandler.replaceFragment(MainActivity.this, R.id.fragmentLayout, new OrderFragment(), null, null, false, "FavouriteFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
                break;
            case R.id.menu_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("확인");
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setNegativeButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.setPositiveButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNavigationDrawer(int position) {
        switch (position) {
            case 0:
                getSupportActionBar().setTitle("메뉴");
                fragmentHandler.replaceFragment(MainActivity.this, R.id.fragmentLayout, new MenuFragment(), null, null, false, "MenuFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
                drawerLayout.closeDrawers();
                break;
            case 1:
                getSupportActionBar().setTitle("예약");
                fragmentHandler.replaceFragment(MainActivity.this, R.id.fragmentLayout, new BookingFragment(), null, null, false, "BookingFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
                drawerLayout.closeDrawers();
                break;
            case 2:
                getSupportActionBar().setTitle("장바구니");
                fragmentHandler.replaceFragment(MainActivity.this, R.id.fragmentLayout, new CartFragment(), null, null, false, "CartFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
                drawerLayout.closeDrawers();
                break;
            case 3:
                getSupportActionBar().setTitle("프로필");
                fragmentHandler.replaceFragment(MainActivity.this, R.id.fragmentLayout, new ProfileFragment(), null, null, false, "ProfileFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
                drawerLayout.closeDrawers();
                break;
//            case 4:
//                getSupportActionBar().setTitle("즐겨찾기");
//                fragmentHandler.replaceFragment(MainActivity.this, R.id.fragmentLayout, new FavouriteFragment(), null, null, false, "FavouriteFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
//                drawerLayout.closeDrawers();
//                break;
            case 4:
                getSupportActionBar().setTitle("Contact us");
                fragmentHandler.replaceFragment(MainActivity.this, R.id.fragmentLayout, new ContactUSFragment(), null, null, false, "ContactUSFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
                drawerLayout.closeDrawers();
                break;
        }
    }

    public void setToolbar() {
        if (toolbar.getTag().equals("profile")) {
            layout.setBackgroundResource(R.drawable.image_six);
            toolbar.setBackgroundColor(0);
            toolbar.setPopupTheme(R.style.Custome_toolbar);
            tvTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvTitle.setText("Profile".toUpperCase());
            toolbar.setTag("menu");
        } else {
            layout.setBackgroundResource(0);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setPopupTheme(0);
            tvTitle.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.action_back_message), android.widget.Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    @Override
    public void onClick(View view, int position) {
        initNavigationDrawer(position);
    }
}
