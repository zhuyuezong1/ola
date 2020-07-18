package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ShopProductBean;
import com.kasa.ola.bean.entity.YearCardBean;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.List;

public class ShopProductAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ShopProductBean> list;
    private OnShopProductGoBuyListener onShopProductGoBuyListener;

    public void setOnShopProductGoBuyListener(OnShopProductGoBuyListener onShopProductGoBuyListener) {
        this.onShopProductGoBuyListener = onShopProductGoBuyListener;
    }

    public ShopProductAdapter(Context context, List<ShopProductBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EducationProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_shop_product,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        EducationProductViewHolder holder = (EducationProductViewHolder) viewHolder;
        final ShopProductBean shopProductBean = list.get(position);
        ImageLoaderUtils.imageLoad(context,shopProductBean.getProductImage(),holder.iv_product);
        holder.tv_product_name.setText(shopProductBean.getProductName());
        holder.tv_product_describe.setText(shopProductBean.getDescribe());
        holder.tv_price.setText("￥"+shopProductBean.getPrice());
        holder.cv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShopProductGoBuyListener!=null){
                    onShopProductGoBuyListener.onItemClick(position);
                }
            }
        });
        holder.tv_go_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShopProductGoBuyListener!=null){
                    onShopProductGoBuyListener.goBuy(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 5;
        return list==null?0:list.size();
    }
    private class EducationProductViewHolder extends RecyclerView.ViewHolder{

        private CardView cv_item;
        private ImageView iv_product;
        private TextView tv_product_name;
        private TextView tv_product_describe;
        private TextView tv_price;
        private TextView tv_go_buy;

        public EducationProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_item = itemView.findViewById(R.id.cv_item);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_describe = itemView.findViewById(R.id.tv_product_describe);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_go_buy = itemView.findViewById(R.id.tv_go_buy);
        }
    }
    public interface OnShopProductGoBuyListener{
        void onItemClick(int position);
        void goBuy(int position);
    }
}
