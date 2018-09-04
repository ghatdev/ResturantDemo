package com.example.restaurantdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.adapter.ProductSizeAdapter;
import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.common.DBInitHelper;
import com.example.restaurantdemo.common.SubMenuArray;
import com.example.restaurantdemo.fragment.CartFragment;
import com.example.restaurantdemo.fragment.FragmentHandler;
import com.example.restaurantdemo.model.SizeMenuModel;
import com.example.restaurantdemo.server.SubMenuAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FullDetailActivity extends AppCompatActivity implements ProductSizeAdapter.ItemClickListener, View.OnClickListener {

    private Toolbar toolbar;
    private TextView title, itemName, itemPrice, tvDetail;
    private LinearLayout layoutMain;
    private ImageView ivToolbarBack, subMenuImg;
    private RecyclerView recyclerView;
    private ProductSizeAdapter adapter;
    private ArrayList<SizeMenuModel> listItem;
    private Button btnPlaceOrder;
    private FragmentHandler fragmentHandler;
    private android.app.FragmentManager fragmentManager;

    private FrameLayout frameLayout;

    private int MENU_POS, SUB_MENU_POS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_detail);

        Intent intent = getIntent();
        MENU_POS = intent.getIntExtra("MENU", 0);
        SUB_MENU_POS = intent.getIntExtra("SUB_MENU", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        subMenuImg = (ImageView) findViewById(R.id.sub_menu_img);
        title = (TextView) findViewById(R.id.toolbar_tv_title);
        itemName = (TextView) findViewById(R.id.item_name);
        itemPrice = (TextView) findViewById(R.id.item_price);
        tvDetail = (TextView) findViewById(R.id.tv_detail);

        title.setText(SubMenuArray.SUB_MENU[MENU_POS][SUB_MENU_POS].getTitle());
        itemName.setText(SubMenuArray.SUB_MENU[MENU_POS][SUB_MENU_POS].getDetail());
        itemPrice.setText(SubMenuArray.SUB_MENU[MENU_POS][SUB_MENU_POS].getPrice());
        tvDetail.setText(SubMenuArray.SUB_MENU_DESC[MENU_POS][SUB_MENU_POS]);
        subMenuImg.setImageResource(SubMenuArray.SUB_MENU[MENU_POS][SUB_MENU_POS].getImageID());

        layoutMain = (LinearLayout) findViewById(R.id.layout_linear);
        layoutMain.setVisibility(View.VISIBLE);

        ivToolbarBack = (ImageView) findViewById(R.id.toolbar_iv_back);
        ivToolbarBack.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.full_detail_recycler_view);
        listItem = prepareAlbum(); // new ArrayList<>();
        adapter = new ProductSizeAdapter(this, listItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);
//        prepareAlbum();

        fragmentHandler = new FragmentHandler();
        fragmentManager = getFragmentManager();

        frameLayout = (FrameLayout) findViewById(R.id.full_detail_fragmentLayout);

        btnPlaceOrder = (Button) findViewById(R.id.btn_placeOrder);
        btnPlaceOrder.setOnClickListener(this);
    }

    private ArrayList<SizeMenuModel>  prepareAlbum() {
//        MySubMenuTask task = new MySubMenuTask(this);
//        task.execute("M" + (MENU_POS+1), String.valueOf(SUB_MENU_POS));

//        int[] covers = new int[]{
//                R.drawable.image_one,
//                R.drawable.image_two,
//                R.drawable.image_three,
//                R.drawable.image_four
//        };

//        listItem.add(new SizeMenuModel("M" + MENU_POS + "-" + SUB_MENU_POS + "-ITEM0000001", covers[0], "아이템1", "\u20A910000", "1"));
//        listItem.add(new SizeMenuModel("M" + MENU_POS + "-" + SUB_MENU_POS + "-ITEM0000002", covers[1], "아이템2", "\u20A912000", "1"));
//        listItem.add(new SizeMenuModel("M" + MENU_POS + "-" + SUB_MENU_POS + "-ITEM0000003", covers[2], "아이템3", "\u20A915000", "1"));
//        listItem.add(new SizeMenuModel("M" + MENU_POS + "-" + SUB_MENU_POS + "-ITEM0000004", covers[3], "아이템4", "\u20A920000", "1"));
        SharedPreferences mPref = getSharedPreferences( "Setting", Context.MODE_PRIVATE );
        DBInitHelper db = new DBInitHelper(mPref.getString(Const.DB_NAME, "/myrest.db"));
        db.openDatabase();
        ArrayList<SizeMenuModel> list = db.queryItemData("M" + (MENU_POS+1), String.valueOf(SUB_MENU_POS+1));
        db.closeDatabase();

        return list;
    }

    @Override
    public void onClick(View v) {
        if (v == ivToolbarBack) {
            finish();
        } else if (v == btnPlaceOrder) {
            title.setText("주문확인");
            fragmentHandler.replaceFragment(FullDetailActivity.this, R.id.full_detail_fragmentLayout, new CartFragment(), null, null, false, "CartFragment", 0, FragmentHandler.ANIMATION_TYPE.NONE);
            layoutMain.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view, int position) {

    }

    class MySubMenuTask extends SubMenuAsyncTask {
        Context context;

        public MySubMenuTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json =  new JSONObject(result);

                if (json == null) {
                    Toast.makeText(getApplicationContext(), "Server message=" + result, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    if (json.getString("Result").equals("OK")) {
                        Toast.makeText(getApplicationContext(), "json=" + json, android.widget.Toast.LENGTH_LONG).show();

//                        int[] covers = new int[]{
//                                R.drawable.image_one,
//                                R.drawable.image_two,
//                                R.drawable.image_three,
//                                R.drawable.image_four
//                        };
//
//                        JSONArray jsonArray = (JSONArray)json.get("Sub_Menu");
//                        if (jsonArray != null) {
//                            for (int i = 0; i > jsonArray.length(); i++) {
//                                JSONObject sub = (JSONObject) jsonArray.get(i);
//                                listItem.add(new SizeMenuModel(sub.get("item_no").toString(),
//                                        covers[0],
//                                        sub.get("title").toString(),
//                                        "\u20A9" + sub.get("price").toString(),
//                                        1));
//                            }
//
//                            adapter.notifyDataSetChanged();
//                        }
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
