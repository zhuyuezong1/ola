package com.kasa.ola.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.MallOrderBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.OrderDetailActivity;
import com.kasa.ola.utils.ImageLoaderUtils;

import java.util.ArrayList;

/**
 * Created by guan on 2018/11/14 0014.
 */
public class MallOrderAdapterOld extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<MallOrderBean> mallOrderList;
    private MallOrderListener mallOrderListener;

    public void setMallOrderListener(MallOrderListener mallOrderListener) {
        this.mallOrderListener = mallOrderListener;
    }

    public interface MallOrderListener {
        void goPay(MallOrderBean mallOrderBean);

        void conform(String orderID);

        void cancel(String orderID);

        void orderDetails(String orderID);
    }

    public MallOrderAdapterOld(Context context, ArrayList<MallOrderBean> mallOrderList) {
        this.context = context;
        this.mallOrderList = mallOrderList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MallOrderViewHolder(LayoutInflater.from(context).inflate(R.layout.view_mall_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MallOrderBean mallOrderBean = mallOrderList.get(position);
        final MallOrderViewHolder viewHolder = (MallOrderViewHolder) holder;
//        ImageLoaderUtils.imageLoad(context, mallOrderModel.imageUrl, viewHolder.iv_store_img);
        viewHolder.tv_store_name.setText(mallOrderBean.getSuppliers());
        viewHolder.tv_order_id.setText(context.getString(R.string.order_id, mallOrderBean.getOrderNo()));
        String orderStatusStr = "";
        String leftText = "";
        String rightText = "";
        final int orderStatus = mallOrderBean.getOrderStatus();
        switch (orderStatus) {
            case 1:
                orderStatusStr = context.getString(R.string.wait_pay);
                viewHolder.tv_operate_left.setVisibility(View.GONE);
                viewHolder.tv_operate_right.setVisibility(View.VISIBLE);
                rightText = context.getString(R.string.go_pay);
                break;
            case 2:
                orderStatusStr = context.getString(R.string.finish_pay);
                viewHolder.tv_operate_left.setVisibility(View.GONE);
                viewHolder.tv_operate_right.setVisibility(View.GONE);
                leftText = "";
                rightText = "";
                break;
            case 3:
                orderStatusStr = context.getString(R.string.finish_deliver);
                viewHolder.tv_operate_left.setVisibility(View.GONE);
                viewHolder.tv_operate_right.setVisibility(View.VISIBLE);
                rightText = context.getString(R.string.confirm_receipt);
                break;
            case 4:
                orderStatusStr = context.getString(R.string.finished);
                viewHolder.tv_operate_left.setVisibility(View.GONE);
                viewHolder.tv_operate_right.setVisibility(View.GONE);
                leftText = "";
                rightText = "";
                break;
//            case 5:
//                orderStatusStr = context.getString(R.string.in_return);
//                viewHolder.tv_operate_left.setVisibility(View.GONE);
//                viewHolder.tv_operate_right.setVisibility(View.GONE);
//                viewHolder.tv_operate_right.setText(context.getString(R.string.cancel));
//                leftText = "";
////                rightText = "";
////                rightText = context.getString(R.string.cancel);
//                break;
//            case 6:
//                orderStatusStr = context.getString(R.string.refunded);
//                viewHolder.tv_operate_left.setVisibility(View.GONE);
//                viewHolder.tv_operate_right.setVisibility(View.GONE);
//                leftText = "";
////                rightText = "";
//                break;
            default:
                orderStatusStr = "";
                viewHolder.tv_operate_left.setVisibility(View.GONE);
                viewHolder.tv_operate_right.setVisibility(View.GONE);
                leftText = "";
//                rightText = "";
        }
        viewHolder.tv_operate_left.setText(leftText);
        viewHolder.tv_operate_right.setText(rightText);
        viewHolder.tv_order_status.setText(orderStatusStr);
        viewHolder.tv_good_num.setText(context.getString(R.string.good_num, mallOrderBean.getTotalNum() + ""));
        viewHolder.tv_total_price.setText(mallOrderBean.getTotalPrice());

        viewHolder.view_good_list.removeAllViews();
        for (int i = 0; i < mallOrderBean.getProductList().size(); i++) {
            MallOrderBean.ProductOrder productOrder = mallOrderBean.getProductList().get(i);
            View goodItem = LayoutInflater.from(context).inflate(R.layout.view_mall_order_good_item, viewHolder.view_good_list, false);
            ImageView iv_good_img = goodItem.findViewById(R.id.iv_good_img);
            TextView tv_good_desc = goodItem.findViewById(R.id.tv_good_desc);
            TextView tv_good_sku = goodItem.findViewById(R.id.tv_good_sku);
            TextView tv_good_price = goodItem.findViewById(R.id.tv_good_price);
            ImageLoaderUtils.imageLoad(context, productOrder.getImageUrl(), iv_good_img);
            tv_good_desc.setText(productOrder.getProductName());
            tv_good_sku.setText("数量:" + productOrder.getProductNum() + "  " + productOrder.getSpe());
            tv_good_price.setText(context.getString(R.string.price, productOrder.getPrice()));
            viewHolder.view_good_list.addView(goodItem);
        }
        viewHolder.view_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (null != mallOrderListener) {
//                    mallOrderListener.orderDetails(mallOrderBean.getOrderNo());
//                }

                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra(Const.ORDER_ID_KEY, mallOrderBean.getOrderNo());
                context.startActivity(intent);
            }
        });

        viewHolder.tv_operate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (orderStatus) {
                    case 1:
                        if (null != mallOrderListener) {
                            mallOrderListener.goPay(mallOrderBean);
                        }
                        break;
                    case 2:
                        break;
                    case 3:
                        if (null != mallOrderListener) {
                            mallOrderListener.conform(mallOrderBean.getOrderNo());
                        }
                        break;
                    case 4:
                        break;
//                    case 5:
//                        if (null != mallOrderListener) {
//                            mallOrderListener.cancel(mallOrderBean.getOrderNo());
//                        }
//                        break;
//                    case 6:
//                        break;
                    default:
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mallOrderList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    private class MallOrderViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_store_img;
        private TextView tv_store_name;
        private TextView tv_order_id;
        private TextView tv_order_status;
        private LinearLayout view_good_list;
        private TextView tv_good_num;
        private TextView tv_total_price;
        private TextView tv_operate_left;
        private TextView tv_operate_right;
        private View view_head;

        public MallOrderViewHolder(View itemView) {
            super(itemView);
            iv_store_img = itemView.findViewById(R.id.iv_store_img);
            tv_store_name = itemView.findViewById(R.id.tv_store_name);
            tv_order_id = itemView.findViewById(R.id.tv_order_id);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
            view_good_list = itemView.findViewById(R.id.view_good_list);
            tv_good_num = itemView.findViewById(R.id.tv_good_num);
            tv_total_price = itemView.findViewById(R.id.tv_total_price);
            tv_operate_left = itemView.findViewById(R.id.tv_operate_left);
            tv_operate_right = itemView.findViewById(R.id.tv_operate_right);
            view_head = itemView.findViewById(R.id.view_head);
        }
    }
}
