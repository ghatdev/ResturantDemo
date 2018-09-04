package com.example.restaurantdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.common.ApplicationShare;
import com.example.restaurantdemo.common.Cart;
import com.example.restaurantdemo.model.SizeMenuModel;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.List;
import java.util.Map;

public class ProductSizeAdapter extends RecyclerView.Adapter<ProductSizeAdapter.MyViewHolder> {

    private Context context;
    private List<SizeMenuModel> albumList;
    private ItemClickListener clickListener;

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public ProductSizeAdapter(Context context, List<SizeMenuModel> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @Override
    public ProductSizeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diffrent_size_product_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductSizeAdapter.MyViewHolder holder, int position) {

        final SizeMenuModel album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.price.setText(album.getPrice());
        holder.count.setText(String.valueOf(album.getQty()));
        holder.tvItemNo.setText(album.getItemNo());

        holder.thumbnail.setImageResource(album.getImageId());

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = holder.count.getText().toString().trim();
                int plusVal = Integer.parseInt(temp) + 1;
                holder.count.setText(String.valueOf(plusVal));
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = holder.count.getText().toString().trim();

                if (temp.equals("0")) {
                    holder.count.setText("0");
                } else {
                    int minusVal = Integer.parseInt(temp) - 1;
                    holder.count.setText(String.valueOf(minusVal));
                }

            }
        });

        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(holder.count.getText().toString()) > 0) {
                    ApplicationShare app = (ApplicationShare)((Activity)context).getApplication();
                    if (app.getLoginEmail() == null) {
                        Toast.makeText(context, "로그인을 해 주세요.", Toast.LENGTH_SHORT).show();
                        ((Activity)context).finish();
                    } else {
                        Cart cart = app.getCart(app.getLoginEmail());
                        Map<String, SizeMenuModel>  cartMap =  cart.getCart();
                        cart.addItem(new SizeMenuModel(album.getCategory1(), album.getCategory2(), holder.tvItemNo.getText().toString(), album.getImageId(),
                                                        holder.title.getText().toString(), holder.price.getText().toString(),
                                                        Integer.parseInt(holder.count.getText().toString())));
                        Toast.makeText(context, "[" + holder.title.getText() + "] 장바구니에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, price, count, plus, minus, tvItemNo;
        Button btnAddToCart;
        CircularImageView thumbnail;

        MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.diffrent_size_product_name);
            tvItemNo = (TextView) itemView.findViewById(R.id.tvItemNo);
            price = (TextView) itemView.findViewById(R.id.diffrent_size_product_price);
            count = (TextView) itemView.findViewById(R.id.diffrent_size_product_tvQuantity);
            plus = (TextView) itemView.findViewById(R.id.diffrent_size_product_ivPlus);
            minus = (TextView) itemView.findViewById(R.id.diffrent_size_product_ivMinus);
            thumbnail = (CircularImageView) itemView.findViewById(R.id.diffrent_size_product_roundImageView);
            btnAddToCart = (Button) itemView.findViewById(R.id.diffrent_size_product_btnAddCart);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }
}
