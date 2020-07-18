package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alipay.sdk.app.PayTask;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.PayResult;
import com.kasa.ola.bean.entity.MallOrderBean;
import com.kasa.ola.bean.entity.OrderInfoBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.PaySuccessDialog;
import com.kasa.ola.dialog.OrdinaryDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.manager.WechatPayManager;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.ui.popwindow.PayPop;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.DateUtil;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener, WechatPayManager.OnWechatPayListener {


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
//    @BindView(R.id.ll_logistics)
//    LinearLayout llLogistics;
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
    private static final int SDK_PAY_FLAG = 1;
    private AlipayHandler alipayHandler = new AlipayHandler(this);
    private PayMsgModel payMsgModel;
    private OrderInfoBean orderInfoBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        orderNo = getIntent().getStringExtra(Const.ORDER_ID_KEY);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.details), "");
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                loadPage();
            }
        });
        tvOperateLeft.setOnClickListener(this);
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
                ToastUtils.showShortToast(OrderDetailActivity.this, msg);
            }
        }, loadingView);
    }

    private void showView(OrderInfoBean orderInfoBean) {
        showHeadOrderView(orderInfoBean);
        showHostInfo(orderInfoBean);
//        showLogistics(orderInfoBean);
        showProductList(orderInfoBean);
        showTimeAndPrice(orderInfoBean);
        showOrderBaseInfo(orderInfoBean);
        showBottomView(orderInfoBean);
        loadExpressInfo(orderInfoBean);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ll_logistics:
//                if (!TextUtils.isEmpty(logisticsUrl)) {
//                    Intent logisticsIntent = new Intent(OrderDetailActivity.this, WebActivity.class);
//                    logisticsIntent.putExtra(Const.WEB_TITLE, getString(R.string.logistics_msg));
//                    logisticsIntent.putExtra(Const.INTENT_URL, logisticsUrl);
//                    startActivity(logisticsIntent);
//                }
//                break;
            case R.id.tv_operate_left:
                switch (orderStatus) {
                    case 2:
                    case 3:
                        OrdinaryDialog.Builder builder = new OrdinaryDialog.Builder(OrderDetailActivity.this);
                        builder.setMessage(OrderDetailActivity.this.getString(R.string.return_good_tips))
                                .setTitle(OrderDetailActivity.this.getString(R.string.return_good_tips_title))
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
                        break;
                    case 4:
                        Intent intent = new Intent(OrderDetailActivity.this, PublishProductCommentActivity.class);
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
                ToastUtils.showShortToast(OrderDetailActivity.this, responseModel.resultCodeDetail);
                loadPage();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(OrderDetailActivity.this, msg);
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
                ToastUtils.showShortToast(OrderDetailActivity.this, msg);
            }
        }, loadingView);
    }

    private void showBottomView(OrderInfoBean orderInfoBean) {
        /**************bottomview操作按钮***************/
        if (orderStatus == 1) {
            tvOperateLeft.setVisibility(View.GONE);
            tvOperateRight.setVisibility(View.VISIBLE);
            tvOperateLeft.setText("");
            tvOperateRight.setText(getString(R.string.go_pay));
        } else if (orderStatus == 2) {
            tvOperateLeft.setVisibility(View.VISIBLE);
            tvOperateRight.setVisibility(View.GONE);
            tvOperateLeft.setText(getString(R.string.apply_return_back));
            tvOperateRight.setText("");
        } else if (orderStatus == 3) {
            tvOperateLeft.setVisibility(View.VISIBLE);
            tvOperateRight.setVisibility(View.VISIBLE);
            tvOperateLeft.setText(getString(R.string.apply_return_back));
            tvOperateRight.setText(getString(R.string.confirm_receipt));
        } else if (orderStatus == 4) {
            tvOperateLeft.setVisibility(View.GONE);
            tvOperateRight.setVisibility(View.VISIBLE);
            tvOperateLeft.setText("");
            tvOperateRight.setText(getString(R.string.go_comment));
        } else {
            tvOperateLeft.setVisibility(View.GONE);
            tvOperateRight.setVisibility(View.GONE);
        }
//        if (orderInfoBean.getDistribution().equals("0")) {
//            llBottom.setVisibility(View.VISIBLE);
//        } else {
//            llBottom.setVisibility(View.GONE);
//        }
    }

    private void showOrderBaseInfo(OrderInfoBean orderInfoBean) {
        if (orderStatus == 1) {
            tvOrderNo.setVisibility(View.VISIBLE);
            tvOrderTime.setVisibility(View.VISIBLE);
            tvPayTime.setVisibility(View.GONE);
            tvPayMethod.setVisibility(View.GONE);
            tvOrderNo.setText(getString(R.string.order_id, orderInfoBean.getOrderNo()));
            tvOrderTime.setText(getString(R.string.create_order_time, orderInfoBean.getOrderCreateTime()));
        } else if (orderStatus == 2 || orderStatus == 3 || orderStatus == 4 || orderStatus == 5 || orderStatus == 6) {
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

//    private void showLogistics(OrderInfoBean orderInfoBean) {
//        /**************物流信息及其列表***************/
////        llLogistics.setVisibility(orderStatus == 3 ? View.VISIBLE : View.GONE);
//////        tvExpressName.setText(orderInfoBean.getExpress().getExpressName());
//////        tvExpressNum.setText(orderInfoBean.getExpress().getExpressNum());
////        tvLogisticsInfo.setText(orderInfoBean.getExpress().getExpressContent());
////        tvLogisticsTime.setText(orderInfoBean.getExpress().getExpressTime());
////        llLogistics.setOnClickListener(this);
//        llLogistics.setVisibility(View.GONE);
//        if (TextUtils.isEmpty(orderInfoBean.getExpress().getExpressNum())) {
//            llLogistics.setVisibility(View.GONE);
//        } else {
//            llLogistics.setVisibility(View.VISIBLE);
////            tvLogisticsInfo.setText(TextUtils.isEmpty(orderInfoBean.getExpress().getExpressContent())?getString(R.string.no_logistics_info):orderInfoBean.getExpress().getExpressContent());
////            tvLogisticsTime.setText(TextUtils.isEmpty(orderInfoBean.getExpress().getExpressTime())?"":orderInfoBean.getExpress().getExpressTime());
//        }
//        llLogistics.setOnClickListener(this);
////        switch (orderStatus) {
////            case 3:
////                if (TextUtils.isEmpty(orderInfoBean.getExpress().getExpressNum())) {
////                    llLogistics.setVisibility(View.GONE);
////                } else {
////                    llLogistics.setVisibility(View.VISIBLE);
////                    tvLogisticsInfo.setText(orderInfoBean.getExpress().getExpressContent());
////                    tvLogisticsTime.setText(orderInfoBean.getExpress().getExpressTime());
////                }
////                llLogistics.setOnClickListener(this);
////                break;
////            case 4:
////            case 5:
////                if (TextUtils.isEmpty(orderInfoBean.getExpress().getExpressNum())) {
////                    llLogistics.setVisibility(View.GONE);
////                } else {
////                    llLogistics.setVisibility(View.VISIBLE);
////                    tvLogisticsInfo.setText(getString(R.string.finish_express_tips));
////                    tvLogisticsTime.setVisibility(View.GONE);
////                }
////                llLogistics.setOnClickListener(this);
////                break;
////        }
//    }

    private void showTimeAndPrice(OrderInfoBean orderInfoBean) {
        /**************时间、价格及优惠相关信息***************/
//        tvGoodsPrice.setText(orderInfoBean.getTotalPrice());
        tvRealPrice.setText(orderInfoBean.getTotalPrice());
        if (orderStatus == 1) {
            tvRealPriceName.setText(getString(R.string.total_product_num, totalNum + ""));
            tvPayTime.setVisibility(View.GONE);
            tvPayMethod.setVisibility(View.GONE);
        } else {
            tvRealPriceName.setText(getString(R.string.total_product_num, totalNum + ""));
            tvPayTime.setText(getString(R.string.order_pay_time, orderInfoBean.getOrderPayTime()));
            tvPayMethod.setVisibility(View.VISIBLE);
            tvPayMethod.setText(getString(R.string.pay_method, orderInfoBean.getPayment()));
        }
        tvOrderNo.setText(getString(R.string.order_id, orderInfoBean.getOrderNo()));
        tvOrderTime.setText(getString(R.string.create_order_time, orderInfoBean.getOrderCreateTime()));
    }

    private void showProductList(OrderInfoBean orderInfoBean) {
        /**************店铺名及商品列表***************/
        tvStoreName.setText(orderInfoBean.getSuppliers());
        List<ProductBean> productList = orderInfoBean.getProductList();
        viewGoodList.removeAllViews();
        for (int i = 0; i < productList.size(); i++) {
            ProductBean productBean = productList.get(i);
            View goodItem = LayoutInflater.from(this).inflate(R.layout.view_mall_order_good_item, viewGoodList, false);
            ImageView iv_good_img = goodItem.findViewById(R.id.iv_good_img);
            TextView tv_good_desc = goodItem.findViewById(R.id.tv_good_desc);
            TextView tv_product_num = goodItem.findViewById(R.id.tv_product_num);
            TextView tv_good_sku = goodItem.findViewById(R.id.tv_good_sku);
            TextView tv_good_price = goodItem.findViewById(R.id.tv_good_price);
            ImageLoaderUtils.imageLoad(this, productBean.getImageUrl(), iv_good_img);
            tv_good_desc.setText(productBean.getProductName());
            tv_product_num.setText("×" + productBean.getProductNum());
            tv_good_sku.setText(/*"数量:" + productBean.getProductNum() + "  " + */productBean.getSpe());
            tv_good_price.setText(productBean.getPrice());
            viewGoodList.addView(goodItem);
            totalNum += Integer.parseInt(productBean.getProductNum());
        }
    }

    private void showHostInfo(OrderInfoBean orderInfoBean) {
//        /**************收货人信息***************/
//        if (TextUtils.isEmpty(orderInfoBean.getAddress().getProvince())) {
//            llAddress.setVisibility(View.GONE);
//        } else {
//            llAddress.setVisibility(View.VISIBLE);
////            tvHostName.setText(orderInfoBean.getAddress().getName() + "  " + orderInfoBean.getAddress().getMobile());
//        tvHostTel.setText(orderInfoBean.getMobile());
//            StringBuilder addrSb = new StringBuilder();
//            addrSb.append(orderInfoBean.getAddress().getProvince())
//                    .append(orderInfoBean.getAddress().getCity())
//                    .append(orderInfoBean.getAddress().getArea())
//                    .append(orderInfoBean.getAddress().getAddressDetail());
//            tvHostAddress.setText(addrSb);
//        }

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
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.pendingpayment);
//                ivPayStatus.setImageResource(R.mipmap.dzficon);
                break;
            case 2:
                statusStr = getString(R.string.wait_send);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.goodstobereceived);
//                ivPayStatus.setImageResource(R.mipmap.cfzicon);
                break;
            case 3:
                statusStr = getString(R.string.wait_for_accept);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.goodstobereceived);
