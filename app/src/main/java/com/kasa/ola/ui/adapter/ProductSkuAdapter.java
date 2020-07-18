package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.model.PriceGroupModel;
import com.kasa.ola.bean.model.ProductSkuLabelModel;
import com.kasa.ola.bean.model.ProductSkuModel;
import com.kasa.ola.widget.AutoLineLayout;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by guan on 2018/9/4 0004.
 */
public class ProductSkuAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<ProductSkuModel> speItem;
    private String[] skuIdList;
    private String[] skuNameList;
    private String speStr = "";
    private ArrayList<PriceGroupModel> priceGroup;
    private String[] initSkuIdList;
    private int selectPosition = 0;
    private ArrayList<PriceGroupModel> selectPriceGroup = new ArrayList<>();

    public ProductSkuAdapter(Context context, ArrayList<ProductSkuModel> speItem) {
        this.context = context;
        this.speItem = speItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductSkuViewHolder(LayoutInflater.from(context).inflate(R.layout.product_sku_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductSkuViewHolder viewHolder = (ProductSkuViewHolder) holder;
        ProductSkuModel productSkuModel = speItem.get(position);
        viewHolder.tvSkuName.setText(productSkuModel.speTitle);
        addSkuLabel(productSkuModel, viewHolder, position);
    }

    private void addSkuLabel(final ProductSkuModel productSkuModel, final ProductSkuViewHolder viewHolder, final int position) {
        viewHolder.skuAutoLayout.removeAllViews();
        int length = productSkuModel.labelList.size();
        for (int i = 0; i < length; i++) {
            final ProductSkuLabelModel productSkuLabelModel = productSkuModel.labelList.get(i);
            final TextView mallSkuLabelText = (TextView) LayoutInflater.from(context).
                    inflate(R.layout.product_sku_label_view, viewHolder.skuAutoLayout, false);
            mallSkuLabelText.setText(productSkuLabelModel.specName);
            if (productSkuLabelModel.isClickable) {
                if (productSkuLabelModel.selected == 1) {
                    mallSkuLabelText.setTextColor(context.getResources().getColor(R.color.COLOR_FF1677FF));
                    mallSkuLabelText.setBackgroundResource(R.drawable.bg_rectangle_blue_stroke);
                    if (speStr.equals("")) {
                        speStr += productSkuLabelModel.specName;
                    } else {
                        speStr += "|" + productSkuLabelModel.specName;
                    }
                } else {
                    mallSkuLabelText.setTextColor(context.getResources().getColor(R.color.COLOR_FF1A1A1A));
                    mallSkuLabelText.setBackgroundResource(R.drawable.bg_rectangle_f2f2f2_1);
                }
                if (onSelectChangeListener != null) {
                    onSelectChangeListener.selectChange(speStr);
                }
                mallSkuLabelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String selectSkuId = "";
                        if (productSkuLabelModel.selected == 0) {
                            productSkuLabelModel.selected = 1;
                            selectPosition = position;
//                            selectSkuId = productSkuLabelModel.speID;
                            skuIdList[productSkuModel.pos] = productSkuLabelModel.speID;
                            skuNameList[productSkuModel.pos] = productSkuLabelModel.specName;
                            if (null != productSkuModel.selectItem) {
                                productSkuModel.selectItem.selected = 0;
                            }
                            productSkuModel.selectItem = productSkuLabelModel;
                        } else if (productSkuLabelModel.selected == 1) {
                            productSkuLabelModel.selected = 0;
                            selectPosition = position;
                            skuIdList[productSkuModel.pos] = "";
                            skuNameList[productSkuModel.pos] = "";
                            if (null != productSkuModel.selectItem) {
                                productSkuModel.selectItem.selected = 0;
                            }
                            productSkuModel.selectItem = null;


                        }
                        speStr = "";

                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(productSkuLabelModel.speImgUrl);
                        }

                        selectPriceGroup=priceGroup;
//                        for (int x=0;x<skuIdList.length;x++){
//                            if (!TextUtils.isEmpty(skuIdList[x])){
//                                selectPriceGroup = getSelectPriceGroup(selectPriceGroup, skuIdList[x]);
//                            }
//                        }
                        selectPriceGroup = getSelectPriceGroup(selectPriceGroup, TextUtils.isEmpty(skuIdList[position])?"":skuIdList[position]);
                        if (productSkuLabelModel.selected == 0){//取消选中
                            for (int n=0;n<speItem.size();n++){
                                if (selectPosition!=n){
                                    ArrayList<ProductSkuLabelModel> labelList = speItem.get(n).labelList;
                                    for (int m = 0; m< labelList.size(); m++){
                                        labelList.get(m).isClickable = true;
                                    }
                                }
                            }
                        }else if(productSkuLabelModel.selected == 1){//选中
                            for (int n=0;n<speItem.size();n++){
                                if (selectPosition!=n){
                                    ArrayList<ProductSkuLabelModel> labelList = speItem.get(n).labelList;
                                    for (int m = 0; m< labelList.size(); m++){
                                        for (int s=0;s<selectPriceGroup.size();s++){
                                            if (selectPriceGroup.get(s).groupContent.contains(labelList.get(m).speID)){
                                                if (selectPriceGroup.get(s).inventory>0){
                                                    labelList.get(m).isClickable = true;
                                                }else {
                                                    labelList.get(m).isClickable = false;
                                                    labelList.get(m).selected = 0;
                                                }
                                            }
                                        }
                                    }


                                }
                            }
                        }


//                        if (!TextUtils.isEmpty(selectSkuId)){
//                            for (int n=0;n<speItem.size();n++){
//                                if (selectPosition!=n){
//                                    selectPriceGroup = getSelectPriceGroup(selectPriceGroup, selectSkuId);
//                                    ArrayList<ProductSkuLabelModel> labelList = speItem.get(n).labelList;
//
//                                    for (int m = 0; m< labelList.size(); m++){
//                                        long inventoryTotal = 0;
//                                        for (int s=0;s<selectPriceGroup.size();s++){
//                                            if (selectPriceGroup.get(s).groupContent.contains(labelList.get(m).speID)){
//                                                if (selectPriceGroup.get(s).inventory>0){
//                                                    inventoryTotal = inventoryTotal + selectPriceGroup.get(s).inventory;
////                                                    labelList.get(m).isClickable = true;
//                                                }else {
////                                                    labelList.get(m).isClickable = false;
//                                                }
//                                            }
//                                        }
//                                        if (inventoryTotal>0){
//                                            labelList.get(m).isClickable = true;
//                                        }else {
//                                            labelList.get(m).isClickable = false;
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
                        notifyDataSetChanged();
                    }
                });
            }else {
                mallSkuLabelText.setTextColor(context.getResources().getColor(R.color.COLOR_FF888888));
                mallSkuLabelText.setBackgroundResource(R.drawable.bg_rectangle_f2f2f2_1);

            }
            viewHolder.skuAutoLayout.addView(mallSkuLabelText);
        }
    }
    private ArrayList<PriceGroupModel> getSelectPriceGroup(ArrayList<PriceGroupModel> priceGroup, String skuId){
        ArrayList<PriceGroupModel> selectPriceGroup = new ArrayList<>();
        if (TextUtils.isEmpty(skuId)){
            return selectPriceGroup;
        }
        for (int a=0;a<priceGroup.size();a++){
            PriceGroupModel priceGroupModel = priceGroup.get(a);
            if (priceGroupModel.groupContent.contains(skuId)){
                selectPriceGroup.add(priceGroupModel);
            }
        }
        return selectPriceGroup;
    }

    public String[] getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(String[] skuIdList) {
        this.skuIdList = skuIdList;
    }

    private boolean getLabelClickable(String speID) {
        for (int i = 0; i < priceGroup.size(); i++) {
            if (priceGroup.get(i).groupContent.contains("_") && speID.contains("_")) {
                if (priceGroup.get(i).groupContent.contains(speID)) {
                    if (priceGroup.get(i).inventory > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else if (!priceGroup.get(i).groupContent.contains("_") && !speID.contains("_")) {
                if (priceGroup.get(i).groupContent.contains(speID)) {
                    if (priceGroup.get(i).inventory > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else if (priceGroup.get(i).groupContent.contains("_") && !speID.contains("_")) {
                if (priceGroup.get(i).groupContent.contains(speID)) {
                    if (priceGroup.get(i).inventory > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isCorrectGroup(String groupContent, String idSb) {
        if (groupContent.contains("_") && idSb.contains("_")){
            String[] ids = idSb.split("_");
            String[] groupContents = groupContent.split("_");
            Arrays.sort(ids);
            Arrays.sort(groupContents);
            return Arrays.equals(ids,groupContents);
        }else if (!groupContent.contains("_") && !idSb.contains("_")){
            if (idSb.equals(groupContent)){
                return true;
            }
        }
        return false;
    }
    public String[] getSkuNameList() {
        return skuNameList;
    }

    public void setSkuNameList(String[] skuNameList) {
        this.skuNameList = skuNameList;
    }

    @Override
    public int getItemCount() {
        return speItem.size();
    }

    public void setPriceGroup(ArrayList<PriceGroupModel> priceGroup) {
        this.priceGroup = priceGroup;
    }

    private class ProductSkuViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_sku;
        private TextView tvSkuName;
        private AutoLineLayout skuAutoLayout;

        public ProductSkuViewHolder(View itemView) {
            super(itemView);
            ll_sku = itemView.findViewById(R.id.ll_sku);
            tvSkuName = itemView.findViewById(R.id.tv_sku_name);
            skuAutoLayout = itemView.findViewById(R.id.sku_auto_layout);
        }
    }

    private OnSelectChangeListener onSelectChangeListener;

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListener {
        void selectChange(String spe);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String imageUrl);
    }

}
