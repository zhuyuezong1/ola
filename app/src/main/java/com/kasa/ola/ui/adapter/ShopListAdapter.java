package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.HomeTopBean;
import com.kasa.ola.ui.QualityShopActivity;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<HomeTopBean.HomeQualityBean> list;
    private OnCheckMoreListener onCheckMoreListener;
    private int type;//0首页，1：列表页

    public void setOnCheckMoreListener(OnCheckMoreListener onCheckMoreListener) {
        this.onCheckMoreListener = onCheckMoreListener;
    }

    public ShopListAdapter(Context context, List<HomeTopBean.HomeQualityBean> list,int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_quality_shop,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShopListViewHolder viewHolder = (ShopListViewHolder) holder;
        HomeTopBean.HomeQualityBean homeQualityBean = list.get(position);
        GridLayoutManager qualityProductLayoutManager = new GridLayoutManager(context,3);
        viewHolder.rv_quality_product.setLayoutManager(qualityProductLayoutManager);
        viewHolder.tv_shop_name.setText(homeQualityBean.getSuppliersName());
        viewHolder.tv_shop_address.setText(homeQualityBean.getAddress());
        if (homeQualityBean.getProductList()!=null && homeQualityBean.getProductList().size()>0){
            HomeQualityProductAdapter homeQualityProductAdapter = new HomeQualityProductAdapter(context, homeQualityBean.getProductList(),type);
            viewHolder.rv_quality_product.setAdapter(homeQualityProductAdapter);
        }else {
            viewHolder.rv_quality_product.setAdapter(null);
        }
        CommonUtils.setScroll(viewHolder.rv_quality_product);
        viewHolder.ll_shop_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCheckMoreListener!=null){
                    onCheckMoreListener.checkMore(position);
                }
            }
        });

        GradientDrawable drawable = (GradientDrawable) viewHolder.rrl_quality_product.getBackground();
        drawable.setCornerRadius(DisplayUtils.dip2px(context,8));
        drawable.setColor(Color.parseColor(homeQualityBean.getBgColor()));
    }

    @Override
    public int getItemCount() {
//        return 10;
        return list==null?0:list.size();
    }

    private class ShopListViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rrl_quality_product;
        private LinearLayout ll_shop_head;
        private TextView tv_shop_name;
        private TextView tv_shop_address;
        private ImageView iv_check_more;
        private RecyclerView rv_quality_product;
        public ShopListViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_shop_head = itemView.findViewById(R.id.ll_shop_head);
            rrl_quality_product = itemView.findViewById(R.id.rrl_quality_product);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            tv_shop_address = itemView.findViewById(R.id.tv_shop_address);
            iv_check_more = itemView.findViewById(R.id.iv_check_more);
            rv_quality_product = itemView.findViewById(R.id.rv_quality_product);
        }
    }

    public interface OnCheckMoreListener{
        void checkMore(int position);
    }
}
