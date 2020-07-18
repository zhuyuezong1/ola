package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;

import java.util.List;

public class UneffectiveProductConfirmAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<CartBean.Product> list;
    public UneffectiveProductConfirmAdapter(Context context, List<CartBean.Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

//        if (viewType==0){
//            holder = new CartViewHolder1(LayoutInflater.from(context).inflate(R.layout.cart_item_title, parent, false));
//        }else {
//            holder = new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false));
//        }
        View view = LayoutInflater.from(context).inflate(R.layout.unffective_product_item, parent,false);
        holder = new ProductConfirmViewHolder(view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);


        return holder;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final CartBean.Product product = list.get(position);
        final ProductConfirmViewHolder viewHolder = (ProductConfirmViewHolder) holder;
        viewHolder.tv_product_name.setText(product.getProductName());
        ImageLoaderUtils.imageLoadRound(context, product.getImageUrl(), viewHolder.iv_product, 2);
                viewHolder.tv_product_format.setText(product.getSpe());
                viewHolder.tv_unit_price.setText("￥"+product.getPrice());
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        viewHolder.iv_product.setColorFilter(filter);
        viewHolder.ll_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showLongToast(context,context.getString(R.string.uneffective_product_item_click_tips));
            }
        });
//                viewHolder.is_have_coupon.setVisibility(isTrue(product.getIsCoupon())? View.VISIBLE: View.GONE);


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
        TextView tv_unit_price;
        LinearLayout ll_product;

        public ProductConfirmViewHolder(View itemView) {
            super(itemView);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_format = itemView.findViewById(R.id.tv_product_format);
            tv_unit_price = itemView.findViewById(R.id.tv_unit_price);
            ll_product = itemView.findViewById(R.id.ll_product);
//            is_have_coupon = itemView.findViewById(R.id.is_have_coupon);

        }
    }


    public interface TotalPriceListener{
         void addOrSub(List<CartBean.Product> cartProductModels);
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
