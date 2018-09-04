package com.example.restaurantdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.model.OrderModel;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Context context;
    private List<OrderModel> albumList;
    private ItemClickListener clickListener;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public OrderAdapter(Context context, List<OrderModel> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        OrderModel album = albumList.get(position);

        holder.title.setText(album.getItemName());
        holder.detail.setText(album.getItemDetail());
        holder.price.setText(album.getItemPrice());
        holder.quantity.setText(album.getItemQuantity());
        holder.total.setText(album.getItemTotalPrice());

        holder.thumbnail.setImageResource(album.getImageId());
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, detail, price, quantity, total;
        ImageView thumbnail;

        MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.order_itemName);
            detail = (TextView) itemView.findViewById(R.id.order_itemDetail);
            price = (TextView) itemView.findViewById(R.id.order_itemPrice);
            quantity = (TextView) itemView.findViewById(R.id.order_itemQuantity);
            total = (TextView) itemView.findViewById(R.id.order_tvGrandTotal);

            thumbnail = (ImageView) itemView.findViewById(R.id.order_ivImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
        }
    }

}
