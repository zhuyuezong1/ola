package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<CartBean.ShoppingCartBean> list;
    private final int EFFECTIVE_PRODUCT = 0;
    private final int UNEFFECTIVE_PRODUCT = 1;
    private int adapterType;

    public void setTotalPriceListener(TotalPriceListener totalPriceListener) {
        this.totalPriceListener = totalPriceListener;
    }

    private TotalPriceListener totalPriceListener;

    public CartAdapter(Context context, ArrayList<CartBean.ShoppingCartBean> list, int adapterType) {
        this.context = context;
        this.list = list;
        this.adapterType = adapterType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == EFFECTIVE_PRODUCT) {
            holder = new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item_new, parent, false));
        } else if (viewType == UNEFFECTIVE_PRODUCT) {
            holder = new UneffectiveProductTitleViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item_new_uneffective, parent, false));
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isEffective()) {
            return EFFECTIVE_PRODUCT;
        } else {
            return UNEFFECTIVE_PRODUCT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final CartBean.ShoppingCartBean shoppingCartBean = list.get(position);

        switch (getItemViewType(position)) {
            case EFFECTIVE_PRODUCT:
                CartViewHolder viewHolder = (CartViewHolder) holder;
                viewHolder.tv_shop_name.setText(shoppingCartBean.getSuppliers());
                CartProductAdapter productAdapter = new CartProductAdapter(context, shoppingCartBean.getProductList());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                viewHolder.product_list.setLayoutManager(linearLayoutManager);
                productAdapter.setHasStableIds(true);
                viewHolder.product_list.setAdapter(productAdapter);
                viewHolder.product_list.setNestedScrollingEnabled(false);
                viewHolder.product_list.setFocusableInTouchMode(false);
                viewHolder.product_list.setVisibility(View.VISIBLE);
                productAdapter.setTotalPriceListener(new CartProductAdapter.TotalPriceListener() {
                    @Override
                    public void addOrSub(List<CartBean.Product> cartProductModels) {
                        shoppingCartBean.setProductList(cartProductModels);
                        if (totalPriceListener!=null){
                            totalPriceListener.addOrSub(list);
                        }
                    }
                });
                viewHolder.iv_check.setBackgroundResource(shoppingCartBean.isSelect() ? R.mipmap.icon_selected : R.mipmap.uncheck_icon);
                viewHolder.iv_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (shoppingCartBean.getProductList()!=null && shoppingCartBean.getProductList().size()>0){
                            if (shoppingCartBean.isSelect()){//取消
                                shoppingCartBean.setSelect(false);
                                for (int i=0;i<shoppingCartBean.getProductList().size();i++){
                                    shoppingCartBean.getProductList().get(i).setSelect(false);
                                }
                            }else {//选中
                                shoppingCartBean.setSelect(true);
                                for (int i=0;i<shoppingCartBean.getProductList().size();i++){
                                    shoppingCartBean.getProductList().get(i).setSelect(true);
                                }
                            }
                        }
                        viewHolder.iv_check.setBackgroundResource(shoppingCartBean.isSelect() ? R.mipmap.icon_selected : R.mipmap.uncheck_icon);
                        productAdapter.notifyDataSetChanged();
                        if (totalPriceListener!=null){
                            totalPriceListener.addOrSub(list);
                        }
                    }
                });
                break;
            case UNEFFECTIVE_PRODUCT:
                UneffectiveProductTitleViewHolder uneffectiveProductTitleViewHolder = (UneffectiveProductTitleViewHolder) holder;
                uneffectiveProductTitleViewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.Builder builder = new CommonDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.delete_uneffective_cart_product))
                                .setLeftButton(context.getString(R.string.cancel_pay))
                                .setRightButton(context.getString(R.string.confirm_pay))
                                .setDialogInterface(new CommonDialog.DialogInterface() {

                                    @Override
                                    public void leftButtonClick(CommonDialog dialog) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void rightButtonClick(CommonDialog dialog) {
                                        dialog.dismiss();

                                        //调接口，同步数据
                                        List<CartBean.Product> productList = shoppingCartBean.getProductList();
                                        String productIds = "";
                                        for (int i = 0; i < productList.size(); i++) {
                                            if (i != productList.size() - 1) {
                                                productIds += productList.get(i).getShopProductID() + ",";
                                            } else {
                                                productIds += productList.get(i).getShopProductID();
                                            }
                                        }
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("userID", LoginHandler.get().getUserId());
                                        jsonObject.put("shopProductID", productIds);
                                        ApiManager.get().getData(/*Const.GET_PRODUCT_DELETE_LIST_TAG*/null, jsonObject, new BusinessBackListener() {
                                            @Override
                                            public void onBusinessSuccess(BaseResponseModel responseModel) {
                                                list.remove(position);
                                                shoppingCartBean.getProductList().clear();
                                                notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onBusinessError(int code, String msg) {
                                                ToastUtils.showLongToast(context, msg);
                                            }
                                        }, null);
                                    }
                                })
                                .create()
                                .show();

                    }
                });
                UneffectiveProductConfirmAdapter uneffectiveProductConfirmAdapter = new UneffectiveProductConfirmAdapter(context, shoppingCartBean.getProductList());
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
                linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                uneffectiveProductTitleViewHolder.uneffective_product_list.setLayoutManager(linearLayoutManager1);
                uneffectiveProductConfirmAdapter.setHasStableIds(true);
                uneffectiveProductTitleViewHolder.uneffective_product_list.setAdapter(uneffectiveProductConfirmAdapter);
                uneffectiveProductTitleViewHolder.uneffective_product_list.setVisibility(View.VISIBLE);
                uneffectiveProductTitleViewHolder.uneffective_product_list.setNestedScrollingEnabled(false);
                uneffectiveProductTitleViewHolder.uneffective_product_list.setFocusableInTouchMode(false);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();

    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        //        ImageView iv_check;
//        ImageView iv_product;
//        TextView tv_product_name;
//        TextView tv_product_format;
//        TextView is_have_coupon;
//        AmountView amount_view;
//        TextView tv_unit_price;
        TextView tv_shop_name;
        RecyclerView product_list;
        ImageView iv_check;

        public CartViewHolder(View itemView) {
            super(itemView);
//            iv_check = itemView.findViewById(R.id.iv_check);
//            iv_product = itemView.findViewById(R.id.iv_product);
//            tv_product_name = itemView.findViewById(R.id.tv_product_name);
//            tv_product_format = itemView.findViewById(R.id.tv_product_format);
//            tv_unit_price = itemView.findViewById(R.id.tv_unit_price);
//            is_have_coupon = itemView.findViewById(R.id.is_have_coupon);
//            amount_view = itemView.findViewById(R.id.amount_view);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            product_list = itemView.findViewById(R.id.product_list);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }

    public class UneffectiveProductTitleViewHolder extends RecyclerView.ViewHolder {
//        TextView tv_clear;
        RecyclerView uneffective_product_list;
        ImageView iv_delete;

        public UneffectiveProductTitleViewHolder(View itemView) {
            super(itemView);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            uneffective_product_list = itemView.findViewById(R.id.uneffective_product_list);
        }
    }

    public interface TotalPriceListener {
        void addOrSub(List<CartBean.ShoppingCartBean> cartProductModels);
    }

}
