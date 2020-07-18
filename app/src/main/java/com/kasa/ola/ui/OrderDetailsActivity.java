package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.MallOrderBean;
import com.kasa.ola.bean.entity.OrderInfoBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.OrdinaryDialog;
import com.kasa.ola.dialog.QRCodeDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.pop.PayPopupWindow;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.ui.popwindow.PayPop;
import com.kasa.ola.utils.DateUtil;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import razerdp.basepopup.BasePopupWindow;

public class OrderDetailsActivity extends BaseActivity implements View.OnClickListener/*, WechatPayManager.OnWechatPayListener*/ {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R.id.tv_order_status_desc)
    TextView tvOrderStatusDesc;
    @BindView(R.id.iv_store_img)
    ImageView ivStoreImg;
    @BindView(R.id.tv_store_name)
    TextView tvStoreName;
    @BindView(R.id.view_good_list)
    LinearLayout viewGoodList;
    //    @BindView(R.id.tv_goods_price)
//    TextView tvGoodsPrice;
    @BindView(R.id.tv_real_price_name)
    TextView tvRealPriceName;
    @BindView(R.id.tv_real_price)
    TextView tvRealPrice;
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.tv_pay_time)
    TextView tvPayTime;
    @BindView(R.id.tv_pay_method)
    TextView tvPayMethod;
    @BindView(R.id.tv_operate_left)
    TextView tvOperateLeft;
    @BindView(R.id.tv_operate_right)
    TextView tvOperateRight;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_operate_middle)
    TextView tvOperateMiddle;
    @BindView(R.id.tv_total_back_value)
    TextView tvTotalBackValue;
    @BindView(R.id.tv_logistics_info)
    TextView tvLogisticsInfo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_logistics)
    LinearLayout llLogistics;
    @BindView(R.id.tv_self_mention_point_flag)
    TextView tvSelfMentionPointFlag;
    private int orderStatus = 1;
    private int totalNum = 0;
    private String totalPrice;
    private CountDownTimer timer;
    private String orderNo;
    private String logisticsUrl;
    private ArrayList<PayTypeModel> payTypeList = new ArrayList<>();
    private PasswordPopupWin pwdPop;
    private PayPop payPop;
    private int payType = 1;
    private String rebates;
    private PayMsgModel payMsgModel;
    private OrderInfoBean orderInfoBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);
        orderNo = getIntent().getStringExtra(Const.ORDER_ID_KEY);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.order_detail), "");
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                loadPage();
            }
        });
        tvOperateLeft.setOnClickListener(this);
        tvOperateMiddle.setOnClickListener(this);
        tvOperateRight.setOnClickListener(this);

        loadPage();
    }

    private void loadPage() {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("orderNo", orderNo);
        ApiManager.get().getData(Const.GET_ORDER_DETAIL_TAG, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    orderInfoBean = JsonUtils.jsonToObject(jo.toString(), OrderInfoBean.class);
                    orderStatus = orderInfoBean.getOrderStatus();
                    totalPrice = orderInfoBean.getTotalPrice();
                    showView(orderInfoBean);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(OrderDetailsActivity.this, msg);
            }
        }, loadingView);
    }

    private void showView(OrderInfoBean orderInfoBean) {
        showHeadOrderView(orderInfoBean);
        showHostInfo(orderInfoBean);
        showLogistics(orderInfoBean);
        showProductList(orderInfoBean);
        showOrderBaseInfo(orderInfoBean);
        showBottomView(orderInfoBean);
        loadExpressInfo(orderInfoBean);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_logistics:
                if (!TextUtils.isEmpty(logisticsUrl)) {
                    Intent logisticsIntent = new Intent(OrderDetailsActivity.this, WebActivity.class);
                    logisticsIntent.putExtra(Const.WEB_TITLE, getString(R.string.logistics_msg));
                    logisticsIntent.putExtra(Const.INTENT_URL, logisticsUrl);
                    startActivity(logisticsIntent);
                }
                break;
            case R.id.tv_operate_left:
                switch (orderStatus) {
                    case 2:
                    case 3:
                    case 9:
                        OrdinaryDialog.Builder builder = new OrdinaryDialog.Builder(OrderDetailsActivity.this);
                        builder.setMessage(OrderDetailsActivity.this.getString(R.string.return_good_tips))
                                .setTitle(OrderDetailsActivity.this.getString(R.string.return_good_tips_title))
                                .setLeftButton(getString(R.string.cancel))
                                .setRightButton(getString(R.string.confirm))
                                .setDialogInterface(new OrdinaryDialog.DialogInterface() {
                                    @Override
                                    public void disagree(OrdinaryDialog dialog) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void agree(OrdinaryDialog dialog) {
                                        updateMallOrderState(orderNo, 2);
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                        break;
                }
                break;
            case R.id.tv_operate_middle:
                switch (orderStatus) {
                    case 3:
                        ToastUtils.showLongToast(OrderDetailsActivity.this, "查看物流");
                        break;
                    case 4:
                        Intent intent = new Intent(OrderDetailsActivity.this, PublishProductCommentActivity.class);
                        intent.putExtra(Const.ORDER_ID_KEY, orderInfoBean.getOrderNo());
                        List<MallOrderBean.ProductOrder> productOrderList = new ArrayList<>();
                        for (int i = 0; i < orderInfoBean.getProductList().size(); i++) {
                            MallOrderBean.ProductOrder productOrder = new MallOrderBean.ProductOrder();
                            productOrder.setProductID(orderInfoBean.getProductList().get(i).getProductID());
                            productOrder.setImageUrl(orderInfoBean.getProductList().get(i).getImageUrl());
                            productOrder.setSpe(orderInfoBean.getProductList().get(i).getSpe());
                            productOrder.setProductName(orderInfoBean.getProductList().get(i).getProductName());
                            productOrderList.add(productOrder);
                        }
                        intent.putExtra(Const.PUBLISH_PRODUCT_COMMENT, (Serializable) productOrderList);
                        startActivityForResult(intent, Const.PUBLISH_COMMENT_SUCCESS);
                        break;
                }
                break;
            case R.id.tv_operate_right:
                switch (orderStatus) {
                    case 1:
                        ArrayList<String> orderNoList = new ArrayList<>();
                        orderNoList.add(orderNo);
//                        Intent intent = new Intent(OrderDetailActivity.this, PayActivity.class);
                        payMsgModel = new PayMsgModel();
                        payMsgModel.totalPrice = totalPrice;
                        payMsgModel.orderList = orderNoList;
//                        intent.putExtra(Const.PAY_MSG_KEY, payMsgModel);
//                        startActivity(intent);
                        showPayPop(payMsgModel);

                        break;
                    case 2:
                        break;
                    case 3:
                        CommonDialog.Builder commonBuilder = new CommonDialog.Builder(OrderDetailsActivity.this);
                        commonBuilder.setMessage(getString(R.string.confirm_accept_good_tips))
                                .setLeftButton(getString(R.string.cancel))
                                .setRightButton(getString(R.string.confirm))
                                .setDialogInterface(new CommonDialog.DialogInterface() {

                                    @Override
                                    public void leftButtonClick(CommonDialog dialog) {
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void rightButtonClick(CommonDialog dialog) {
                                        updateMallOrderState(orderNo, 1);
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                        break;
                    case 4:

                        break;
                    case 9:
                        QRCodeDialog.Builder builder = new QRCodeDialog.Builder(OrderDetailsActivity.this);
                        builder.setMessage(getString(R.string.show_qr_code_intro))
                                .setCodeImageUrl(orderInfoBean.getCodeImageUrl())
                                .setDialogInterface(new QRCodeDialog.DialogInterface() {
                                    @Override
                                    public void close(QRCodeDialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                        break;
                }
                break;
//            case R.id.tv_get_detail:
//                Intent intent = new Intent(OrderDetailActivity.this, WebActivity.class);
//                intent.putExtra(Const.WEB_TITLE, getString(R.string.logistics_msg));
//                intent.putExtra(Const.INTENT_URL, logisticsUrl);
//                startActivity(intent);
//                break;
        }
    }

    private void updateMallOrderState(String orderID, int type) {
        ApiManager.get().updateMallOrderState(orderID, type, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(OrderDetailsActivity.this, responseModel.resultCodeDetail);
                loadPage();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(OrderDetailsActivity.this, msg);
            }
        }, this);
    }

    private void loadExpressInfo(OrderInfoBean orderInfoBean) {
        JSONObject jo = new JSONObject();
        jo.put("expressName", orderInfoBean.getExpress().getExpressName());
        jo.put("expressNum", orderInfoBean.getExpress().getExpressNum());
        ApiManager.get().getData(Const.GET_LOGISTICS_URL, jo, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    logisticsUrl = jo.optString("url");
                }

            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(OrderDetailsActivity.this, msg);
            }
        }, loadingView);
    }

    private void showBottomView(OrderInfoBean orderInfoBean) {
        /**************bottomview操作按钮***************/
        switch (orderStatus) {
            case 1:
                llBottom.setVisibility(View.VISIBLE);
                tvOperateLeft.setVisibility(View.GONE);
                tvOperateMiddle.setVisibility(View.GONE);
                tvOperateRight.setVisibility(View.VISIBLE);
                tvOperateLeft.setText("");
                tvOperateMiddle.setText("");
                tvOperateRight.setText(getString(R.string.pay_right_now));
                break;
            case 2:
                llBottom.setVisibility(View.VISIBLE);
                tvOperateLeft.setVisibility(View.VISIBLE);
                tvOperateMiddle.setVisibility(View.GONE);
                tvOperateRight.setVisibility(View.GONE);
                tvOperateLeft.setText(getString(R.string.apply_return_back));
                tvOperateMiddle.setText("");
                tvOperateRight.setText("");
                break;
            case 3:
                llBottom.setVisibility(View.VISIBLE);
                tvOperateLeft.setVisibility(View.VISIBLE);
                tvOperateMiddle.setVisibility(View.VISIBLE);
                tvOperateRight.setVisibility(View.VISIBLE);
                tvOperateLeft.setText(getString(R.string.apply_return_back));
                tvOperateMiddle.setText(getString(R.string.check_logistics));
                tvOperateRight.setText(getString(R.string.confirm_receipt));
                break;
            case 4:
                llBottom.setVisibility(View.VISIBLE);
                tvOperateLeft.setVisibility(View.GONE);
                tvOperateMiddle.setVisibility(View.VISIBLE);
                tvOperateRight.setVisibility(View.GONE);
                tvOperateLeft.setText("");
                tvOperateMiddle.setText(getString(R.string.comment_right_now));
                tvOperateRight.setText("");
                break;
            case 5://已完成
            case 7:
            case 8:
                llBottom.setVisibility(View.GONE);
                break;
            case 9:
                llBottom.setVisibility(View.VISIBLE);
                tvOperateLeft.setVisibility(View.VISIBLE);
                tvOperateMiddle.setVisibility(View.GONE);
                tvOperateRight.setVisibility(View.VISIBLE);
                tvOperateLeft.setText(getString(R.string.apply_return_back));
                tvOperateMiddle.setText("");
                tvOperateRight.setText(getString(R.string.check_qr_code));
                break;
            default:
                llBottom.setVisibility(View.GONE);
                tvOperateLeft.setVisibility(View.GONE);
                tvOperateMiddle.setVisibility(View.GONE);
                tvOperateRight.setVisibility(View.GONE);
                break;
        }
    }

    private void showOrderBaseInfo(OrderInfoBean orderInfoBean) {
        tvRealPriceName.setText(getString(R.string.total_product_num, orderInfoBean.getTotalNum()));
        tvRealPrice.setText(getString(R.string.commission_value, orderInfoBean.getTotalPrice()));
        tvTotalBackValue.setText(getString(R.string.commission_value, orderInfoBean.getRebatesAll()));
        if (orderStatus == 1) {
            tvOrderNo.setVisibility(View.VISIBLE);
            tvOrderTime.setVisibility(View.VISIBLE);
            tvPayTime.setVisibility(View.GONE);
            tvPayMethod.setVisibility(View.GONE);
            tvOrderNo.setText(getString(R.string.order_id, orderInfoBean.getOrderNo()));
            tvOrderTime.setText(getString(R.string.create_order_time, orderInfoBean.getOrderCreateTime()));
        } else {
            tvOrderNo.setVisibility(View.VISIBLE);
            tvOrderTime.setVisibility(View.VISIBLE);
            tvPayTime.setVisibility(View.VISIBLE);
            tvPayMethod.setVisibility(View.VISIBLE);
            tvOrderNo.setText(getString(R.string.order_id, orderInfoBean.getOrderNo()));
            tvOrderTime.setText(getString(R.string.create_order_time, orderInfoBean.getOrderCreateTime()));
            tvPayTime.setText(getString(R.string.order_pay_time, orderInfoBean.getOrderPayTime()));
            tvPayMethod.setText(getString(R.string.pay_method, orderInfoBean.getPayment()));
        }
    }

    private void showLogistics(OrderInfoBean orderInfoBean) {
        /**************物流信息及其列表***************/
//        llLogistics.setVisibility(orderStatus == 3 ? View.VISIBLE : View.GONE);
////        tvExpressName.setText(orderInfoBean.getExpress().getExpressName());
////        tvExpressNum.setText(orderInfoBean.getExpress().getExpressNum());
//        tvLogisticsInfo.setText(orderInfoBean.getExpress().getExpressContent());
//        tvLogisticsTime.setText(orderInfoBean.getExpress().getExpressTime());
//        llLogistics.setOnClickListener(this);
        if (orderStatus == 3) {
            if (TextUtils.isEmpty(orderInfoBean.getExpress().getExpressNum())) {
                llLogistics.setVisibility(View.GONE);
            } else {
                llLogistics.setVisibility(View.VISIBLE);
                tvLogisticsInfo.setText(TextUtils.isEmpty(orderInfoBean.getExpress().getExpressContent()) ? getString(R.string.no_logistics_info) : orderInfoBean.getExpress().getExpressContent());
                tvTime.setText(TextUtils.isEmpty(orderInfoBean.getExpress().getExpressTime()) ? "" : orderInfoBean.getExpress().getExpressTime());
            }
            llLogistics.setOnClickListener(this);
        } else {
            llLogistics.setVisibility(View.GONE);
        }

//        switch (orderStatus) {
//            case 3:
//                if (TextUtils.isEmpty(orderInfoBean.getExpress().getExpressNum())) {
//                    llLogistics.setVisibility(View.GONE);
//                } else {
//                    llLogistics.setVisibility(View.VISIBLE);
//                    tvLogisticsInfo.setText(orderInfoBean.getExpress().getExpressContent());
//                    tvLogisticsTime.setText(orderInfoBean.getExpress().getExpressTime());
//                }
//                llLogistics.setOnClickListener(this);
//                break;
//            case 4:
//            case 5:
//                if (TextUtils.isEmpty(orderInfoBean.getExpress().getExpressNum())) {
//                    llLogistics.setVisibility(View.GONE);
//                } else {
//                    llLogistics.setVisibility(View.VISIBLE);
//                    tvLogisticsInfo.setText(getString(R.string.finish_express_tips));
//                    tvLogisticsTime.setVisibility(View.GONE);
//                }
//                llLogistics.setOnClickListener(this);
//                break;
//        }
    }


    private void showProductList(OrderInfoBean orderInfoBean) {
        /**************店铺名及商品列表***************/
        tvStoreName.setText(orderInfoBean.getSuppliers());
        List<ProductBean> productList = orderInfoBean.getProductList();
        viewGoodList.removeAllViews();
        for (int i = 0; i < productList.size(); i++) {
            ProductBean productBean = productList.get(i);
            View goodItem = LayoutInflater.from(this).inflate(R.layout.view_mall_order_details_good_item, viewGoodList, false);
            LinearLayout ll_product = goodItem.findViewById(R.id.ll_product);
            ll_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderDetailsActivity.this, ProductDetailsActivity.class);
                    intent.putExtra(Const.MALL_GOOD_ID_KEY, productBean.getProductID());
                    startActivity(intent);
                }
            });
            ImageView iv_good_img = goodItem.findViewById(R.id.iv_good_img);
            TextView tv_good_desc = goodItem.findViewById(R.id.tv_good_desc);
            TextView tv_back_value = goodItem.findViewById(R.id.tv_back_value);
            TextView tv_product_num = goodItem.findViewById(R.id.tv_product_num);
            TextView tv_good_sku = goodItem.findViewById(R.id.tv_good_sku);
            TextView tv_good_price = goodItem.findViewById(R.id.tv_good_price);
            TextView tv_special_price = goodItem.findViewById(R.id.tv_special_price);
            ImageLoaderUtils.imageLoad(this, productBean.getImageUrl(), iv_good_img);
            tv_good_desc.setText(productBean.getProductName());
            tv_back_value.setText(getString(R.string.back_value, productBean.getRebates()));
            tv_product_num.setText("×" + productBean.getProductNum());
            tv_good_sku.setText(/*"数量:" + productBean.getProductNum() + "  " + */productBean.getSpe());
            tv_good_price.setText(getString(R.string.commission_value, productBean.getPrice()));
            tv_special_price.setText(getString(R.string.commission_value, productBean.getSpecialPrice()));
            tv_special_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            viewGoodList.addView(goodItem);
            totalNum += Integer.parseInt(productBean.getProductNum());
        }
    }

    private void showHostInfo(OrderInfoBean orderInfoBean) {
        /**************收货人信息***************/
        if (orderInfoBean.getAddress() == null | (orderInfoBean.getAddress()!=null&& TextUtils.isEmpty(orderInfoBean.getAddress().getProvince()))) {
            if (TextUtils.isEmpty(orderInfoBean.getTakeAddress().getProvince())) {
                llAddress.setVisibility(View.GONE);
            } else {
                llAddress.setVisibility(View.VISIBLE);
                tvName.setText(orderInfoBean.getTakeAddress().getName());
                tvSelfMentionPointFlag.setVisibility(View.VISIBLE);
                tvPhoneNumber.setVisibility(View.GONE);
                StringBuilder addrSb = new StringBuilder();
                addrSb.append(orderInfoBean.getTakeAddress().getProvince())
                        .append(orderInfoBean.getTakeAddress().getCity())
                        .append(orderInfoBean.getTakeAddress().getArea())
                        .append(orderInfoBean.getTakeAddress().getAddressDetail());
                tvAddress.setText(addrSb);
            }
        } else {
            if (TextUtils.isEmpty(orderInfoBean.getAddress().getProvince())) {
                llAddress.setVisibility(View.GONE);
            } else {
                llAddress.setVisibility(View.VISIBLE);
                tvName.setText(orderInfoBean.getAddress().getName());
                tvSelfMentionPointFlag.setVisibility(View.GONE);
                tvPhoneNumber.setVisibility(View.VISIBLE);
                tvPhoneNumber.setText(orderInfoBean.getAddress().getMobile());
                StringBuilder addrSb = new StringBuilder();
                addrSb.append(orderInfoBean.getAddress().getProvince())
                        .append(orderInfoBean.getAddress().getCity())
                        .append(orderInfoBean.getAddress().getArea())
                        .append(orderInfoBean.getAddress().getAddressDetail());
                tvAddress.setText(addrSb);
            }
        }
    }

    private void showHeadOrderView(OrderInfoBean orderInfoBean) {
        String statusStr = "";
        switch (orderStatus) {
            case 1:
                /**************头部订单状态信息，下同***************/
                statusStr = getString(R.string.wait_pay);
                timer = setTimer((int) (DateUtil.formatTurnSecond("0:" + orderInfoBean.getOrderCloseTime()
                        .replace("分", ":").replace("秒", "")) * 1000), 1000);
                timer.start();
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_wait_for_pay);
//                ivPayStatus.setImageResource(R.mipmap.dzficon);
                break;
            case 2:
                statusStr = getString(R.string.wait_send);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_wait_for_send);
