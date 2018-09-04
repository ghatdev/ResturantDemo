package com.example.restaurantdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantdemo.R;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        tvToolbarTitle = (TextView) findViewById(R.id.toolbar_tv_title);
        tvToolbarTitle.setText("비밀번호 초기화");
        ivBack = (ImageView) findViewById(R.id.toolbar_iv_back);

        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }
}
