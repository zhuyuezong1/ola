package com.kasa.ola.pop;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alipay.sdk.app.PayTask;
import com.kasa.ola.R;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.PayResult;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.dialog.DoubleBtnCommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.manager.WechatPayManager;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.PayPopAdapter;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DateUtil;
import com.kasa.ola.utils.PayUtils;
import com.kasa.ola.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import razerdp.basepopup.BasePopupWindow;


public class PayPopupWindow extends BasePopupWindow implements WechatPayManager.OnWechatPayListener{

    private Activity context;
    private final TextView tv_surplus_time;
    private PayPopAdapter mAdapter;
    private ArrayList<PayTypeModel> payTypeList = new ArrayList<>();
    private int payType = 0;
    private boolean isVacanciesFirst = false;
    private String needPayValue;
    private String balance;
    private String reduce;
    private PasswordPopupWin pwdPop;
    private PayMsgModel payMsgModel;
    private final LinearLayout ll_money_pay;
    private final TextView is_vacancies_first;
    private final TextView textView_payType;
    private final TextView tv_pay_value;
    private PayPopListener payPopListener;
    private final TextView tv_confirm;
    private View view;
    private static final int SDK_PAY_FLAG = 1;
    private final AlipayHandler alipayHandler;
    private String secretKey;

    public PayPopupWindow(Activity context, final PayPopListener payPopListener, OnDismissListener onDismissListener, long totalNum, PayMsgModel payMsgModel, View view) {
        super(context);
        this.context = context;
        this.payMsgModel = payMsgModel;
        this.payPopListener = payPopListener;
        this.view = view;
        alipayHandler = new AlipayHandler(context);
        balance = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("balance")) ? "0.00" : CommonUtils.getTwoZero(LoginHandler.get().getMyInfo().optString("balance"));
        ll_money_pay = findViewById(R.id.ll_money_pay);
        TextView tv_title = findViewById(R.id.tv_title);
        ImageView iv_close = findViewById(R.id.iv_close);
        tv_surplus_time = findViewById(R.id.tv_surplus_time);
        tv_surplus_time.setVisibility(View.GONE);
        ImageView imageView_payType = findViewById(R.id.imageView_payType);
        tv_pay_value = findViewById(R.id.tv_pay_value);
        textView_payType = findViewById(R.id.textView_payType);
        TextView textView_explain = findViewById(R.id.textView_explain);
        TextView tv_product_num = findViewById(R.id.tv_product_num);
        RecyclerView rv_pays = findViewById(R.id.rv_pays);

        imageView_payType.setBackgroundResource(R.mipmap.icon_vacancies_pay);
        textView_payType.setText(context.getString(R.string.quota_pay));
        String noticeText = "共" + "<font color='#ff0000'>￥" + balance + "</font>"+",优先使用余额抵扣";
//        textView_explain.setText(context.getString(R.string.quota_explain_pop,balance));
        textView_explain.setText(Html.fromHtml(noticeText));

        needPayValue = payMsgModel.totalPrice;

        tv_pay_value.setText(context.getString(R.string.price,needPayValue));
        tv_product_num.setText(context.getString(R.string.pay_total_product_num,totalNum+""));
        is_vacancies_first = findViewById(R.id.is_vacancies_first);

