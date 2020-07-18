package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MallProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.ArrayList;

public class MallAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<MallProductBean> list;

    public MallAdapter(Context context, ArrayList<MallProductBean> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        holder = new MallProductViewHolder(LayoutInflater.from(context).inflate(R.layout.mall_product_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MallProductBean mallProductBean = list.get(position);
        MallProductViewHolder viewHolder = (MallProductViewHolder) holder;
        viewHolder.tv_product_name.setText(mallProductBean.getProductName());
        ImageLoaderUtils.imageLoad(context, mallProductBean.getImageUrl(), viewHolder.iv_product);
        viewHolder.tv_unit_price.setText(Html.fromHtml("￥"+"<font color='#FF2850'>"+mallProductBean.getPrice()+"</font>"+"/"+mallProductBean.getUnit()));
        viewHolder.ll_classify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductInfoActivity.class);
                intent.putExtra(Const.MALL_GOOD_ID_KEY, mallProductBean.getProductID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class MallProductViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_product;
        TextView tv_product_name;
        TextView tv_unit_price;
        TextView tv_mall_buy;
        LinearLayout ll_classify;

        public MallProductViewHolder(View itemView) {
            super(itemView);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_unit_price = itemView.findViewById(R.id.tv_unit_price);
            tv_mall_buy = itemView.findViewById(R.id.tv_mall_buy);
            ll_classify = itemView.findViewById(R.id.ll_classify);
        }
    }
}
