package com.example.restaurantdemo.fragment;

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

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.activity.SubCategoryMenuActivity;
import com.example.restaurantdemo.adapter.MenuAdapter;
import com.example.restaurantdemo.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends BaseFragment implements MenuAdapter.ItemClickListener {

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
        InitView();
    }

    private void InitView() {

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        menuList = new ArrayList<>();
        menuadapter = new MenuAdapter(getActivity(), menuList,true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(menuadapter);
        menuadapter.setClickListener(FavouriteFragment.this);

        prepareAlbum();

    }

    private void prepareAlbum() {
        int[] covers = new int[]{
                R.drawable.image_one,
                R.drawable.image_two,
                R.drawable.image_three,
                R.drawable.image_four,
                R.drawable.image_five,
                R.drawable.image_six,

        };

        menuList.add(new MenuModel(1, "Coffee", "Freshly Coffee", covers[0]));
        menuList.add(new MenuModel(2, "Coffee", "Freshly Coffee", covers[1]));
        menuList.add(new MenuModel(3, "Coffee", "Freshly Coffee", covers[2]));
        menuList.add(new MenuModel(4, "Coffee", "Freshly Coffee", covers[3]));
        menuList.add(new MenuModel(5, "Coffee", "Freshly Coffee", covers[4]));

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
        // Toast.makeText(this, position + "", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), SubCategoryMenuActivity.class);
        startActivity(intent);
    }
}
