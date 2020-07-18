package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kasa.ola.R;
import com.kasa.ola.bean.model.PriceGroupModel;
import com.kasa.ola.bean.model.ProductSkuModel;
import com.kasa.ola.ui.adapter.ProductSkuAdapter;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MallSkuDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private LoadMoreRecyclerView skuRecycleView;
    private ProductSkuAdapter productSkuAdapter;
    private ArrayList<ProductSkuModel> speItem;
    private ArrayList<PriceGroupModel> priceGroup;
    private OnConfirmListener onConfirmListener;
    private String[] skuIdList;
    private String[] skuNameList;
    private int num = 1;
    private int maxNum = 1;
    private TextView tvNum;
    private String price;
    private TextView tvProductFormat;
    private ImageView ivProduct;
    private String imageUrl;
    private TextView tvProductPrice;
    private StringBuilder idSb;
    private StringBuilder nameSb;
    private StringBuilder fullNameSb;
    private TextView btnAdd;
    private TextView btnReduce;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {

        void confirm(String groupContent, String skuGroup, String skuFullGroup, String price, int num);
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
            case R.id.bg_container:
                dismiss();
                break;
            case R.id.view_container:
                break;
            case R.id.btn_confirm:
                skuIdList = productSkuAdapter.getSkuIdList();
                skuNameList = productSkuAdapter.getSkuNameList();
                StringBuilder idSb = new StringBuilder();
                StringBuilder nameSb = new StringBuilder();
                StringBuilder fullNameSb = new StringBuilder();
                StringBuilder unSelected = new StringBuilder();
                for (int i = 0; i < skuIdList.length; i++) {
                    if (TextUtils.isEmpty(skuIdList[i])) {
                        unSelected.append(" ").append(speItem.get(i).speTitle);
                    } else {
                        if (i == (skuIdList.length - 1)) {
                            idSb.append(skuIdList[i]);
                            nameSb.append(skuNameList[i]);
                            fullNameSb.append(speItem.get(i).speTitle).append(":").append(skuNameList[i]);
                        } else {
                            idSb.append(skuIdList[i]).append("_");
                            nameSb.append(skuNameList[i]).append(",");
                            fullNameSb.append(speItem.get(i).speTitle).append(":").append(skuNameList[i]).append(",");
                        }
                    }
                    if (i == (skuIdList.length - 1) && !TextUtils.isEmpty(unSelected)) {
                        ToastUtils.showShortToast(context, context.getString(R.string.choose_sku_tips, unSelected));
                        return;
                    }
                }
                if (null != onConfirmListener) {
                    if (priceGroup.size() == 0) {
                        onConfirmListener.confirm("", "", "", "", num);
                        dismiss();
                    } else {
                        for (int i = 0; i < priceGroup.size(); i++) {
                            if (isCorrectGroup(priceGroup.get(i).groupContent, idSb.toString())) {
                                onConfirmListener.confirm(priceGroup.get(i).groupContent,nameSb.toString(),fullNameSb.toString(), priceGroup.get(i).totalPrice, num);
                                dismiss();
                            }else {
                                dismiss();
                            }
                        }
                    }
                }
                break;
            case R.id.btn_reduce:
                if (num > 1) {
                    num = num-1;
                    tvNum.setText(String.valueOf(num));
                } else {
                    ToastUtils.showShortToast(context, "至少要选择一件哟");
                }
                changeAddAndReduceColor(btnReduce,num);
                break;
            case R.id.btn_add:
                if (num < maxNum) {
                    num = num+1;
                    tvNum.setText(String.valueOf(num));
                } else {
                    ToastUtils.showShortToast(context, "最多只能选择" + maxNum + "件哟");
                }
                changeAddAndReduceColor(btnReduce,num);
                break;
        }
    }
    private void changeAddAndReduceColor(TextView tv, int num){
        if (num==1){
            tv.setEnabled(false);
            tv.setTextColor(context.getResources().getColor(R.color.COLOR_FFE9E9E9));
        }else {
            tv.setEnabled(true);
            tv.setTextColor(context.getResources().getColor(R.color.COLOR_FF666666));
        }
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

    public MallSkuDialog(@NonNull Context context, ArrayList<ProductSkuModel> speItem, ArrayList<PriceGroupModel> priceGroup,
                         String[] skuIdList, String[] skuNameList, int num, String price, String imageUrl) {
        super(context, R.style.fade_dialog_style);
        this.context = context;
        this.speItem = speItem;
        this.priceGroup = priceGroup;
        this.skuIdList = skuIdList;
        this.skuNameList = skuNameList;
        this.num = num;
        this.price = price;
        this.imageUrl = imageUrl;
        initView();
    }

    private void initView() {
        setContentView(R.layout.mall_product_sku_choose_layout);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        skuRecycleView = findViewById(R.id.sku_recycle_view);
        skuRecycleView.setLayoutManager(new LinearLayoutManager(context));
        skuRecycleView.setNoFooter(true);
        productSkuAdapter = new ProductSkuAdapter(context, speItem);
        productSkuAdapter.setSkuIdList(skuIdList);
        productSkuAdapter.setPriceGroup(priceGroup);
        productSkuAdapter.setSkuNameList(skuNameList);
        skuRecycleView.setAdapter(productSkuAdapter);

        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.bg_container).setOnClickListener(this);
        findViewById(R.id.view_container).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

//        View headView = LayoutInflater.from(context).inflate(R.layout.view_mall_sku_head, skuRecycleView, false);

        ivProduct = findViewById(R.id.iv_product);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductFormat = findViewById(R.id.tv_product_format);
        if (!TextUtils.isEmpty(imageUrl)){
            ImageLoaderUtils.imageLoadRound(context,imageUrl,ivProduct,5);
        }
        productSkuAdapter.setOnSelectChangeListener(new ProductSkuAdapter.OnSelectChangeListener() {
            @Override
            public void selectChange(String spe) {
                tvProductFormat.setText(spe);
            }
        });
        tvProductPrice.setText("￥"+price);
        productSkuAdapter.setOnItemClickListener(new ProductSkuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String imageUrl) {
                if (!TextUtils.isEmpty(switchSku())){
                    tvProductPrice.setText("￥"+switchSku());
                }
                if (TextUtils.isEmpty(imageUrl)){

                }else {
                    ImageLoaderUtils.imageLoad(context, imageUrl, ivProduct);
                }
            }
        });

        btnReduce = findViewById(R.id.btn_reduce);
        tvNum = findViewById(R.id.tv_num);
        btnAdd = findViewById(R.id.btn_add);
        btnReduce.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        changeAddAndReduceColor(btnReduce,num);
        tvNum.setText(num + "");
        tvNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviseNumDialog.Builder builder = new ReviseNumDialog.Builder(context);
                builder
                        .setLeftButton(context.getString(R.string.cancel))
                        .setRightButton(context.getString(R.string.ok))
                        .setDialogInterface(new ReviseNumDialog.DialogInterface() {

                            @Override
                            public void leftButtonClick(ReviseNumDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void rightButtonClick(ReviseNumDialog dialog, final int number) {
                                dialog.dismiss();
                                if (number <= 0) {
                                    ToastUtils.showLongToast(context, context.getString(R.string.no_product_prompt));
                                }else {
                                    num = number;
                                    tvNum.setText(num + "");
                                }
                            }
                        })
                        .create(num)
                        .show();
            }
        });
