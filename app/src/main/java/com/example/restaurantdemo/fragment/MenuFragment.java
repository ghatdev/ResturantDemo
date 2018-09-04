package com.example.restaurantdemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.activity.SubCategoryMenuActivity;
import com.example.restaurantdemo.adapter.MenuAdapter;
import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.model.MenuModel;
import com.example.restaurantdemo.server.FavoriteAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends BaseFragment implements MenuAdapter.ItemClickListener {

    private MenuAdapter menuadapter;
    private List<MenuModel> menuList;
    private RecyclerView recyclerView;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        MyFavoriteTask task = new MyFavoriteTask(getActivity());
//        task.execute("");

        InitView();
    }

    private void InitView() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        menuList = new ArrayList<>();
        menuadapter = new MenuAdapter(getActivity(), menuList, false);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(menuadapter);
        menuadapter.setClickListener(MenuFragment.this);

        prepareAlbum();

    }

    private void prepareAlbum() {
        menuList.add(new MenuModel(1, "햄버거", "4개 메뉴", Const.MENU_IMG[0]));
        menuList.add(new MenuModel(2, "한식", "4개 메뉴", Const.MENU_IMG[1]));
        menuList.add(new MenuModel(3, "중식", "3개 메뉴", Const.MENU_IMG[2]));
        menuList.add(new MenuModel(4, "일식", "5개 메뉴", Const.MENU_IMG[3]));
        menuList.add(new MenuModel(5, "커피", "6개 메뉴", Const.MENU_IMG[4]));
        menuList.add(new MenuModel(6, "디저트", "4개 메뉴", Const.MENU_IMG[5]));

        menuadapter.notifyDataSetChanged();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources resources = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(getActivity(), SubCategoryMenuActivity.class);
        intent.putExtra("menu_position", position);
        startActivity(intent);
    }

    class MyFavoriteTask extends FavoriteAsyncTask {
        Context context;

        public MyFavoriteTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json =  new JSONObject(result);

                if (json == null) {
                    Toast.makeText(getActivity(), "Server message=" + result, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    if (json.getString("Result").equals("OK")) {
                        Toast.makeText(getActivity(), "json=" + json, android.widget.Toast.LENGTH_LONG).show();

                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
