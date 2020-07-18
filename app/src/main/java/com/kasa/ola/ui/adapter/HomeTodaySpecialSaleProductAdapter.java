package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.HomeBean;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class HomeTodaySpecialSaleProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<HomeBean.MallProductBean> list;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public HomeTodaySpecialSaleProductAdapter(Context context, List<HomeBean.MallProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HomeTodaySpecialSaleProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_today_special_sale,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        HomeTodaySpecialSaleProductViewHolder holder = (HomeTodaySpecialSaleProductViewHolder) viewHolder;
        HomeBean.MallProductBean mallProductBean = list.get(i);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = 0;
        widthPixels = metrics.widthPixels- DisplayUtils.dip2px(context,14)*2- DisplayUtils.dip2px(context,5)*2;
        ViewGroup.LayoutParams layoutParams = holder.iv_today_special_sale_product.getLayoutParams();
        layoutParams.width=widthPixels/2;
        layoutParams.height = widthPixels/2;
        ImageLoaderUtils.imageLoad(context,mallProductBean.getImageUrl(),holder.iv_today_special_sale_product);
        holder.tv_product_name.setText(mallProductBean.getProductName());
        holder.tv_price.setText("￥"+mallProductBean.getPrice());
        holder.item_today_special_sale_product_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(i);
                }
            }
        });
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
//        return 20;
        return list==null?0:list.size();
    }

    public class HomeTodaySpecialSaleProductViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout item_today_special_sale_product_view;
        public ImageView iv_today_special_sale_product;
        public TextView tv_product_name;
        public TextView tv_price;

        public HomeTodaySpecialSaleProductViewHolder(@NonNull View itemView) {
            super(itemView);
            item_today_special_sale_product_view = itemView.findViewById(R.id.item_today_special_sale_product_view);
            iv_today_special_sale_product = itemView.findViewById(R.id.iv_today_special_sale_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }

}
