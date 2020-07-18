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
import com.kasa.ola.bean.entity.HomeTopBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.ProductDetailsActivity;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class HomeQualityProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductBean> list;
    private int type;
    public HomeQualityProductAdapter(Context context, List<ProductBean> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeQualityProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_quality_grid,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeQualityProductViewHolder viewHolder = (HomeQualityProductViewHolder) holder;
        ProductBean productBean = list.get(position);
        ViewGroup.LayoutParams layoutParams = viewHolder.iv_product.getLayoutParams();
        int width = 0;
        if (type==0){
            width = DisplayUtils.getScreenWidth(context) - DisplayUtils.dip2px(context, 12) * 6-DisplayUtils.dip2px(context, 8)*5;
        }else if (type==1){
            width = DisplayUtils.getScreenWidth(context) - DisplayUtils.dip2px(context, 8) * 8-DisplayUtils.dip2px(context, 4)*6;
        }
        layoutParams.width = width/3;
        layoutParams.height = width/3;
        ImageLoaderUtils.imageLoadRound(context,productBean.getImageUrl(),viewHolder.iv_product,5);
        if (LoginHandler.get().checkLogined()){
            viewHolder.tv_back_value.setVisibility(View.VISIBLE);
        }else {
            viewHolder.tv_back_value.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(productBean.getRebates())){
            viewHolder.tv_back_value.setText(context.getString(R.string.back_value,productBean.getRebates()));
        }
        viewHolder.tv_special_price.setText(context.getString(R.string.commission_value,productBean.getSpecialPrice()));
        viewHolder.tv_price.setText(context.getString(R.string.commission_value,productBean.getPrice()));
        viewHolder.tv_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra(Const.MALL_GOOD_ID_KEY,productBean.getProductID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list==null){
            return 0;
        }else if (list.size()>3){
            return 3;
        }else {
            return list.size();
        }
    }
    private class HomeQualityProductViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout rl_item;
        ImageView iv_product;
        TextView tv_special_price;
        TextView tv_price;
        TextView tv_back_value;
        public HomeQualityProductViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_item = itemView.findViewById(R.id.rl_item);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_special_price = itemView.findViewById(R.id.tv_special_price);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
        }
    }
}
