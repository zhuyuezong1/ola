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
import com.kasa.ola.bean.entity.OrganBean;
import com.kasa.ola.bean.entity.ShopInfoBean;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ShopInfoBean> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ShopAdapter(Context context, List<ShopInfoBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EducationOrganViewHolder(LayoutInflater.from(context).inflate(R.layout.item_shop, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        EducationOrganViewHolder holder = (EducationOrganViewHolder) viewHolder;
        ShopInfoBean shopInfoBean = list.get(position);
        holder.tv_shop_name.setText(shopInfoBean.getSuppliersName());
//        ImageLoaderUtils.imageLoad(context,shopInfoBean.getSuppliersLogo(),holder.iv_shop_logo,R.mipmap.shop_icon_default,false);
        ImageLoaderUtils.imageLoadRound(context,shopInfoBean.getSuppliersLogo(),holder.iv_shop_logo,R.mipmap.shop_icon_default,5);

        holder.tv_shop_describe.setText(shopInfoBean.getSuppliersDesc());
        if (TextUtils.isEmpty(shopInfoBean.getAddress())){
            holder.tv_shop_address.setVisibility(View.GONE);
        }else {
            holder.tv_shop_address.setVisibility(View.VISIBLE);
            holder.tv_shop_address.setText("地址："+shopInfoBean.getAddress());
        }
        holder.cv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
//                    ToastUtils.showLongToast(context,"假数据,无法跳转");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }
    private class EducationOrganViewHolder extends RecyclerView.ViewHolder{
        public CardView cv_item;
        public ImageView iv_shop_logo;
        public TextView tv_shop_name;
        public TextView tv_shop_address;
        public TextView tv_shop_describe;

        public EducationOrganViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_item = itemView.findViewById(R.id.cv_item);
            iv_shop_logo = itemView.findViewById(R.id.iv_shop_logo);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            tv_shop_describe = itemView.findViewById(R.id.tv_shop_describe);
            tv_shop_address = itemView.findViewById(R.id.tv_shop_address);
        }
    }
}