//                ivPayStatus.setImageResource(R.mipmap.cfzicon);
                break;
            case 4:
                statusStr = getString(R.string.wait_for_discuss);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.complete);
//                ivPayStatus.setImageResource(R.mipmap.cfzicon);
                break;
            case 5:
                statusStr = getString(R.string.finished);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.complete);
//                ivPayStatus.setImageResource(R.mipmap.wc_icon);
                break;
            case 6:
                break;
            case 7:
                statusStr = getString(R.string.have_closed);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.complete);
//                ivPayStatus.setImageResource(R.mipmap.wc_icon);
                break;
            case 8:
                statusStr = getString(R.string.after_sale_1);
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus, R.mipmap.complete);
//                ivPayStatus.setImageResource(R.mipmap.wc_icon);
                break;
            case 9:
                statusStr = "可使用";
                tvOrderStatusDesc.setVisibility(View.GONE);
                DisplayUtils.setViewDrawableLeft(tvOrderStatus,R.mipmap.icon_can_use);
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
                ToastUtils.showShortToast(OrderDetailActivity.this, getString(R.string.order_invalid));
                finish();
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showPayPop(PayMsgModel payMsgModel) {
        initPayTypeList();
        payPop = new PayPop(OrderDetailActivity.this, payTypeList, payMsgModel, totalNum, new PayPop.PayTypeSelectListener() {
            @Override
            public void onSelectPayType(int payTypeNum) {
                payType = payTypeNum;
            }

            @Override
            public void onPay(int payTypeNum, double totalMoney, View v) {
                onGoPay(v);
            }
        });
        payPop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        payPop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        payPop.showAtLocation(llBottom, Gravity.BOTTOM, 0, 0);
        payPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });
        backgroundAlpha(0.3f);
    }

    private void onGoPay(View v) {
        if (payType == 3) { //余额支付
            if (!checkPayPwd()) {
                return;
            }
            showPayPasswordPop(tvOperateRight);
        } else {//其他支付
            payOrder();
        }
    }

    public void showPayPasswordPop(View v) {
        if (null == pwdPop) {
            pwdPop = new PasswordPopupWin(OrderDetailActivity.this);
        }
        pwdPop.setPasswordPopListener(new PasswordPopupWin.PasswordPopListener() {
            @Override
            public void onPasswordVerify(String pwd) {
                //此处为密码输到第六位的监听，替换成付款逻辑即可
                verifyPayPwd(pwd);
            }
        });
        pwdPop.showAtBottom(v.getRootView(), v);
    }

    private void payOrder() {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("payment", payType);
        JSONArray ja = new JSONArray();
        for (int i = 0; i < payMsgModel.orderList.size(); i++) {
            ja.put(new JSONObject().put("orderNo", payMsgModel.orderList.get(i)));
        }
        jo.put("orderNoList", ja);
//        jo.put("orderNo", orderId);
        ApiManager.get().getData(Const.ORDER_PAY, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {

                if (payType == 3) {
//                    ToastUtils.showShortToast(OrderDetailActivity.this, responseModel.resultCodeDetail);
                    ApiManager.get().getMyInfo(null);
                    payResult(1, "");
//                    finish();
                } else if (payType == 1 || payType == 2) {
                    //下一步进入支付宝
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        JSONObject jo = (JSONObject) responseModel.data;
                        rebates = jo.optString("orderString");
                        if (payType == 1) {
                            //下一步进入支付宝
                            alipay(jo.optString("orderString"));
                        } else {
                            //微信支付
                            WechatPayManager.get().pay(OrderDetailActivity.this, jo, OrderDetailActivity.this);
                        }

                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(OrderDetailActivity.this, msg);
                payResult(0, "");
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.paying_tips)).create());
    }

    private void payResult(int resultType, String rebates) {
        payPop.dismiss();
        PaySuccessDialog.Builder builder = new PaySuccessDialog.Builder(OrderDetailActivity.this);
        builder.setResult(resultType, rebates)
                .setDialogInterface(new PaySuccessDialog.DialogInterface() {
                    @Override
                    public void walkAround(int resultType, PaySuccessDialog dialog) {
                        if (resultType == 1) {
                            dialog.dismiss();
                            finish();
                            ActivityUtils.finishTopActivity(MainActivity.class);
                            EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
                        } else if (resultType == 0) {
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }

    private void verifyPayPwd(String payPwd) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("payPwd", payPwd);
        ApiManager.get().getData(Const.VERIFY_PAY_PWD, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                payOrder();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(OrderDetailActivity.this, msg);
                if (null != pwdPop) {
                    pwdPop.clearPwd();
                }
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.checking_tips)).create());
    }

    private boolean checkPayPwd() {
        if (LoginHandler.get().getMyInfo().optInt("hasPayPwd") == 0) {
            CommonDialog.Builder builder = new CommonDialog.Builder(this);
            builder.setMessage(this.getString(R.string.set_pwd_tips))
                    .setLeftButton(this.getString(R.string.cancel_pay))
                    .setRightButton(this.getString(R.string.confirm_pay))
                    .setDialogInterface(new CommonDialog.DialogInterface() {

                        @Override
                        public void leftButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void rightButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(OrderDetailActivity.this, ModPasswordActivity.class);
                            intent.putExtra(Const.PWD_TYPE_KEY, 2);
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();
            return false;
        }
        return true;
    }

    private void initPayTypeList() {
        payTypeList.clear();
        PayTypeModel alipayPayModel = new PayTypeModel();
        alipayPayModel.iconId = R.mipmap.alipay;
        alipayPayModel.payType = 1;
        alipayPayModel.payTypeDetails = getString(R.string.ali_pay);
        alipayPayModel.explain = getString(R.string.ali_explain);
        alipayPayModel.status = 0;

        PayTypeModel wechatPayPayModel = new PayTypeModel();
        wechatPayPayModel.iconId = R.mipmap.wechat_pay;
        wechatPayPayModel.payType = 2;
        wechatPayPayModel.payTypeDetails = getString(R.string.wechat);
        wechatPayPayModel.explain = getString(R.string.wechat_explain);
        wechatPayPayModel.status = 0;

        PayTypeModel quotaPayPayModel = new PayTypeModel();
        quotaPayPayModel.iconId = R.mipmap.yue;
        quotaPayPayModel.payType = 3;
        quotaPayPayModel.payTypeDetails = getString(R.string.quota_pay);
        quotaPayPayModel.explain = getString(R.string.quota_explain);
        quotaPayPayModel.status = 0;

        alipayPayModel.status = 1;
        payTypeList.add(alipayPayModel);
        payTypeList.add(wechatPayPayModel);
        payTypeList.add(quotaPayPayModel);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        this.getWindow().setAttributes(lp);
    }

    @Override
    public void onWechatPayRequestError() {

    }

    @Override
    public void onWechatPayRequestSuccess() {

    }

    @Override
    public void onWechatPayError(int code, String msg) {
        payResult(0, "");
    }

    @Override
    public void onWechatPaySuccess() {
        payResult(1, rebates);
    }

    private void alipay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderDetailActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                alipayHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private class AlipayHandler extends Handler {

        private final WeakReference<OrderDetailActivity> mMallPayActivity;

        private AlipayHandler(OrderDetailActivity t) {
            mMallPayActivity = new WeakReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OrderDetailActivity t = mMallPayActivity.get();
            if (null != t) {
                if (msg.what == SDK_PAY_FLAG) {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast(t, t.getString(R.string.pay_succeed));
                        payResult(1, rebates);
                        t.finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast(t, t.getString(R.string.pay_failed));
                        payResult(0, "");
                    }
                    ApiManager.get().getMyInfo(null);
                }
            }
        }
    }
}
