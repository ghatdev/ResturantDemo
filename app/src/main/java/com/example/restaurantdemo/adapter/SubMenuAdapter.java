package com.example.restaurantdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.restaurantdemo.R;
import com.example.restaurantdemo.model.SubMenuModel;

import java.util.List;

public class SubMenuAdapter extends RecyclerView.Adapter<SubMenuAdapter.MyViewHolder> {

    private Context context;
    private List<SubMenuModel> albumList;
    private ItemClickListener clickListener;

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, count, price;
        public ImageView thumbnail, heart;


        public MyViewHolder(final View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.card_title);
            count = (TextView) itemView.findViewById(R.id.card_count);
            price = (TextView) itemView.findViewById(R.id.card_price);
            thumbnail = (ImageView) itemView.findViewById(R.id.card_thumbnail);
            heart = (ImageView) itemView.findViewById(R.id.card_overflow);

//            itemView.setOnClickListener(this);
            title.setOnClickListener(this);
            count.setOnClickListener(this);
            thumbnail.setOnClickListener(this);
            heart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "즐겨찾기에 추가/삭제 하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }


    public SubMenuAdapter(Context context, List<SubMenuModel> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @Override
    public SubMenuAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_menu_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubMenuAdapter.MyViewHolder holder, int position) {

        SubMenuModel album = albumList.get(position);
        holder.title.setText(album.getTitle());
        holder.count.setText(album.getDetail());
        holder.price.setText(album.getPrice());

        Glide.with(context).load(album.getImageID()).placeholder(R.drawable.image_one).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
