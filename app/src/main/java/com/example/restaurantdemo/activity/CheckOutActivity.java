package com.example.restaurantdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.common.ApplicationShare;
import com.example.restaurantdemo.common.Cart;
import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.common.DBCheckoutHelper;
import com.example.restaurantdemo.common.DecCryptoUtil;
import com.example.restaurantdemo.model.CardModel;
import com.example.restaurantdemo.model.SizeMenuModel;
import com.example.restaurantdemo.server.CheckoutAsyncTask;
import com.example.restaurantdemo.service.GpsTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RelativeLayout shippinglayout, paymentlayout, confirmlayout;
    private LinearLayout shippingView, paymentView, confirmView;
    private ViewFlipper viewFlipper;
    private TextView tvTitle, tvShppingBottom, tvPaymentBottom, tvConfirmBottom;
    private ImageView ivBack;
    private EditText edName, edAddress, edTel;
    private EditText edCardNo, edCardHolder, edExpire, edCvc;
    private TextView tvOrderPrice, tvOrderNo;

    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        tvTitle = (TextView) findViewById(R.id.toolbar_tv_title);
        ivBack = (ImageView) findViewById(R.id.toolbar_iv_back);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        shippinglayout = (RelativeLayout) findViewById(R.id.relative_shpping);
        paymentlayout = (RelativeLayout) findViewById(R.id.relative_payment);
        confirmlayout = (RelativeLayout) findViewById(R.id.relative_confirm);

        shippingView = (LinearLayout) findViewById(R.id.linear_shippingView);
        paymentView = (LinearLayout) findViewById(R.id.linear_paymentView);
        confirmView = (LinearLayout) findViewById(R.id.linear_confirmView);

        tvPaymentBottom = (TextView) findViewById(R.id.checkout_payment_bottom_title);
        tvShppingBottom = (TextView) findViewById(R.id.checkout_shipping_bottom_title);
        tvConfirmBottom = (TextView) findViewById(R.id.checkout_confirm_bottom_title);

        edName = (EditText) findViewById(R.id.checkout_edFullName);
        edAddress = (EditText) findViewById(R.id.checkout_edAddress);
        edTel = (EditText) findViewById(R.id.checkout_edContactNo);

        edCardNo = (EditText) findViewById(R.id.checkout_payment_edcardNumber);
        edCardHolder = (EditText) findViewById(R.id.checkout_payment_edcardHolder);
        edExpire = (EditText) findViewById(R.id.checkout_payment_edexpireDate);
        edCvc = (EditText) findViewById(R.id.checkout_payment_edCVC);

        tvOrderPrice = (TextView) findViewById(R.id.checkout_order_no);
        tvOrderNo = (TextView) findViewById(R.id.checkout_order_price);

        loading = (ProgressBar) findViewById(R.id.pb_loading);

        viewFlipper.setDisplayedChild(0);

        tvTitle.setTypeface(Typeface.MONOSPACE);
        tvTitle.setText("결제");

        ivBack.setOnClickListener(this);

        shippingView.setOnClickListener(this);
        paymentView.setOnClickListener(this);
        confirmView.setOnClickListener(this);

        tvPaymentBottom.setOnClickListener(this);
        tvShppingBottom.setOnClickListener(this);
        tvConfirmBottom.setOnClickListener(this);

        setBasicInfo();
    }

    private void setBasicInfo() {
        ApplicationShare app = (ApplicationShare)getApplication();
        String email = app.getLoginEmail();
        String name = app.getLoginName();
        String tel = app.getLoginTel();

        edName.setText(name != null && !name.isEmpty() ? name : "");
        edTel.setText(tel != null && !tel.isEmpty() ? tel : "");

        // Card Info
        // Retrieve Card Info from DB Sqlite
        SharedPreferences mPref = getSharedPreferences( "Setting", Context.MODE_PRIVATE );
        DBCheckoutHelper db = new DBCheckoutHelper(mPref.getString(Const.DB_NAME, "/myrest.db"));
        db.openDatabase();
        CardModel card = db.queryCardInfo(email);
        db.closeDatabase();
        if (card != null && card.getEmail().equals(email)) {
            try {
                edCardNo.setText(DecCryptoUtil.DECRYPT(card.getCardno()));
                edCardHolder.setText(card.getCardholder());
                edExpire.setText(card.getCardexpire());
                edCvc.setText(card.getCardcvc());
            } catch (Exception ex) {
                Log.e("Card Set", ex.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == ivBack) {
            finish();
        } else if (v == tvShppingBottom) {
            if (checkShippingInfo()) {
                viewFlipper.setDisplayedChild(1);
            }
        } else if (v == tvPaymentBottom) {
            if (checkPaymentInfo()) {
//                viewFlipper.setDisplayedChild(2);
                HashMap<String, String> map = new HashMap<>();
                map.put("card_no", edCardNo.getText().toString());
                map.put("card_holder", edCardHolder.getText().toString());
                map.put("card_expire", edExpire.getText().toString());
                map.put("card_cvc", edCvc.getText().toString());
                map.put("name", edName.getText().toString());
                map.put("address", edAddress.getText().toString());
                map.put("tel", edTel.getText().toString());

                ApplicationShare app = (ApplicationShare)getApplication();
                Cart cart = app.getCart(app.getLoginEmail());
                Map<String, SizeMenuModel> cartMap =  cart.getCart();
                Iterator<String> keyIt = cartMap.keySet().iterator();
                StringBuilder itemNoSb = new StringBuilder();
                StringBuilder qtySb = new StringBuilder();
                StringBuilder priceSb = new StringBuilder();
                StringBuilder totalSb = new StringBuilder();
                while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    SizeMenuModel smm = cartMap.get(key);
                    itemNoSb.append(smm.getItemNo() + "|");
                    qtySb.append(smm.getQty() + "|");
                    priceSb.append(smm.getPrice() + "|");
                    totalSb.append((smm.getQty() * Integer.parseInt(smm.getPrice().substring(1, smm.getPrice().length()))) + "|");
                }

                map.put("email", app.getLoginEmail());
                map.put("item_nos", itemNoSb.toString());
                map.put("qtys", qtySb.toString());
                map.put("prices", priceSb.toString());
                map.put("totals", totalSb.toString());

                MyCheckoutTask checkoutTask = new MyCheckoutTask(this);
                checkoutTask.execute(map);
                loading.setVisibility(View.VISIBLE);
            }
        } else if (v == tvConfirmBottom) {
            Intent intent = new Intent(CheckOutActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (v == shippingView) {
            viewFlipper.setDisplayedChild(0);
        } else if (v == paymentView) {
            if (checkShippingInfo()) {
                viewFlipper.setDisplayedChild(1);
            }
        } /*else if (v == confirmView) {
            viewFlipper.setDisplayedChild(2);
        }*/
    }

    private boolean checkShippingInfo() {
        if (edName.getText() == null || edName.getText().toString().isEmpty()) {
            Toast.makeText(this, "배송지 정보 [성명]을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            edName.requestFocus();
            return false;
        }

        if (edAddress.getText() == null || edAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "배송지 정보 [주소]을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            edAddress.requestFocus();
            return false;
        }

        if (edTel.getText() == null || edTel.getText().toString().isEmpty()) {
            Toast.makeText(this, "배송지 정보 [연락처]을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            edTel.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkPaymentInfo() {
        if (edCardNo.getText() == null || edCardNo.getText().toString().isEmpty()) {
            Toast.makeText(this, "카드번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            edCardNo.requestFocus();
            return false;
        } else {
            if (edCardNo.getText().toString().length() != 16) {
                Toast.makeText(this, "카드번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                edCardNo.requestFocus();
                return false;
            }
        }

        if (edCardHolder.getText() == null || edCardHolder.getText().toString().isEmpty()) {
            Toast.makeText(this, "카드 소유자를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            edAddress.requestFocus();
            return false;
        }

        if (edExpire.getText() == null || edExpire.getText().toString().isEmpty()) {
            Toast.makeText(this, "만기일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            edExpire.requestFocus();
            return false;
        } else {
            if (edExpire.getText().toString().length() != 4) {
                Toast.makeText(this, "만기일을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                edExpire.requestFocus();
                return false;
            }

            String _mm = edExpire.getText().toString().substring(0, 2);
            int mm = Integer.parseInt(_mm);
            if (mm <= 0 || mm >= 13) {
                Toast.makeText(this, "만기일을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                edExpire.requestFocus();
                return false;
            }
        }

        if (edCvc.getText() == null || edCvc.getText().toString().isEmpty()) {
            Toast.makeText(this, "CVC를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            edCvc.requestFocus();
            return false;
        } else {
            if (edCvc.getText().toString().length() != 3) {
                Toast.makeText(this, "CVC를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                edCvc.requestFocus();
                return false;
            }
        }

        return true;
    }

    class MyCheckoutTask extends CheckoutAsyncTask {
        Context context;

        public MyCheckoutTask(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // DB initialize
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json =  new JSONObject(result);

                if (json == null) {
                    Toast.makeText(getApplicationContext(), "Server message=" + result, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    GpsTracker gps = new GpsTracker(CheckOutActivity.this);
                    gps.sendGeoInfo(context.getString(R.string.server_url));

                    Intent serviceIntent = new Intent(getApplicationContext(), GpsTracker.class);
                    serviceIntent.putExtra("latitude", gps.getLatitude());
                    serviceIntent.putExtra("longitude", gps.getLongitude());
                    startService(serviceIntent);

                    if (json.getString("result").equals("OK")) {
                        loading.setVisibility(View.GONE);
                        ApplicationShare app = (ApplicationShare)getApplication();
                        Cart cart = app.getCart(app.getLoginEmail());
                        cart.clearCart();

                        tvOrderPrice.setText(json.getString("order_no"));
                        tvOrderNo.setText("\u20A9" + json.getString("order_price"));
                        viewFlipper.setDisplayedChild(2);

                        // Save DB info to Local Sqlite
                        SharedPreferences mPref = getSharedPreferences( "Setting", Context.MODE_PRIVATE );
                        DBCheckoutHelper db = new DBCheckoutHelper(mPref.getString(Const.DB_NAME, "/myrest.db"));
                        db.openDatabase();
                        db.saveCard (new CardModel(app.getLoginEmail(), edCardNo.getText().toString(), edCardHolder.getText().toString(), edExpire.getText().toString(), edCvc.getText().toString()));
                        db.closeDatabase();
                    } else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "결제가 실패하였습니다. 다시 시도해 주싮시오.", android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
