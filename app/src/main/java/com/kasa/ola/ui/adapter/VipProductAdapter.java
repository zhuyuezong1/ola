package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MallProductBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;

import java.util.List;

public class VipProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductBean> vipProducts;
    private int NO_DATA = 0;
//    private int num = 0;
//    private int maxNum = 999;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public VipProductAdapter(Context context, List<ProductBean> vipProducts) {
        this.context = context;
        this.vipProducts = vipProducts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VipProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_vip_product,viewGroup,false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final VipProductViewHolder holder = (VipProductViewHolder) viewHolder;
        final ProductBean productBean = vipProducts.get(position);
//        num = Integer.parseInt(holder.tv_num.getText().toString());
        if (!TextUtils.isEmpty(productBean.getProductName())){
            holder.tv_product_name.setText(productBean.getProductName());
        }
        if (!TextUtils.isEmpty(productBean.getDescribe())){
            holder.tv_product_describe.setText(productBean.getDescribe());
        }
        if (!TextUtils.isEmpty(productBean.getPrice())){
            holder.tv_price.setText("￥"+productBean.getPrice());
        }
        if (!TextUtils.isEmpty(productBean.getImageUrl())){
            ImageLoaderUtils.imageLoad(context,productBean.getImageUrl(),holder.iv_product_image);
        }
        holder.cv_vip_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
//        holder.tv_buy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(productBean.getPrice())){
//                    onVipProductItemListener.add(productBean.getPrice(),holder.tv_num.getText().toString());
//                }
//            }
//        });
//        holder.btn_reduce.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (num > 0) {
//                    num = num-1;
//                    holder.tv_num.setText(String.valueOf(num));
//                }
//            }
//        });
//        holder.btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (num < maxNum) {
//                    num = num+1;
//                    holder.tv_num.setText(String.valueOf(num));
//                } else {
//                    ToastUtils.showShortToast(context, "最多只能选择" + maxNum + "件哟");
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
//        return 0;
        return vipProducts==null?0:vipProducts.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public class VipProductViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_product_name;
        public TextView tv_product_describe;
        public TextView tv_status;
//        public TextView btn_reduce;
//        public TextView tv_num;
//        public TextView btn_add;
        public TextView tv_price;
//        public TextView tv_buy;
        public ImageView iv_product_image;
        public CardView cv_vip_item;

        public VipProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            cv_vip_item = itemView.findViewById(R.id.cv_vip_item);
            tv_product_describe = itemView.findViewById(R.id.tv_product_describe);
            tv_status = itemView.findViewById(R.id.tv_status);
//            btn_reduce = itemView.findViewById(R.id.btn_reduce);
//            tv_num = itemView.findViewById(R.id.tv_num);
//            btn_add = itemView.findViewById(R.id.btn_add);
            tv_price = itemView.findViewById(R.id.tv_price);
//            tv_buy = itemView.findViewById(R.id.tv_buy);
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
        }
    }
    private interface OnVipProductItemListener{
        void onItemClick(int position);
    }
}
