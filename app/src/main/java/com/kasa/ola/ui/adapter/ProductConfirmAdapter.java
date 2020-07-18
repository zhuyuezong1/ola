package com.kasa.ola.ui.adapter;

import android.content.Context;
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
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.bean.entity.ConfirmOrderInfoBean;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductConfirmAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ConfirmOrderInfoBean.CommitInfoListBean.ProductListBean> list;
    public ProductConfirmAdapter(Context context, List<ConfirmOrderInfoBean.CommitInfoListBean.ProductListBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

        View view = LayoutInflater.from(context).inflate(R.layout.confirm_order_item, parent,false);
        holder = new ProductConfirmViewHolder(view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);


        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final ConfirmOrderInfoBean.CommitInfoListBean.ProductListBean product = list.get(position);
        final ProductConfirmViewHolder viewHolder = (ProductConfirmViewHolder) holder;
        viewHolder.tv_product_name.setText(product.getProductName());
        viewHolder.tv_product_num.setText("×"+product.getProductNum());
        viewHolder.tv_product_format.setText((TextUtils.isEmpty(product.getSpe())?"":product.getSpe()));
//                if (product.getPayType().equals("0")){
        viewHolder.tv_special_price.setText("￥"+product.getPrice());
        viewHolder.tv_price.setVisibility(View.GONE);
        viewHolder.tv_back_value.setText(context.getString(R.string.home_back_value,product.getRebates()));
//                }else {
//                    viewHolder.tv_unit_price.setText(product.getFuPrice()+product.getFuPriceUnit()+"+￥"+product.getPrice());
//                }

//        viewHolder.is_have_coupon.setVisibility(isTrue(product.getIsCoupon())?View.GONE:View.VISIBLE);
        ImageLoaderUtils.imageLoad(context, product.getImageUrl(), viewHolder.iv_product);


    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();

    }

    public class ProductConfirmViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_product;
        TextView tv_product_name;
        TextView tv_product_format;
//        TextView is_have_coupon;
        TextView tv_special_price;
        TextView tv_price;
        TextView tv_product_num;
        TextView tv_back_value;

        public ProductConfirmViewHolder(View itemView) {
            super(itemView);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_format = itemView.findViewById(R.id.tv_product_format);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_special_price = itemView.findViewById(R.id.tv_special_price);
            tv_product_num = itemView.findViewById(R.id.tv_product_num);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
//            is_have_coupon = itemView.findViewById(R.id.is_have_coupon);

        }
    }


    public interface TotalPriceListener{
         void addOrSub(ArrayList<CartBean.Product> cartProductModels);
    }
    public boolean isTrue(String str){
        if (!TextUtils.isEmpty(str)){
            if (str.equals("1")){//支持
                return true;
            }else if(str.equals("0")){
                return false;
            }
        }
        return false;
    }
}
