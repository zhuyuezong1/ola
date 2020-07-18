package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.bean.entity.ShopProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.ProductDetailsActivity;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class StoreProductVerticalAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ShopProductBean> list;
    private OnStoreProductListListener onStoreProductListListener;

    public interface OnStoreProductListListener{
        void onItemClick(int position);
        void onLongClick(int position);
        void downProduct(int position);
        void upProduct(int position);
    }

    public void setOnStoreProductListListener(OnStoreProductListListener onStoreProductListListener) {
        this.onStoreProductListListener = onStoreProductListListener;
    }

    public StoreProductVerticalAdapter(Context context, List<ShopProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StoreProductVerticalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_vertical_store_product,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        StoreProductVerticalViewHolder holder = (StoreProductVerticalViewHolder) viewHolder;
        final ShopProductBean productBean = list.get(position);
        holder.tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_product_name.setText(productBean.getProductName());
        holder.tv_product_describe.setText(productBean.getDescribe());
        holder.tv_price.setText("￥"+productBean.getSpecialPrice());
        holder.tv_original_price.setText("￥"+productBean.getPrice());
        holder.tv_stock.setText(context.getString(R.string.stock_residue,productBean.getInventory()+""));
        ImageLoaderUtils.imageLoadRound(context,productBean.getProductImage(),holder.iv_product,5);
        holder.tv_back_value.setVisibility(View.GONE);
        String productStatus = productBean.getProductStatus();
        switch (productStatus){
            case "0":
                holder.tv_up_or_down.setVisibility(View.GONE);
                holder.rl_no_pass.setVisibility(View.VISIBLE);
                break;
            case "1":
                holder.tv_up_or_down.setVisibility(View.GONE);
                holder.rl_no_pass.setVisibility(View.GONE);
                break;
            case "2":
                holder.tv_up_or_down.setVisibility(View.VISIBLE);
                holder.rl_no_pass.setVisibility(View.GONE);
                holder.tv_up_or_down.setText(context.getString(R.string.undercarriage_product));
                break;
            case "3":
                holder.tv_up_or_down.setVisibility(View.VISIBLE);
                holder.rl_no_pass.setVisibility(View.GONE);
                holder.tv_up_or_down.setText(context.getString(R.string.put_on_product));
                break;
            default:
                holder.rl_no_pass.setVisibility(View.GONE);
                break;
        }
        holder.tv_up_or_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (productStatus){
                    case "0":
                        break;
                    case "1":
                        break;
                    case "2":
                        if (onStoreProductListListener!=null){
                            onStoreProductListListener.downProduct(position);
                        }
                        break;
                    case "3":
                        if (onStoreProductListListener!=null){
                            onStoreProductListListener.upProduct(position);
                        }
                        break;
                }
            }
        });
        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onStoreProductListListener!=null){
                    onStoreProductListListener.onItemClick(position);
                }
            }
        });
        holder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onStoreProductListListener!=null){
                    onStoreProductListListener.onLongClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
//        StoreProductVerticalViewHolder viewHolder = (StoreProductVerticalViewHolder) holder;
//        ImageLoaderUtils.clear(context,viewHolder.iv_product);
    }

    @Override
    public int getItemCount() {
//        return 2;
        return list==null?0:list.size();
    }
    private class StoreProductVerticalViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout rl_item;
        private ImageView iv_product;
        private TextView tv_product_name;
        private TextView tv_product_describe;
        private TextView tv_price;
        private TextView tv_original_price;
        private TextView tv_back_value;
        private TextView tv_stock;
        private TextView tv_distance;
        private TextView tv_up_or_down;
        private RelativeLayout rl_no_pass;
        public StoreProductVerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_item = itemView.findViewById(R.id.rl_item);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_describe = itemView.findViewById(R.id.tv_product_describe);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_original_price = itemView.findViewById(R.id.tv_original_price);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
            tv_stock = itemView.findViewById(R.id.tv_stock);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_up_or_down = itemView.findViewById(R.id.tv_up_or_down);
            rl_no_pass = itemView.findViewById(R.id.rl_no_pass);
        }
    }
}
