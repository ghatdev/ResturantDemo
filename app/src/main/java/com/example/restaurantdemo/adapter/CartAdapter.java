package com.example.restaurantdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.common.ApplicationShare;
import com.example.restaurantdemo.common.Cart;
import com.example.restaurantdemo.common.Const;
import com.example.restaurantdemo.model.CartModel;
import com.example.restaurantdemo.model.SizeMenuModel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context context;
    private List<CartModel> albumList;
    private ItemClickListener clickListener;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public CartAdapter(Context context, List<CartModel> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartAdapter.MyViewHolder holder, final int position) {

        final CartModel album = albumList.get(position);
        holder.title.setText(album.getItemName());
        holder.detail.setText(album.getItemDetail());
        holder.price.setText(album.getItemPrice());
        holder.quantity.setText(album.getItemQuantity());

        holder.thumbnail.setImageResource(album.getImageId());
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qunt = holder.quantity.getText().toString();
                if (qunt.equals("0")) {
                    return;
                }

                int plusVal = Integer.parseInt(qunt) + 1;
                holder.quantity.setText(String.valueOf(plusVal));

                updateCountStr(album.getItemNo(), Integer.parseInt(holder.quantity.getText().toString()));
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qunt = holder.quantity.getText().toString();
                if (qunt.equals("0")) {
                    holder.quantity.setText("0");
                } else {
                    int minusVal = Integer.parseInt(qunt) - 1;
                    holder.quantity.setText(String.valueOf(minusVal));
                }

                updateCountStr(album.getItemNo(), Integer.parseInt(holder.quantity.getText().toString()));
//                if (holder.quantity.getText().toString().equals("0")) {
//                    albumList.remove(position);
//                    notifyDataSetChanged();
//                }
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumList.remove(position);
                notifyDataSetChanged();

                updateCountStr(album.getItemNo(), -1);
            }
        });

//        holder.btnInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, FullDetailActivity.class);
//                intent.putExtra("MENU", String.valueOf(album.getCategory1().charAt(1)));
//                intent.putExtra("SUB_MENU", album.getCategory2());
//
//                context.startActivity(intent);
//            }
//        });
    }

    private void updateCountStr(String itemNo, int qty) {
        ApplicationShare app = (ApplicationShare)((Activity)context).getApplication();
        if (app.getLoginEmail() == null) {
            Toast.makeText((Activity)context, "로그인을 해 주세요.", Toast.LENGTH_SHORT).show();
            ((Activity)context).finish();
        } else {
            Cart cart = app.getCart(app.getLoginEmail());
            Map<String, SizeMenuModel> cartMap =  cart.getCart();

            SizeMenuModel s = cartMap.get(itemNo);
            if (s != null) {
                s.setQty(qty);
                cart.updateItem(s);
            }

            int count = cartMap.size();
            int sum = 0;

            Iterator<String> keyIt = cartMap.keySet().iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                SizeMenuModel value = cartMap.get(key);

                sum += Integer.parseInt(value.getPrice().substring(1, value.getPrice().length())) * value.getQty();
            }

            TextView tvItemCount = (TextView)((Activity) context).findViewById(R.id.cart_itemCount);
            tvItemCount.setText(String.format(Const.CART_COUNT_STR, String.valueOf(count), String.valueOf(sum)));
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, detail, price, quantity;
        public ImageView thumbnail;
//        public ImageButton btnInfo;
        public ImageButton btnCancel, btnMinus, btnPlus;


        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.cart_itemName);
            detail = (TextView) itemView.findViewById(R.id.cart_itemDetail);
            price = (TextView) itemView.findViewById(R.id.cart_itemPrice);
            quantity = (TextView) itemView.findViewById(R.id.cart_itemQuantity);

            thumbnail = (ImageView) itemView.findViewById(R.id.cart_ivImage);

//            btnInfo = (ImageButton) itemView.findViewById(R.id.cart_btnInfo);
            btnCancel = (ImageButton) itemView.findViewById(R.id.cart_btnCancel);
            btnMinus = (ImageButton) itemView.findViewById(R.id.cart_btnMinus);
            btnPlus = (ImageButton) itemView.findViewById(R.id.cart_btnPlus);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
        }
    }

}
