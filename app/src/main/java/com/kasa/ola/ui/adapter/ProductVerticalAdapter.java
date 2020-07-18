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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.ProductDetailsActivity;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class ProductVerticalAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductBean> list;
    private OnLongClickListener onLongClickListener;

    public interface OnLongClickListener{
        void onLongClick(int position);
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public ProductVerticalAdapter(Context context, List<ProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProductVerticalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_vertical_product,viewGroup,false));
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
        holder.tv_stock.setText(context.getString(R.string.sale_number,productBean.getSales()+""));
        ImageLoaderUtils.imageLoadRound(context,productBean.getImageUrl(),holder.iv_product,5);
        if (LoginHandler.get().checkLogined() && !TextUtils.isEmpty(productBean.getRebates())){
            holder.tv_back_value.setVisibility(View.VISIBLE);
            holder.tv_back_value.setText(context.getString(R.string.back_value,productBean.getRebates()));
        }else {
            holder.tv_back_value.setVisibility(View.GONE);
        }
        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(context, ProductInfoActivity.class);
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra(Const.MALL_GOOD_ID_KEY,productBean.getProductID());
                    context.startActivity(intent);
            }
        });
        holder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onLongClickListener!=null){
                    onLongClickListener.onLongClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
//        ProductVerticalViewHolder viewHolder = (ProductVerticalViewHolder) holder;
//        ImageLoaderUtils.clear(context,viewHolder.iv_product);
    }

    @Override
    public int getItemCount() {
//        return 2;
        return list==null?0:list.size();
    }
    private class ProductVerticalViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout rl_item;
        private ImageView iv_product;
        private TextView tv_product_name;
        private TextView tv_product_describe;
        private TextView tv_price;
        private TextView tv_original_price;
        private TextView tv_back_value;
        private TextView tv_stock;
        private TextView tv_distance;
        public ProductVerticalViewHolder(@NonNull View itemView) {
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
        }
    }
}
