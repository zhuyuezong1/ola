package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class StoreProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductBean> list;

    public StoreProductAdapter(Context context, List<ProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProductVerticalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_store_product,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ProductVerticalViewHolder holder = (ProductVerticalViewHolder) viewHolder;
        final ProductBean productBean = list.get(position);
        holder.tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_product_name.setText(productBean.getProductName());
        holder.tv_product_describe.setText(productBean.getDescribe());
        holder.tv_price.setText("￥"+productBean.getSpecialPrice());
        holder.tv_original_price.setText("￥"+productBean.getPrice());
        ImageLoaderUtils.imageLoadRound(context,productBean.getImageUrl(),holder.iv_product,5);
        if (LoginHandler.get().checkLogined() && !TextUtils.isEmpty(productBean.getRebates())){
            holder.tv_back_value.setVisibility(View.VISIBLE);
            holder.tv_back_value.setText(context.getString(R.string.back_value,productBean.getRebates()));
        }else {
            holder.tv_back_value.setVisibility(View.GONE);
        }
        holder.ll_store_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, ProductInfoActivity.class);
                    intent.putExtra(Const.MALL_GOOD_ID_KEY,productBean.getProductID());
                    context.startActivity(intent);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ProductVerticalViewHolder viewHolder = (ProductVerticalViewHolder) holder;
        ImageLoaderUtils.clear(context,viewHolder.iv_product);
    }

    @Override
    public int getItemCount() {
//        return 2;
        return list==null?0:list.size();
    }
    private class ProductVerticalViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout ll_store_product;
        private ImageView iv_product;
        private TextView tv_product_name;
        private TextView tv_product_describe;
        private TextView tv_price;
        private TextView tv_original_price;
        private TextView tv_back_value;
        private TextView tv_stock;
        private TextView tv_go_buy;
        public ProductVerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_store_product = itemView.findViewById(R.id.ll_store_product);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_describe = itemView.findViewById(R.id.tv_product_describe);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_original_price = itemView.findViewById(R.id.tv_original_price);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
            tv_stock = itemView.findViewById(R.id.tv_stock);
            tv_go_buy = itemView.findViewById(R.id.tv_go_buy);
        }
    }
}
