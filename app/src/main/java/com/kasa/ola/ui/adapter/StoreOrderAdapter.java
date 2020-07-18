package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ShopOrderBean;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.widget.RoundImageView;

import java.util.List;

public class StoreOrderAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ShopOrderBean> list;
    public StoreOrderAdapter(Context context, List<ShopOrderBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoreOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_store_order,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoreOrderViewHolder viewHolder = (StoreOrderViewHolder) holder;
        ShopOrderBean shopOrderBean = list.get(position);
        viewHolder.tv_special_price.setText(shopOrderBean.getMoney());
        viewHolder.tv_desc.setText(shopOrderBean.getProductName());
        viewHolder.tv_time.setText(shopOrderBean.getTime());
        viewHolder.tv_order.setText(context.getString(R.string.order_id,shopOrderBean.getOrderNo()));
        if (shopOrderBean.getOrderType().equals("0")){
            viewHolder.tv_check_status.setText(context.getString(R.string.have_uncheck));
            viewHolder.tv_check_status.setTextColor(context.getResources().getColor(R.color.COLOR_FF1677FF));
        }else if (shopOrderBean.getOrderType().equals("1")){
            viewHolder.tv_check_status.setText(context.getString(R.string.have_checked));
            viewHolder.tv_check_status.setTextColor(context.getResources().getColor(R.color.COLOR_FF333333));
        }
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class StoreOrderViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_special_price;
        private TextView tv_check_status;
        private TextView tv_desc;
        private TextView tv_time;
        private TextView tv_order;
        public StoreOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_special_price = itemView.findViewById(R.id.tv_special_price);
            tv_check_status = itemView.findViewById(R.id.tv_check_status);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_order = itemView.findViewById(R.id.tv_order);
        }
    }
}
