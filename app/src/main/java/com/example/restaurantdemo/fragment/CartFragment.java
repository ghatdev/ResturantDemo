package com.example.restaurantdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.activity.CheckOutActivity;
import com.example.restaurantdemo.adapter.CartAdapter;
import com.example.restaurantdemo.common.ApplicationShare;
import com.example.restaurantdemo.common.Cart;
import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.model.CartModel;
import com.example.restaurantdemo.model.SizeMenuModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartFragment extends BaseFragment implements CartAdapter.ItemClickListener, View.OnClickListener {

    private View view;
    private CartAdapter cartAdapter;
    private List<CartModel> menuList;
    private RecyclerView recyclerView;
    private TextView tvItemCount, tvCheckout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_cart, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitView();
    }

    private void InitView() {

        recyclerView = (RecyclerView) getView().findViewById(R.id.cart_recycler_view);
        tvCheckout = (TextView) getView().findViewById(R.id.cart_checkout);
        tvItemCount = (TextView) getView().findViewById(R.id.cart_itemCount);

        menuList = new ArrayList<>();
        cartAdapter = new CartAdapter(getActivity(), menuList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.setClickListener(CartFragment.this);
        tvCheckout.setOnClickListener(this);

        prepareAlbum();

    }

    private void prepareAlbum() {
        ApplicationShare app = (ApplicationShare)getActivity().getApplication();
        if (app.getLoginEmail() == null) {
            Toast.makeText(getActivity(), "로그인을 해 주세요.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Cart cart = app.getCart(app.getLoginEmail());
            Map<String, SizeMenuModel> cartMap =  cart.getCart();
            Iterator<String> keyIt = cartMap.keySet().iterator();
            int count = 0, sum = 0;
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                SizeMenuModel value = cartMap.get(key);

                CartModel a = new CartModel(value.getCategory1(), value.getCategory2(), value.getItemNo(), value.getImageId(), value.getName(),
                        "SimpleText allows editing including text formatting", value.getPrice(), String.valueOf(value.getQty()));
                menuList.add(a);

                count++;
                sum += Integer.parseInt(value.getPrice().substring(1, value.getPrice().length())) * value.getQty();
            }

            cartAdapter.notifyDataSetChanged();

            tvItemCount.setText(String.format(Const.CART_COUNT_STR, String.valueOf(count), String.valueOf(sum)));
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        if (v == tvCheckout) {
            ApplicationShare app = (ApplicationShare)getActivity().getApplication();
            Cart cart = app.getCart(app.getLoginEmail());
            Map<String, SizeMenuModel> cartMap =  cart.getCart();

            if (cartMap.size() > 0) {
                Intent intent = new Intent(getActivity(), CheckOutActivity.class);
                getActivity().startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "결제할 상품이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
