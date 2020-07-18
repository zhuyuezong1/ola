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

public class HomePromoteSalesProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<HomeBean.MallProductBean> list;
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public HomePromoteSalesProductAdapter(Context context, List<HomeBean.MallProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HomeVipProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_promote_sales,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        HomeBean.MallProductBean mallProductBean = list.get(i);
        HomeVipProductViewHolder holder = (HomeVipProductViewHolder) viewHolder;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels- DisplayUtils.dip2px(context,14)*2- DisplayUtils.dip2px(context,5)*2;
        ViewGroup.LayoutParams layoutParams = holder.item_vip_view.getLayoutParams();
        layoutParams.width=widthPixels/3;
        ViewGroup.LayoutParams layoutParams1 = holder.iv_vip_product.getLayoutParams();
        layoutParams1.width = widthPixels/3;
        layoutParams1.height =widthPixels/3;
        ImageLoaderUtils.imageLoad(context,list.get(i).getImageUrl(),holder.iv_vip_product);
        holder.item_vip_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(i);
                }
            }
        });
        holder.tv_product_title.setText(mallProductBean.getProductName());
        holder.tv_price.setText("￥"+mallProductBean.getPrice());
    }

    @Override
    public int getItemCount() {
//        return 20;
        return list==null?0:list.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class HomeVipProductViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout item_vip_view;
        public ImageView iv_vip_product;
        public TextView tv_product_title;
        public TextView tv_price;

        public HomeVipProductViewHolder(@NonNull View itemView) {
            super(itemView);
            item_vip_view = itemView.findViewById(R.id.item_vip_view);
            iv_vip_product = itemView.findViewById(R.id.iv_vip_product);
            tv_product_title = itemView.findViewById(R.id.tv_product_title);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }


}
