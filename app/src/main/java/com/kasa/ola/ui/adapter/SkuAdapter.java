package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.ProductInfoBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.widget.AutoLineLayout;
import com.kasa.ola.widget.flowlayout.FlowLayout;
import com.kasa.ola.widget.flowlayout.TagAdapter;
import com.kasa.ola.widget.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class SkuAdapter extends RecyclerView.Adapter {
    private Context context;
    private ProductInfoBean productInfoBean;
    private TagItemOnClick tagItemOnClick;

    public interface TagItemOnClick {
        void onItemClick(View view, int tagPosition, int positions);
    }

    public void setTagItemOnClickListener(TagItemOnClick listener) {
        tagItemOnClick = listener;
    }
    public SkuAdapter(Context context, ProductInfoBean productInfoBean) {
        this.context = context;
        this.productInfoBean = productInfoBean;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SkuViewHolder(LayoutInflater.from(context).inflate(R.layout.item_choose_shop,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        final SkuViewHolder holder = (SkuViewHolder) viewHolder;
        final ProductInfoBean.SpecificationsBean specificationsBean = productInfoBean.getSpecifications().get(position);
        holder.tv_type.setText(specificationsBean.getSpeTitle());
        final List<String> listString = new ArrayList<>();
        for (int i = 0; i < specificationsBean.getSpeItem().size(); i++) {
            listString.add(specificationsBean.getSpeItem().get(i).getSpeName());
        }
        final LayoutInflater mInflater = LayoutInflater.from(context);
        holder.flow_layout_hot.setAdapter(new TagAdapter<String>(listString) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.item_shop_group_text,
                        holder.flow_layout_hot, false);
                tv.setText(s);
                if (specificationsBean.getSpeItem().get(position).isCanSelect()) {
                    tv.setEnabled(true);
                    tv.setTextColor(context.getResources().getColor(R.color.black));
                    if (specificationsBean.getSpeItem().get(position).isSelect()){
                        tv.setBackgroundResource(R.drawable.bg_rectangle_ffe6e6_stroke);
                        tv.setTextColor(context.getResources().getColor(R.color.COLOR_FFE44242));
                    }
                } else {
                    tv.setEnabled(false);
                    tv.setBackgroundResource(R.drawable.bg_rectangle_f2f2f2_1);
                    tv.setTextColor(context.getResources().getColor(R.color.COLOR_FF9A9A9A));
                }
//                if (specificationsBean.getSpeItem().get(position).isSelect() && specificationsBean.getSpeItem().get(position).isCanSelect()) {
//                    tv.setBackgroundResource(R.drawable.bg_rectangle_ffe6e6_stroke);
//                    tv.setTextColor(context.getResources().getColor(R.color.COLOR_FFE44242));
//
//                } else {
//                    tv.setBackgroundResource(R.drawable.bg_rectangle_f2f2f2_1);
//                }
                return tv;
            }
        });
        holder.flow_layout_hot.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public void onTagClick(View view, int tagPosition, FlowLayout parent) {
                if (tagItemOnClick != null) {
                    tagItemOnClick.onItemClick(view, tagPosition, position);
                }
            }
        });
        holder.flow_layout_hot.setMaxSelectCount(1);


//        holder.tvSkuName.setText(specificationsBean.getSpeTitle());
//        addSkuLabel(specificationsBean, holder, position);
    }

//    private void addSkuLabel(ProductInfoBean.SpecificationsBean specificationsBean, SkuViewHolder holder, int position) {
//        holder.skuAutoLayout.removeAllViews();
//        for (int i = 0;i<specificationsBean.getSpeItem().size();i++){
//            ProductInfoBean.SpecificationsBean.SpeItemBean speItemBean = specificationsBean.getSpeItem().get(i);
//            TextView mallSkuLabelText = (TextView) LayoutInflater.from(context).inflate(R.layout.product_sku_label_view, holder.skuAutoLayout, false);
//            mallSkuLabelText.setText(speItemBean.getSpeName());
//        }
//    }

    @Override
    public int getItemCount() {
        return productInfoBean.getSpecifications()==null?0:productInfoBean.getSpecifications().size();
    }

    private class SkuViewHolder extends RecyclerView.ViewHolder{
//        private LinearLayout ll_sku;
//        private TextView tvSkuName;
//        private AutoLineLayout skuAutoLayout;
        private TextView tv_type;
        private TagFlowLayout flow_layout_hot;

        public SkuViewHolder(@NonNull View itemView) {
            super(itemView);
//            ll_sku = itemView.findViewById(R.id.ll_sku);
//            tvSkuName = itemView.findViewById(R.id.tv_sku_name);
//            skuAutoLayout = itemView.findViewById(R.id.sku_auto_layout);
            tv_type = itemView.findViewById(R.id.tv_type);
            flow_layout_hot = itemView.findViewById(R.id.flow_layout_hot);
        }
    }


}
