package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.PriceGroupBean;
import com.kasa.ola.bean.entity.SkuBean;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.widget.AutoLineLayout;

import java.util.ArrayList;
import java.util.List;

public class SkuTestAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<SkuBean> skuBeans;
    private List<PriceGroupBean> priceGroups;
    private OnLabelClickListener onLabelClickListener;

    public List<String> getSelectSpeIdList() {
        return selectSpeIdList;
    }

    private List<String> selectSpeIdList = new ArrayList<>();

    public void setOnLabelClickListener(OnLabelClickListener onLabelClickListener) {
        this.onLabelClickListener = onLabelClickListener;
    }

    public SkuTestAdapter(Context context, List<SkuBean> skuBeans, List<PriceGroupBean> priceGroups) {
        this.context = context;
        this.skuBeans = skuBeans;
        this.priceGroups = priceGroups;
        for (int i = 0;i<skuBeans.size();i++){
            selectSpeIdList.add("");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SkuTestHolder(LayoutInflater.from(context).inflate(R.layout.product_sku_test_item_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        SkuTestHolder holder = (SkuTestHolder) viewHolder;
        SkuBean skuBean = skuBeans.get(position);
        holder.tvSkuName.setText(skuBean.getSpeTitle());
        addSkuLabel(skuBeans.get(position).getSpeItem(), holder, position);
    }

    private void addSkuLabel(final List<SkuBean.SpeItemBean> labelItems, SkuTestHolder holder, final int position) {
        holder.skuAutoLayout.removeAllViews();
        for (int i=0;i<labelItems.size();i++){
            final SkuBean.SpeItemBean speItemBean = labelItems.get(i);
            final TextView mallSkuLabelText = (TextView) LayoutInflater.from(context).inflate(R.layout.product_sku_label_test_view, holder.skuAutoLayout, false);
            mallSkuLabelText.setText(speItemBean.getSpeName());
            if (speItemBean.isSelected()) {
                mallSkuLabelText.setTextColor(context.getResources().getColor(R.color.COLOR_FFFB6868));
                mallSkuLabelText.setBackgroundResource(R.drawable.bg_rectangle_ffe6e6_stroke);
            } else {
                mallSkuLabelText.setTextColor(context.getResources().getColor(R.color.COLOR_FF1A1A1A));
                mallSkuLabelText.setBackgroundResource(R.drawable.bg_rectangle_f2f2f2_1);
            }
            holder.skuAutoLayout.addView(mallSkuLabelText);
            final int finalI = i;
            mallSkuLabelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (speItemBean.isSelected()){//已选中
                        for (int i=0;i<labelItems.size();i++){
                            labelItems.get(i).setSelected(false);
                        }
                        speItemBean.setSelected(false);
                        selectSpeIdList.set(position,"");
                        if (onLabelClickListener!=null){
                            onLabelClickListener.onCancel(position, finalI);
                        }

                    }else {//未选中
                        for (int i=0;i<labelItems.size();i++){
                            labelItems.get(i).setSelected(false);
                        }
                        speItemBean.setSelected(true);
                        selectSpeIdList.set(position,speItemBean.getSpeID()+"");
                        if (onLabelClickListener!=null){
                            onLabelClickListener.onSelect(position, finalI);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface OnLabelClickListener{
        void onSelect(int position,int labelPosition);
        void onCancel(int position,int labelPosition);
    }

    @Override
    public int getItemCount() {
        return skuBeans==null?0:skuBeans.size();
    }
    private class SkuTestHolder extends RecyclerView.ViewHolder {

        private TextView tvSkuName;
        private AutoLineLayout skuAutoLayout;

        public SkuTestHolder(View itemView) {
            super(itemView);
            tvSkuName = itemView.findViewById(R.id.tv_sku_name);
            skuAutoLayout = itemView.findViewById(R.id.sku_auto_layout);
        }
    }
}
