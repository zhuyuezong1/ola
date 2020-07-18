package com.kasa.ola.pop;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.DoubleBtnCommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.ConfirmOrderActivity;
import com.kasa.ola.ui.OrderDetailActivity;
import com.kasa.ola.ui.PayActivity;
import com.kasa.ola.ui.adapter.PayPopAdapter;
import com.kasa.ola.ui.adapter.PayTypeAdapter;
import com.kasa.ola.ui.listener.OnConfirmListener;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DateUtil;
import com.kasa.ola.utils.PayUtils;
import com.kasa.ola.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;


public class PayPopup extends BasePopupWindow {

    private Activity context;
    private final TextView tv_surplus_time;
    private PayPopAdapter mAdapter;
    private ArrayList<PayTypeModel> payTypeList = new ArrayList<>();
    private int payType = 1;
    private boolean isVacanciesFirst = false;
    private String neeaPayValue;
    private String balance;
    private String reduce;
    private PasswordPopupWin pwdPop;
    private PayMsgModel payMsgModel;
    private final LinearLayout ll_money_pay;
    private final CheckBox is_vacancies_first;
    private final TextView textView_payType;
    private final TextView tv_pay_value;
    private OpenVancanciesFirstListener openVancanciesFirstListener;

    public PayPopup(Activity context, final PayTypeSelectListener payTypeSelectListener, OnDismissListener onDismissListener, long totalNum, PayMsgModel payMsgModel,OpenVancanciesFirstListener openVancanciesFirstListener) {
        super(context);
        this.context = context;
        this.payMsgModel = payMsgModel;
        this.openVancanciesFirstListener = openVancanciesFirstListener;
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
        textView_explain.setText(context.getString(R.string.quota_explain_pop,balance));

        neeaPayValue = payMsgModel.totalPrice;

        tv_pay_value.setText(context.getString(R.string.price,neeaPayValue));
        tv_product_num.setText(context.getString(R.string.pay_total_product_num,totalNum+""));
        is_vacancies_first = findViewById(R.id.is_vacancies_first);

        TextView tv_confirm = findViewById(R.id.tv_confirm);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuitDialog(onDismissListener);

            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payTypeSelectListener!=null){
                    payTypeSelectListener.onPay(payType,0.0,tv_confirm);
                }
            }
        });
        is_vacancies_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVacanciesFirst) {
                    neeaPayValue = payMsgModel.totalPrice;
                    tv_pay_value.setText(context.getString(R.string.price,neeaPayValue));
                    textView_payType.setText(context.getString(R.string.quota_pay));
                    is_vacancies_first.setChecked(false);
                    ll_money_pay.setVisibility(View.VISIBLE);
                    if (openVancanciesFirstListener!=null){
                        openVancanciesFirstListener.openVancanciesFirst(PayPopup.this,false);
                    }
                } else {
                    if (openVancanciesFirstListener!=null){
                        openVancanciesFirstListener.openVancanciesFirst(PayPopup.this,true);
                    }
//                    if (!PayUtils.checkPayPwd(context)){
//                        return;
//                    }
//                    showPayPasswordPop(is_vacancies_first);
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
                if (payTypeSelectListener!=null){
                    payTypeSelectListener.onSelectPayType(payTypeNum);
                }
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

    public void setOpenFirstViewChange(){
        if (Double.parseDouble(balance)<Double.parseDouble(payMsgModel.totalPrice)){
            neeaPayValue = (Double.parseDouble(payMsgModel.totalPrice)-Double.parseDouble(balance))+"";
            reduce = balance;
            ll_money_pay.setVisibility(View.VISIBLE);
        }else {
            neeaPayValue = "0.00";
            reduce = payMsgModel.totalPrice;
            ll_money_pay.setVisibility(View.INVISIBLE);
            payType = 3;
        }
        tv_pay_value.setText(context.getString(R.string.price,neeaPayValue));
        textView_payType.setText(context.getString(R.string.quota_pay_pop,reduce));
        is_vacancies_first.setChecked(true);
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
    public interface PayTypeSelectListener{
        void onSelectPayType(int payTypeNum);
        void onPay(int payTypeNum,double totalMoney,View v);
    }
    public interface OpenVancanciesFirstListener{
        void openVancanciesFirst(PayPopup payPopup,boolean isOpen);
    }
}