        tv_confirm = findViewById(R.id.tv_confirm);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuitDialog(onDismissListener);

            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payType==0){
                    ToastUtils.showLongToast(context,context.getString(R.string.please_choose_pay_type));
                }else {
                    onGoPay(v);
                }
            }
        });
        is_vacancies_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVacanciesFirst) {
                    payType = 0;
                    needPayValue = payMsgModel.totalPrice;
                    tv_pay_value.setText(context.getString(R.string.price,needPayValue));
                    textView_payType.setText(context.getString(R.string.quota_pay));
//                    is_vacancies_first.setChecked(false);
                    ll_money_pay.setVisibility(View.VISIBLE);
                    is_vacancies_first.setBackgroundResource(R.mipmap.toggle_close);
                } else {
                    payType = 3;
                    if (!PayUtils.checkPayPwd(context)){
                        return;
                    }
                    showPayPasswordPop(view);
                }
                isVacanciesFirst = !isVacanciesFirst;
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_pays.setLayoutManager(linearLayoutManager);
        mAdapter = new PayPopAdapter(context, payTypeList);
        mAdapter.setPayTypeListener(new PayPopAdapter.PayTypeListener() {
            @Override
            public void onItemClick(int payTypeNum) {
                payType = payTypeNum;
            }
        });
        rv_pays.setAdapter(mAdapter);
        payTypeList.clear();
        PayTypeModel alipayPayModel = new PayTypeModel();
        alipayPayModel.iconId = R.mipmap.icon_ali_pay;
        alipayPayModel.payType = 1;
        alipayPayModel.payTypeDetails = context.getString(R.string.ali_pay);
        alipayPayModel.explain = context.getString(R.string.ali_explain);
        alipayPayModel.status = 0;

        PayTypeModel wechatPayPayModel = new PayTypeModel();
        wechatPayPayModel.iconId = R.mipmap.icon_wechat_pay;
        wechatPayPayModel.payType = 2;
        wechatPayPayModel.payTypeDetails = context.getString(R.string.wechat);
        wechatPayPayModel.explain = context.getString(R.string.wechat_explain);
        wechatPayPayModel.status = 0;

        alipayPayModel.status = 0;
        payTypeList.add(alipayPayModel);
        payTypeList.add(wechatPayPayModel);
        mAdapter.notifyDataSetChanged();
        setPopupGravity(Gravity.BOTTOM);
    }
    public void showPayPasswordPop(View v) {
        if (null == pwdPop) {
            pwdPop = new PasswordPopupWin(context);
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
    private void onGoPay(View v) {
        if (payType == 3) { //余额支付
            if (isVacanciesFirst){
                payOrder();
            }else {
                if (!PayUtils.checkPayPwd(context)) {
                    return;
                }
                showPayPasswordPop(view);
            }
        } else {//其他支付
            payOrder();
        }
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
        jo.put("isBalancePay", isVacanciesFirst?"1":"0");
        jo.put("secretKey", secretKey);
//        jo.put("orderNo", orderId);
        ApiManager.get().getData(Const.ORDER_PAY, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {

                if (payType == 3) {
                    ApiManager.get().getMyInfo(null);
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        JSONObject jo = (JSONObject) responseModel.data;
                    }
                    payResult(1, payType);
                } else if (payType == 1 || payType == 2) {
                    //下一步进入支付宝
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        JSONObject jo = (JSONObject) responseModel.data;
                        if (payType == 1) {
                            //下一步进入支付宝
                            alipay(jo.optString("orderString"));
                        } else {
                            //微信支付
                            WechatPayManager.get().pay(context, jo, PayPopupWindow.this);
                        }

                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(context, msg);
                payResult(0, payType);
            }
        }, new LoadingDialog.Builder(context).setMessage(context.getString(R.string.paying_tips)).create());
    }

    private void alipay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
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

    private void verifyPayPwd(String payPwd) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("payPwd", payPwd);
        ApiManager.get().getData(Const.VERIFY_PAY_PWD, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if(responseModel.data!=null && responseModel.data instanceof JSONObject){
                    JSONObject jo = (JSONObject) responseModel.data;
                    secretKey = jo.optString("secretKey");
                }
                if (!isVacanciesFirst){
                    onGoPay(view);
                }else {
                    is_vacancies_first.setBackgroundResource(R.mipmap.toggle_open);
                    if (null != pwdPop) {
                        pwdPop.clearPwd();
                        pwdPop.dismiss();
                    }
                    if (Double.parseDouble(balance)<Double.parseDouble(payMsgModel.totalPrice)){
                        needPayValue = CommonUtils.getTwoDecimal(Double.parseDouble(payMsgModel.totalPrice)-Double.parseDouble(balance));
                        reduce = balance;
                        ll_money_pay.setVisibility(View.VISIBLE);
                    }else {
                        needPayValue = "0.00";
                        reduce = payMsgModel.totalPrice;
                        ll_money_pay.setVisibility(View.INVISIBLE);
                        payType = 3;
                    }
                    tv_pay_value.setText(context.getString(R.string.price,needPayValue));
                    textView_payType.setText(context.getString(R.string.quota_pay_pop,reduce));
//                    is_vacancies_first.setChecked(true);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(getContext(), msg);
                if (null != pwdPop) {
                    pwdPop.clearPwd();
                }
            }
        }, new LoadingDialog.Builder(getContext()).setMessage(context.getString(R.string.checking_tips)).create());
    }

    private void showQuitDialog(OnDismissListener onDismissListener) {
        DoubleBtnCommonDialog.Builder builder = new DoubleBtnCommonDialog.Builder(context);
        builder.setTitle(context.getString(R.string.is_cancel_pay_tips))
//                .setMessage(context.getString(R.string.is_cancel_pay_message_start)+"xxxx"+context.getString(R.string.is_cancel_pay_message_end))
                .setLeftButton(context.getString(R.string.continue_pay))
                .setRightButton(context.getString(R.string.confirm_cancel))
                .setDialogInterface(new DoubleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void leftButtonClick(DoubleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(DoubleBtnCommonDialog dialog) {
                        dialog.dismiss();
                        dismiss();
                        if (onDismissListener!=null){
                            onDismissListener.onDismiss();
                        }
                    }
                }).create().show();
//        CommonDialog.Builder builder = new CommonDialog.Builder(context);
//        builder.setMessage(context.getString(R.string.cancel_pay_tips))
//                .setLeftButton(context.getString(R.string.cancel_pay))
//                .setRightButton(context.getString(R.string.confirm_pay))
//                .setDialogInterface(new CommonDialog.DialogInterface() {
//
//                    @Override
//                    public void leftButtonClick(CommonDialog dialog) {
//                        dialog.dismiss();
//                    }
//
//                    @Override
//                    public void rightButtonClick(CommonDialog dialog) {
//                        dialog.dismiss();
//                        dismiss();
//                        if (onDismissListener!=null){
//                            onDismissListener.onDismiss();
//                        }
//                    }
//                })
//                .create()
//                .show();
    }
    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0, 350);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0, 1f, 350);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_slide_from_bottom_pay);
    }
    private CountDownTimer setTimer(final int millisInFuture, int countDownInterval) {
        return new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                String orderTime = context.getString(R.string.pay_left_time, DateUtil.timeParse1(millisUntilFinished));
                tv_surplus_time.setText(orderTime);
            }

            @Override
            public void onFinish() {
                ToastUtils.showShortToast(context, context.getString(R.string.order_invalid));
                dismiss();
            }
        };
    }

    @Override
    public void onWechatPayRequestError() {

    }

    @Override
    public void onWechatPayRequestSuccess() {

    }

    @Override
    public void onWechatPayError(int code, String msg) {
        payResult(0, payType);
    }

    @Override
    public void onWechatPaySuccess() {
        payResult(1, payType);
    }

    public interface PayPopListener{
        void onPayResult(int payTypeNum,String totalMoney);
    }
    private class AlipayHandler extends Handler {

        private final WeakReference<Activity> mMallPayActivity;

        private AlipayHandler(Activity t) {
            mMallPayActivity = new WeakReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity t = mMallPayActivity.get();
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
                        payResult(1, payType);
                        t.finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast(t, t.getString(R.string.pay_failed));
                        payResult(0, payType);
                    }
                    ApiManager.get().getMyInfo(null);
                }
            }
        }
    }
    private void payResult(int type, int payType) {
        if (payPopListener!=null){
            dismiss();
            if (type==1){
                payPopListener.onPayResult(payType,payMsgModel.totalPrice);
            }
        }
    }
}
