package com.example.restaurantdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.restaurantdemo.R;
import com.example.restaurantdemo.model.MenuModel;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context context;
    private List<MenuModel> albumList;
    private boolean isFavorite = false;
    private ItemClickListener clickListener;

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public MenuAdapter(Context context, List<MenuModel> albumList, boolean isFavorite) {
        this.context = context;
        this.albumList = albumList;
        this.isFavorite = isFavorite;
    }

    @Override
    public MenuAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuAdapter.MyViewHolder holder, int position) {

        MenuModel album = albumList.get(position);
        holder.title.setText(album.getTitle());
        holder.count.setText(album.getDetail());

        Glide.with(context).load(album.getImageID()).placeholder(R.drawable.image_one).into(holder.thumbnail);
        if (isFavorite) {
            holder.ivlike.setBackground(context.getResources().getDrawable(R.drawable.like_heart));
        } else {
            holder.ivlike.setBackground(context.getResources().getDrawable(R.drawable.ic_heart_outline_grey));
        }

        // retrieve favorite count
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, count, total_fav_cnt;
        public ImageView thumbnail, ivlike;


        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            count = (TextView) itemView.findViewById(R.id.count);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            ivlike = (ImageView) itemView.findViewById(R.id.iv_favourite);
            total_fav_cnt = (TextView) itemView.findViewById(R.id.total_fav_cnt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }
}
