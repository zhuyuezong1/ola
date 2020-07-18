package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.ReviseNumDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.ProductDetailsActivity;
import com.kasa.ola.ui.fragment.CartFragment;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.AmountView;

import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<CartBean.Product> list;
    private int amount = 0;

    public void setTotalPriceListener(TotalPriceListener totalPriceListener) {
        this.totalPriceListener = totalPriceListener;
    }

    private TotalPriceListener totalPriceListener;

    public CartProductAdapter(Context context, List<CartBean.Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;

//        if (viewType==0){
//            holder = new CartViewHolder1(LayoutInflater.from(context).inflate(R.layout.cart_item_title, parent, false));
//        }else {
//            holder = new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false));
//        }
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        holder = new ProductViewHolder(view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);


        return holder;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final CartBean.Product product = list.get(position);
        final ProductViewHolder viewHolder = (ProductViewHolder) holder;
        viewHolder.tv_product_name.setText(product.getProductName());
        ImageLoaderUtils.imageLoad(context, product.getImageUrl(), viewHolder.iv_product);
        viewHolder.tv_product_format.setText(product.getSpe());
//        if (product.getPayType().equals("0")) {
            viewHolder.tv_unit_price.setText("￥" + product.getPrice());
            viewHolder.tv_back_value.setText(context.getString(R.string.home_back_value,product.getRebates()));
//        } else {
//            viewHolder.tv_unit_price.setText(product.getFuPrice() + product.getFuPriceUnit() + "+￥" + product.getPrice());
//        }
//                product.setProductNum(viewHolder.amount_view.getAmount()+"");

        viewHolder.iv_check.setBackgroundResource(product.isSelect() ? R.mipmap.icon_selected : R.mipmap.uncheck_icon);
        viewHolder.iv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.isSelect()) {
                    product.setSelect(false);
                    notifyDataSetChanged();
                    if (totalPriceListener != null) {
                        totalPriceListener.addOrSub(list);
                    }
                } else {
//                    if (TextUtils.isEmpty(CartFragment.selectPayType)) {
//                        product.setSelect(true);
//                        notifyDataSetChanged();
//                        if (totalPriceListener != null) {
//                            totalPriceListener.addOrSub(list);
//                        }
//                    } else {
                            product.setSelect(true);
                            notifyDataSetChanged();
                            if (totalPriceListener != null) {
                                totalPriceListener.addOrSub(list);
                            }
//                        }
//                    }
                }
            }
        });

//        viewHolder.amount_view.setGoods_storage(999);

        viewHolder.tv_num.setText(product.getProductNum());

        changeAddAndReduceColor(viewHolder.btn_reduce,Integer.parseInt(viewHolder.tv_num.getText().toString()));
        viewHolder.btn_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(viewHolder.tv_num.getText().toString());
                if (amount > 1) {
                    amount--;
                    changeNum(amount, viewHolder.tv_num, product,totalPriceListener,viewHolder.btn_reduce);
                }else {
                    ToastUtils.showLongToast(context, context.getString(R.string.no_product_prompt));
                }
            }
        });
        viewHolder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(viewHolder.tv_num.getText().toString());
                if (amount < Const.MAX_NUM) {
                    amount++;
                    changeNum(amount, viewHolder.tv_num, product,totalPriceListener,viewHolder.btn_reduce);
                }else {
                    ToastUtils.showShortToast(context, "最多只能选择" + Const.MAX_NUM + "件哟");
                }
            }
        });
        viewHolder.tv_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(viewHolder.tv_num.getText().toString());