//                ivPayStatus.setImageResource(R.mipmap.cfzicon);
                break;
            case 3:
                statusStr = getString(R.string.wait_for_accept);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_wait_for_accept);
//                ivPayStatus.setImageResource(R.mipmap.cfzicon);
                break;
            case 4:
                statusStr = getString(R.string.wait_for_discuss);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_wait_for_discuss);
//                ivPayStatus.setImageResource(R.mipmap.cfzicon);
                break;
            case 5:
                statusStr = getString(R.string.finished);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_complete);
//                ivPayStatus.setImageResource(R.mipmap.wc_icon);
                break;
            case 6:
                break;
            case 7:
                statusStr = getString(R.string.have_closed);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_order_close);
//                ivPayStatus.setImageResource(R.mipmap.wc_icon);
                break;
            case 8:
                statusStr = getString(R.string.after_sale_1);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_after_sale);
//                ivPayStatus.setImageResource(R.mipmap.wc_icon);
                break;
            case 9:
                statusStr = getString(R.string.can_use);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.icon_can_use);
                llAddress.setVisibility(View.GONE);
                break;
        }
        tvOrderStatus.setText(statusStr);
    }

    private CountDownTimer setTimer(final int millisInFuture, int countDownInterval) {
        return new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (!isFinishing()) {
                    String orderTime = getString(R.string.waite_pay_hint, DateUtil.timeParse(millisUntilFinished));
                    tvOrderStatusDesc.setText(orderTime);
                }
            }

            @Override
            public void onFinish() {
                ToastUtils.showShortToast(OrderDetailsActivity.this, getString(R.string.order_invalid));
                finish();
            }
        };
    }

    private void showPayPop(PayMsgModel payMsgModel) {
        PayPopupWindow payPopup = new PayPopupWindow(OrderDetailsActivity.this, new PayPopupWindow.PayPopListener() {
            @Override
            public void onPayResult(int payTypeNum, String totalMoney) {
                payType = payTypeNum;
                payResult(payTypeNum, totalMoney);
            }
        }, new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                finish();
            }
        }, Integer.parseInt(orderInfoBean.getTotalNum()), payMsgModel, llBottom);
        payPopup.showPopupWindow();
    }

    private void payResult(int payTypeNum, String totalMoney) {
        Intent intent = new Intent(OrderDetailsActivity.this, PaySuccessResultActivity.class);
        intent.putExtra(Const.PAY_VALUE, totalMoney);
        intent.putExtra(Const.ORDER_ENTER_TYPE, 1);
        startActivity(intent);
    }

}
