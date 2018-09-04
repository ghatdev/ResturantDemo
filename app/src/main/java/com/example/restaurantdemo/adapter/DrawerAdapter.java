package com.example.restaurantdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantdemo.R;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyViewHolder> {

    private Context context;
    private String[] itemName;
    private int[] itemImage;
    private ItemClickListener clickListener;
    private int lastPosition = -1;

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public DrawerAdapter(Context context, String[] itemName, int[] itemImage) {
        this.context = context;
        this.itemName = itemName;
        this.itemImage = itemImage;
    }

    @Override
    public DrawerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.MyViewHolder holder, int position) {

        holder.title.setText(itemName[position]);

        holder.thumbnail.setImageResource(itemImage[position]);
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return itemImage.length;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.navigation_item_tvTitle);
            thumbnail = (ImageView) itemView.findViewById(R.id.navigation_item_ivImage);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }

        public void clearAnimation() {
            itemView.clearAnimation();
        }
    }

    @Override
    public void onViewDetachedFromWindow(final MyViewHolder holder) {
        ((MyViewHolder) holder).clearAnimation();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.flip_anim);
            viewToAnimate.startAnimation(animation);

            lastPosition = position;
        }
    }


}
