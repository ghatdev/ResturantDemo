package com.example.restaurantdemo.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.adapter.SubMenuAdapter;
import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.common.SubMenuArray;
import com.example.restaurantdemo.model.SubMenuModel;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryMenuActivity extends AppCompatActivity implements SubMenuAdapter.ItemClickListener, View.OnClickListener {
    private static int MENU_POSITION;


    private RecyclerView recyclerView;
    private SubMenuAdapter adapter;
    private TextView tvToolbarTitle;
    private ImageView ivToolbarBack;
    private ImageView ivBackdrop;
    private List<SubMenuModel> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_menu);

        Intent intent = getIntent();
        MENU_POSITION = intent.getIntExtra("menu_position", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivBackdrop = (ImageView)findViewById(R.id.backdrop);
        ivBackdrop.setImageResource(Const.MENU_IMG[MENU_POSITION]);

        ivToolbarBack = (ImageView) findViewById(R.id.toolbar_iv_back);
        ivToolbarBack.setOnClickListener(this);
        tvToolbarTitle = (TextView) findViewById(R.id.toolbar_collapse_tv_title);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new SubMenuAdapter(this, albumList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);

        prepareAlbum();

    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle("Coffee");
                    tvToolbarTitle.setText(Const.MENU[MENU_POSITION]);
                    ivToolbarBack.setVisibility(View.VISIBLE);
                    collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.black));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("  ");
                    isShow = false;
                }
            }
        });
    }

    private void prepareAlbum() {
        SubMenuModel[] subMenu = SubMenuArray.SUB_MENU[MENU_POSITION];

        for (SubMenuModel menu : subMenu) {
            albumList.add(menu);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(SubCategoryMenuActivity.this, FullDetailActivity.class);
        intent.putExtra("MENU", MENU_POSITION);
        intent.putExtra("SUB_MENU", position);

        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == ivToolbarBack) {
            finish();
        }
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
}
