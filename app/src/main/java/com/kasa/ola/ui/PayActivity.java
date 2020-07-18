package com.kasa.ola.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alipay.sdk.app.PayTask;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.PayResult;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.manager.WechatPayManager;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.PayTypeAdapter;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayActivity extends BaseActivity implements BaseActivity.EventBusListener, WechatPayManager.OnWechatPayListener {

    @BindView(R.id.textview_title)
    TextView textviewTitle;
    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.money_amount)
    TextView moneyAmount;
    @BindView(R.id.pay_list)
    RecyclerView payList;
    @BindView(R.id.go_pay)
    Button goPay;
//    private String orderId;
    private AlipayHandler alipayHandler = new AlipayHandler(this);
    private static final int SDK_PAY_FLAG = 1;
    private ArrayList<PayTypeModel> payTypeList = new ArrayList<>();
    private PayTypeAdapter mAdapter;
    private int payType = 1;
    private PasswordPopupWin pwdPop;
    private PayMsgModel payMsgModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        payMsgModel = (PayMsgModel) getIntent().getSerializableExtra(Const.PAY_MSG_KEY);
//        orderId = payMsgModel.orderList;
        initView();
        initPayTypeList();
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != alipayHandler) {
            alipayHandler.removeCallbacksAndMessages(null);
        }
    }
    @Override
    public void onBackPressed() {
        showQuitDialog();
    }
    private void initView() {
        textviewTitle.setText(getString(R.string.pay_title));
        layoutBack.setVisibility(View.VISIBLE);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuitDialog();
            }
        });
        String str = getResources().getString(R.string.need_pay);
        String strFormat = String.format(str, Float.parseFloat(payMsgModel.totalPrice));
        String strHtml = Html.fromHtml(strFormat).toString();
        int nStartLen = 4;
        int nEndLen = strHtml.length() - 1;
        SpannableString styledText = new SpannableString(strHtml);
        styledText.setSpan(new TextAppearanceSpan(this, R.style.textview_style),
                nStartLen, nEndLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        moneyAmount.setText(styledText, TextView.BufferType.SPANNABLE);
        payList.setLayoutManager(new LinearLayoutManager(this));
        payList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mAdapter = new PayTypeAdapter(PayActivity.this, payTypeList);
        mAdapter.setPayTypeListener(new PayTypeAdapter.PayTypeListener() {
            @Override
            public void onItemClick(int payTypeNum) {
                payType = payTypeNum;
            }
        });
        payList.setAdapter(mAdapter);
    }
    public void onGoPay(final View v) {
        goPay.setText(R.string.go_pay);
        if (payType == 3) { //余额支付
            if (!checkPayPwd()) {
                return;
            }
            showPayPop(v);
        } else {//其他支付
            payOrder();
        }
    }
    private void showQuitDialog() {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        builder.setMessage(this.getString(R.string.cancel_pay_tips))
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
                        finish();
                    }
                })
                .create()
                .show();
    }
    public void showPayPop(View v) {
        if (null == pwdPop) {
            pwdPop = new PasswordPopupWin(this);
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
                ToastUtils.showShortToast(PayActivity.this, msg);
                if (null != pwdPop) {
                    pwdPop.clearPwd();
                }
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.checking_tips)).create());
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
        ApiManager.get().getData(Const.MALL_ORDER_PAY, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {

                if (payType == 3) {
                    ToastUtils.showShortToast(PayActivity.this, responseModel.resultCodeDetail);
                    ApiManager.get().getMyInfo(null);
                    finish();
                } else if (payType == 1 ||payType == 2 ) {
                    //下一步进入支付宝
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        JSONObject jo = (JSONObject) responseModel.data;
                        if (payType == 1){
                            //下一步进入支付宝
                            alipay(jo.optString("orderString"));
                        }else {
                            //微信支付
                            WechatPayManager.get().pay(PayActivity.this, jo, PayActivity.this);
                        }

                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(PayActivity.this, msg);
                showResult(0);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.paying_tips)).create());
    }

    private void alipay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayActivity.this);
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
                            Intent intent = new Intent(PayActivity.this, ModPasswordActivity.class);
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

    @Override
    public void onWechatPayRequestError() {

    }

    @Override
    public void onWechatPayRequestSuccess() {

    }

    @Override
    public void onWechatPayError(int code, String msg) {
        showResult(0);
    }

    @Override
    public void onWechatPaySuccess() {
        showResult(1);
        finish();
    }
    private void showResult(int result) {
        Intent intent = new Intent(PayActivity.this, PayStatusActivity.class);
        intent.putExtra(Const.PAY_STATUS_KEY, result);
        startActivity(intent);
    }
    private class AlipayHandler extends Handler {

        private final WeakReference<PayActivity> mMallPayActivity;

        private AlipayHandler(PayActivity t) {
            mMallPayActivity = new WeakReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PayActivity t = mMallPayActivity.get();
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
                        showResult(1, "");
//                        Intent intent = new Intent(t, PayStatusActivity.class);
//                        intent.putExtra(Const.PAY_STATUS_KEY, 1);
//                        t.startActivity(intent);
                        t.finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast(t, t.getString(R.string.pay_failed));
//                        Intent intent = new Intent(t, PayStatusActivity.class);
//                        intent.putExtra(Const.PAY_STATUS_KEY, 0);
//                        t.startActivity(intent);
                        showResult(0, "");
                    }
                    ApiManager.get().getMyInfo(null);
                }
            }
        }
    }
    private void showResult(int result, String orderId) {
        Intent intent = new Intent(PayActivity.this, PayStatusActivity.class);
        intent.putExtra(Const.PAY_STATUS_KEY, result);
        intent.putExtra(Const.ORDER_ID_KEY, orderId);
        startActivity(intent);
        if (null != pwdPop) {
            pwdPop.dismiss();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.PwdClear pwdClear) {
        if (null != pwdPop) {
            pwdPop.clearPwd();
        }
    }
}
