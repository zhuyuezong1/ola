package com.kasa.ola.pop;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.bean.entity.WithdrawBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.listener.OnConfirmListener;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;


public class ExchangePopup extends BasePopupWindow {
    private  List<CardBean> cardBeans;
    private SelectBankPopup selectBankPopup;
    private final TextView tv_vacancies_value;
    private Context context;
    private int type;
    private final TextView tv_bank_name;
    private String bankID;
    private String maxWithdraw;

    public void setCardBeans(List<CardBean> cardBeans) {
        LogUtil.d("sssssssss","刷新支付弹窗银行卡列表");
        this.cardBeans = cardBeans;
        if (selectBankPopup!=null){
            selectBankPopup.setCardList(cardBeans);
        }
    }
    private WithdrawBean withdrawBean;

    public void setWithdrawBean(WithdrawBean withdrawBean) {
        this.withdrawBean = withdrawBean;
        if (type==Const.WITHDRAW_POP){
            if (withdrawBean!=null){
                if (!TextUtils.isEmpty(withdrawBean.getMoney())){
                    Long v = (long) (Double.parseDouble(withdrawBean.getMoney())/100)*100;
                    double ss = v.doubleValue();
                    tv_vacancies_value.setText(context.getString(R.string.commission_value,CommonUtils.getTwoZero(ss+"")));
                }else {
                    tv_vacancies_value.setText(context.getString(R.string.commission_value,"0.00"));
                }
                if (!TextUtils.isEmpty(withdrawBean.getBankName())){
                    tv_bank_name.setText(withdrawBean.getBankName()+"("+withdrawBean.getBankNo()+")");
                    bankID = withdrawBean.getBankID();
                }else {
                    tv_bank_name.setText(context.getString(R.string.add_card));
                    bankID = "";
                }
            }
        }
    }

    public ExchangePopup(Activity context, final OnConfirmListener onConfirmListener, int type, List<CardBean> cardBeans) {
        super(context);
        this.cardBeans = cardBeans;
        this.context = context;
        this.type = type;
        TextView tv_title = findViewById(R.id.tv_title);
        ImageView iv_close = findViewById(R.id.iv_close);
        TextView tv_notice_tips = findViewById(R.id.tv_notice_tips);
        TextView tv_commission_value = findViewById(R.id.tv_commission_value);
        EditText et_value = findViewById(R.id.et_value);
        TextView tv_all = findViewById(R.id.tv_all);
        LinearLayout ll_choose_bank = findViewById(R.id.ll_choose_bank);
        LinearLayout ll_can_withdraw = findViewById(R.id.ll_can_withdraw);
        RelativeLayout rl_select_bank = findViewById(R.id.rl_select_bank);
        tv_bank_name = findViewById(R.id.tv_bank_name);
        TextView tv_confirm = findViewById(R.id.tv_confirm);
        tv_vacancies_value = findViewById(R.id.tv_vacancies_value);
        String vacancies = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("balance")) ? "0.00" : CommonUtils.getTwoZero(LoginHandler.get().getMyInfo().optString("balance"));
        String commission = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("commission")) ? "0.00" : CommonUtils.getTwoZero(LoginHandler.get().getMyInfo().optString("commission"));
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (type==Const.WITHDRAW_POP){
            Long v = (long) (Double.parseDouble(vacancies)/100)*100;
            double ss = v.doubleValue();
            maxWithdraw = CommonUtils.getTwoZero(ss + "");
        }
        et_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)){
                    tv_confirm.setBackgroundResource(R.drawable.shape_corner_d5d5d5);
                    tv_confirm.setEnabled(false);
                }else {
                    tv_confirm.setBackgroundResource(R.drawable.shape_blue_corner_22);
                    tv_confirm.setEnabled(true);
                }
                if (!TextUtils.isEmpty(s)&& !s.toString().substring(0,1).equals(".")) {
                    if (type==Const.COMMISSION_POP){
                        if (Double.parseDouble(s.toString()) > Double.parseDouble(commission)) {
                            et_value.setText(String.format("%d", (long) Double.parseDouble(commission)));
                            et_value.setSelection(et_value.getText().length());
                        }
                    }else if (type==Const.WITHDRAW_POP){
                        if (Double.parseDouble(s.toString()) > Double.parseDouble(maxWithdraw)) {
                            et_value.setText(String.format("%d", (long) Double.parseDouble(maxWithdraw)));
                            et_value.setSelection(et_value.getText().length());
                        }
                    }
                }else {
                    et_value.setText("");
                }
            }
        });
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type==Const.COMMISSION_POP){
                    et_value.setText(commission);
                }else if (type==Const.WITHDRAW_POP){
                    et_value.setText(maxWithdraw);
                }
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmListener!=null){
                    if (type==Const.COMMISSION_POP){
                        onConfirmListener.confirm(et_value.getText().toString(),"");
                    }else if (type==Const.WITHDRAW_POP){
                        onConfirmListener.confirm(et_value.getText().toString(),bankID);
                    }
                }
            }
        });
        if (type==Const.COMMISSION_POP){
            tv_title.setText(context.getString(R.string.commission_exchange));
            tv_confirm.setText(context.getString(R.string.confirm_exchange));
            ll_choose_bank.setVisibility(View.GONE);
            ll_can_withdraw.setVisibility(View.GONE);
            tv_commission_value.setText(context.getString(R.string.commission_value,commission));
            tv_notice_tips.setText(context.getString(R.string.commission_exchange_tips));
        }else if (type==Const.WITHDRAW_POP){
            tv_title.setText(context.getString(R.string.valances_withdraw));
            tv_confirm.setText(context.getString(R.string.confirm_withdraw));
            ll_choose_bank.setVisibility(View.VISIBLE);
            ll_can_withdraw.setVisibility(View.VISIBLE);
            tv_commission_value.setText(context.getString(R.string.commission_value,vacancies));
            tv_notice_tips.setText(context.getString(R.string.withdraw_exchange_tips));
        }

        rl_select_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                selectBankPopup.setCardList(objects);
                selectBankPopup = new SelectBankPopup(context, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        tv_bank_name.setText(cardBeans.get(position).getBankName());
                        bankID = cardBeans.get(position).getBankID();
                    }
                },cardBeans);
                selectBankPopup.showPopupWindow();
            }
        });
//        mEditText = findViewById(R.id.ed_input);
//        findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onConfirmListener!=null){
//                    onConfirmListener.confirm(mEditText.getText().toString());
//                }
//                dismiss();
//            }
//        });
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//              /*  m_etPhoneNumber.setCursorVisible(false);//失去光标
//                m_etSmsCode.setCursorVisible(false);*/
//        findViewById(R.id.ed_input).requestFocus();
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
//        }
//        setAutoShowInputMethod(et_value, true);
        setPopupGravity(Gravity.BOTTOM);
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
        return createPopupById(R.layout.popup_slide_from_bottom_exchange);
    }

//    public void refreshBankList() {
//        if (selectBankPopup!=null){
//            selectBankPopup.refreshBankList();
//        }
//    }
}
