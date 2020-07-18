package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class RecommendProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ProductBean> list;

    public RecommendProductAdapter(Context context, List<ProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RecommendProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recommend_product,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        RecommendProductViewHolder holder = (RecommendProductViewHolder) viewHolder;
        final ProductBean productBean = list.get(position);
        holder.tv_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_product_name.setText(productBean.getProductName());
        holder.tv_sale_number.setText(context.getString(R.string.sale_number,productBean.getSales()));
        holder.tv_describe.setText(productBean.getDescribe());
        holder.tv_price.setText("￥"+productBean.getSpecialPrice());
        holder.tv_original_price.setText("￥"+productBean.getPrice());
        ViewGroup.LayoutParams layoutParams = holder.iv_recommend_product.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(context) - DisplayUtils.dip2px(context, 10) * 4/*- DisplayUtils.dip2px(context, 14) * 2*/;
        layoutParams.width = width/2;
        layoutParams.height = width / 2;
        ImageLoaderUtils.imageLoadSkipMemoryCache(context,productBean.getImageUrl(),holder.iv_recommend_product);
//        ImageLoaderUtils.imageLoadLargeNumber(context,productBean.getImageUrl(),holder.iv_recommend_product);
        if (LoginHandler.get().checkLogined() && !TextUtils.isEmpty(productBean.getRebates())){
            holder.tv_back_value.setVisibility(View.VISIBLE);
            holder.tv_back_value.setText(context.getString(R.string.estimate_can_back_value,productBean.getRebates()));
        }else {
            holder.tv_back_value.setVisibility(View.GONE);
        }
        holder.item_product.setOnClickListener(new View.OnClickListener() {
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
        RecommendProductViewHolder viewHolder = (RecommendProductViewHolder) holder;
        ImageLoaderUtils.clear(context,viewHolder.iv_recommend_product);
    }

    @Override
    public int getItemCount() {
//        return 2;
        return list==null?0:list.size();
    }
    private class RecommendProductViewHolder extends RecyclerView.ViewHolder{
        private CardView item_product;
        private ImageView iv_recommend_product;
        private TextView tv_product_name;
        private TextView tv_sale_number;
        private TextView tv_describe;
        private TextView tv_price;
        private TextView tv_original_price;
        private TextView tv_buy_now;
        private TextView tv_back_value;
        public RecommendProductViewHolder(@NonNull View itemView) {
            super(itemView);
            item_product = itemView.findViewById(R.id.item_product);
            iv_recommend_product = itemView.findViewById(R.id.iv_recommend_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_sale_number = itemView.findViewById(R.id.tv_sale_number);
            tv_describe = itemView.findViewById(R.id.tv_describe);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_original_price = itemView.findViewById(R.id.tv_original_price);
            tv_buy_now = itemView.findViewById(R.id.tv_buy_now);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
        }
    }
}
