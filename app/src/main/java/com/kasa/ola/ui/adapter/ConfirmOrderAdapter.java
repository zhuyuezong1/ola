package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.bean.entity.ConfirmOrderInfoBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.ToastUtils;

import java.util.List;

public class ConfirmOrderAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ConfirmOrderInfoBean.CommitInfoListBean> list;
    public ConfirmOrderAdapter(Context context, List<ConfirmOrderInfoBean.CommitInfoListBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConfirmOrderAdapter.ConfirmViewHolder(LayoutInflater.from(context).inflate(R.layout.confirm_order_item_new, parent, false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ConfirmViewHolder viewHolder = (ConfirmViewHolder) holder;
        final ConfirmOrderInfoBean.CommitInfoListBean commitInfoListBean = list.get(position);
        viewHolder.tv_shop_name.setText(commitInfoListBean.getSuppliers());
        ProductConfirmAdapter productConfirmAdapter = new ProductConfirmAdapter(context, commitInfoListBean.getProductList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewHolder.product_list.setLayoutManager(linearLayoutManager);
        viewHolder.product_list.setAdapter(productConfirmAdapter);
        viewHolder.product_list.setVisibility(View.VISIBLE);
        viewHolder.tv_product_total_price.setText(commitInfoListBean.getTotalPrice());
        viewHolder.product_num.setText(context.getString(R.string.total_product_num_new,commitInfoListBean.getTotalNum()));
        viewHolder.tv_total_money.setText("￥" + commitInfoListBean.getTotalPrice());
//        viewHolder.tv_product_name.setText(cartProductModel.getProductName());
//        viewHolder.tv_product_format.setText(cartProductModel.getProductFormat());
//        viewHolder.tv_unit_price.setText("￥"+cartProductModel.getUnitPrice());
//        viewHolder.is_have_coupon.setVisibility(cartProductModel.isHaveCoupon()?View.VISIBLE:View.GONE);
        viewHolder.et_remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s!=null && !TextUtils.isEmpty(s.toString())){
                    int num = Integer.parseInt(s.toString());
                    if (num>20){
                        ToastUtils.showLongToast(context,"最多只能输入20个字");
                    }
                }
                commitInfoListBean.setRemark(viewHolder.et_remark.getText().toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();

    }

    public class ConfirmViewHolder extends RecyclerView.ViewHolder {

        TextView tv_shop_name;
        RecyclerView product_list;
        TextView tv_product_total_price;
        TextView product_num;
        TextView tv_title_money;
        TextView tv_total_money;
        EditText et_remark;

        public ConfirmViewHolder(View itemView) {
            super(itemView);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            product_list = itemView.findViewById(R.id.product_list);
            tv_product_total_price = itemView.findViewById(R.id.tv_product_total_price);
            product_num = itemView.findViewById(R.id.product_num);
            tv_title_money = itemView.findViewById(R.id.tv_title_money);
            tv_total_money = itemView.findViewById(R.id.tv_total_money);
            et_remark = itemView.findViewById(R.id.et_remark);

        }
    }


}
