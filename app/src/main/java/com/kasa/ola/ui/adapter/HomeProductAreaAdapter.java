package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class HomeProductAreaAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductBean> list;
    public HomeProductAreaAdapter(Context context, List<ProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeProductAreaViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_grid,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeProductAreaViewHolder viewHolder = (HomeProductAreaViewHolder) holder;
        ProductBean productBean = list.get(position);
        ViewGroup.LayoutParams layoutParams = viewHolder.iv_product.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(context) - DisplayUtils.dip2px(context, 12) * 8-DisplayUtils.dip2px(context, 0.6f);
        layoutParams.width = width/4;
        layoutParams.height = width/4;
        ImageLoaderUtils.imageLoadRound(context,productBean.getImageUrl(),viewHolder.iv_product,5);
        viewHolder.tv_special_price.setText(context.getString(R.string.commission_value,productBean.getSpecialPrice()));
        if (LoginHandler.get().checkLogined()){
            viewHolder.tv_back_value.setVisibility(View.VISIBLE);
        }else {
            viewHolder.tv_back_value.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(productBean.getRebates())){
            viewHolder.tv_back_value.setText(context.getString(R.string.home_back_value,productBean.getRebates()));
        }
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }else if (list.size()>2){
            return 2;
        }else {
            return list.size();
        }
    }
    private class HomeProductAreaViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_product;
        TextView tv_special_price;
        TextView tv_back_value;
        public HomeProductAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_special_price = itemView.findViewById(R.id.tv_special_price);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
        }
    }
}