//                int i = Integer.parseInt(viewHolder.tv_num.getText().toString());
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
                            public void rightButtonClick(ReviseNumDialog dialog, final int num) {
                                dialog.dismiss();
                                if (num == 0) {
                                    ToastUtils.showLongToast(context, context.getString(R.string.no_product_prompt));
                                }else if (num>Const.MAX_NUM){
                                    ToastUtils.showShortToast(context, "最多只能选择" + Const.MAX_NUM + "件哟");
                                }
                                if (num != Integer.parseInt(viewHolder.tv_num.getText().toString()) && num != 0 && num<Const.MAX_NUM) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("userID", LoginHandler.get().getUserId());
                                    jsonObject.put("shopProductID", product.getShopProductID());
                                    jsonObject.put("productNum", num + "");
                                    ApiManager.get().getData(Const.UPDATA_SHOP_CART_PRODUCT_TAG, jsonObject, new BusinessBackListener() {
                                        @Override
                                        public void onBusinessSuccess(BaseResponseModel responseModel) {
                                            viewHolder.tv_num.setText(num+"");
                                            if (totalPriceListener != null) {
                                                product.setProductNum(num + "");
                                                totalPriceListener.addOrSub(list);
                                            }
                                        }

                                        @Override
                                        public void onBusinessError(int code, String msg) {
                                            ToastUtils.showLongToast(context, msg);
                                        }
                                    }, null);
                                }

                            }
                        })
                        .create(amount)
                        .show();
            }
        });
        viewHolder.ll_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra(Const.MALL_GOOD_ID_KEY,product.getProductID());
                context.startActivity(intent);
            }
        });
    }

    private void changeNum(final int amount, final TextView textView, final CartBean.Product product, final TotalPriceListener totalPriceListener,final TextView reductTv) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("shopProductID", product.getShopProductID());
        jsonObject.put("productNum", amount + "");
        ApiManager.get().getData(Const.UPDATA_SHOP_CART_PRODUCT_TAG, jsonObject, new BusinessBackListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                textView.setText(amount+"");
                if (reductTv!=null){
                    changeAddAndReduceColor(reductTv,amount);
                }
                if (totalPriceListener != null) {
                    product.setProductNum(amount + "");
                    totalPriceListener.addOrSub(list);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(context, msg);
            }
        }, new LoadingDialog.Builder(context).setMessage(context.getString(R.string.submitting_tips)).create());
    }

//    private boolean getSelected() {
//        boolean hasSelect = false;
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).isSelect()) {
//                hasSelect = true;
//            }
//        }
//        return hasSelect;
//    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_check;
        ImageView iv_product;
        TextView tv_product_name;
        TextView tv_product_format;
//        TextView is_have_coupon;
//        AmountView amount_view;
        TextView tv_unit_price;
        TextView btn_reduce;
        TextView tv_num;
        TextView btn_add;
        TextView tv_back_value;
        LinearLayout ll_product;

        public ProductViewHolder(View itemView) {
            super(itemView);
            iv_check = itemView.findViewById(R.id.iv_check);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_format = itemView.findViewById(R.id.tv_product_format);
            tv_unit_price = itemView.findViewById(R.id.tv_unit_price);
//            is_have_coupon = itemView.findViewById(R.id.is_have_coupon);
//            amount_view = itemView.findViewById(R.id.amount_view);
            btn_reduce = itemView.findViewById(R.id.btn_reduce);
            tv_num = itemView.findViewById(R.id.tv_num);
            btn_add = itemView.findViewById(R.id.btn_add);
            tv_back_value = itemView.findViewById(R.id.tv_back_value);
            ll_product = itemView.findViewById(R.id.ll_product);

        }
    }


    public interface TotalPriceListener {
        void addOrSub(List<CartBean.Product> cartProductModels);
    }

    public boolean isTrue(String str) {
        if (!TextUtils.isEmpty(str)) {
            if (str.equals("1")) {//支持
                return true;
            } else if (str.equals("0")) {
                return false;
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changeAddAndReduceColor(TextView tv, int num){
        if (num==1){
            tv.setEnabled(false);
            tv.setTextColor(context.getColor(R.color.COLOR_FFE9E9E9));
        }else {
            tv.setEnabled(true);
            tv.setTextColor(context.getColor(R.color.COLOR_FF666666));
        }
    }

}
