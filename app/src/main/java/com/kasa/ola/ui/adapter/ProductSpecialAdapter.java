package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.ui.ProductSpecialActivity;
import com.kasa.ola.utils.ToastUtils;

import java.util.List;

public class ProductSpecialAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductBean> list;
    public ProductSpecialAdapter(Context context, List<ProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProductSpecialViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_special,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ProductSpecialViewHolder holder = (ProductSpecialViewHolder) viewHolder;
        holder.tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG );
        holder.cv_vip_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLongToast(context,"待传递productID");
                Intent intent = new Intent(context, ProductInfoActivity.class);
                intent.putExtra(Const.MALL_GOOD_ID_KEY,"");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }
    private class ProductSpecialViewHolder extends RecyclerView.ViewHolder{
        public CardView cv_vip_item;
        public ImageView iv_product_image;
        public TextView tv_product_name;
        public TextView tv_sale_number;
        public TextView tv_product_describe;
        public TextView tv_price;
        public TextView tv_original_price;
        public TextView tv_buy_now;
        public TextView tv_back_value;
        public ProductSpecialViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_sale_number = itemView.findViewById(R.id.tv_sale_number);
            tv_product_describe = itemView.findViewById(R.id.tv_product_describe);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_original_price = itemView.findViewById(R.id.tv_original_price);
            tv_buy_now = itemView.findViewById(R.id.tv_buy_now);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
            cv_vip_item = itemView.findViewById(R.id.cv_vip_item);
        }
    }
}
