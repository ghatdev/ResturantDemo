package com.example.restaurantdemo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.adapter.OrderAdapter;
import com.example.restaurantdemo.common.ApplicationShare;
import com.example.restaurantdemo.common.ItemArray;
import com.example.restaurantdemo.model.OrderModel;
import com.example.restaurantdemo.server.OrderListAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends BaseFragment implements OrderAdapter.ItemClickListener {

    private View view;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitView();
    }

    private void InitView() {

        recyclerView = (RecyclerView) getView().findViewById(R.id.order_recycler_view);

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getActivity(), orderList);


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(orderAdapter);
        orderAdapter.setClickListener(OrderFragment.this);

        prepareAlbum();
    }

    private void prepareAlbum() {
        ApplicationShare app = (ApplicationShare)(this.getActivity()).getApplication();

        MyOrderListTask orderListTask = new MyOrderListTask(this.getActivity());
        orderListTask.execute(app.getLoginEmail());

//        int[] covers = new int[]{
//                R.drawable.image_six
//        };
//
//        OrderModel a = new OrderModel(covers[0], "Coffee", "SimpleText allows editing including text formatting", "\u20A9 250", "2", "\u20A9 500");
//        orderList.add(a);
//
//        orderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    class MyOrderListTask extends OrderListAsyncTask {
        Context context;

        public MyOrderListTask(Context context) {
            super(context);
            this.context = context;
        }

        ProgressDialog loading = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("주문 내역을 가져오고 있습니다...");
            loading.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json =  new JSONObject(result);

                if (json == null) {
                    Toast.makeText(context, "Server message=" + result, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    if (json.getString("result").equals("OK")) {
                        loading.dismiss();

                        JSONArray list = json.getJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject obj = (JSONObject)list.get(i);
                            OrderModel a = new OrderModel(ItemArray.ITEMS.get(obj.getString("img")), obj.getString("title"),
                                    "SimpleText allows editing including text formatting",
                                    "\u20A9" + obj.getString("price"), obj.getString("qty"), "\u20A9" + obj.getString("total"));
                            orderList.add(a);
                        }

                        orderAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