//        skuRecycleView.addHeaderView(headView);
    }

    private String switchSku() {
        skuIdList = productSkuAdapter.getSkuIdList();
        skuNameList = productSkuAdapter.getSkuNameList();
        idSb = new StringBuilder();
        nameSb = new StringBuilder();
        fullNameSb = new StringBuilder();
        StringBuilder unSelected = new StringBuilder();
        for (int i = 0; i < skuIdList.length; i++) {
            if (TextUtils.isEmpty(skuIdList[i])) {
                unSelected.append(" ").append(speItem.get(i).speTitle);
            } else {
                if (i == (skuIdList.length - 1)) {
                    idSb.append(skuIdList[i]);
                    nameSb.append(skuNameList[i]);
                    fullNameSb.append(speItem.get(i).speTitle).append(":").append(skuNameList[i]);
                } else {
                    idSb.append(skuIdList[i]).append("_");
                    nameSb.append(skuNameList[i]).append(",");
                    fullNameSb.append(speItem.get(i).speTitle).append(":").append(skuNameList[i]).append(",");
                }
            }
            if (i == (skuIdList.length - 1) && !TextUtils.isEmpty(unSelected)) {
//                ToastUtils.showShortToast(context, context.getString(R.string.choose_sku_tips, unSelected));
                return price;
            }
        }
        if (priceGroup.size() == 0) {
        } else {
            for (int i = 0; i < priceGroup.size(); i++) {
                if (isCorrectGroup(priceGroup.get(i).groupContent, idSb.toString())) {
                    return priceGroup.get(i).totalPrice;
                }
            }
        }
        return "";
    }

}
