package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.bean.entity.WithdrawBean;
import com.kasa.ola.dialog.CheckRateDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.OrdinaryDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.listener.OnConfirmDialogListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.textView_other)
    TextView textViewOther;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.tv_lai_fu_quota)
    TextView tvLaiFuQuota;
    @BindView(R.id.et_withdraw_num)
    EditText etWithdrawNum;
//    EditText etWithdrawNum;
    @BindView(R.id.tv_all_withdraw)
    TextView tvAllWithdraw;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.btn_withdraw)
    Button btnWithdraw;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.include_bank_card)
    View includBankCard;
    @BindView(R.id.tv_withdraw_rule)
    TextView tvWithdrawRule;
    @BindView(R.id.tv_check_rate)
    TextView tvCheckRate;
    private WithdrawBean withdrawBean;
    private String bankID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.withdraw), "");
        etWithdrawNum.setEnabled(true);
//        etWithdrawNum.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etWithdrawNum.setHint(getString(R.string.withdraw_hint));
        tvCheckRate.setOnClickListener(this);
        etWithdrawNum.setOnClickListener(this);
        textView.setText(getString(R.string.withdraw_bank));
        textViewOther.setText(getString(R.string.choose_bank_card));
        tvWithdrawRule.setText(getString(R.string.withdraw_rule));
        tvAllWithdraw.setOnClickListener(this);
        includBankCard.setOnClickListener(this);
        btnWithdraw.setOnClickListener(this);
//        etWithdrawNum.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Double money = 0.0;
//                double rate = 0.0;
//                if (!TextUtils.isEmpty(etWithdrawNum.getText().toString())) {
//                    money = Double.parseDouble(etWithdrawNum.getText().toString());
//                }
//                if (money<=800){
//                    rate = 0.06;
//                }else if (money>800 && money<=20000){
//                    rate = 0.2;
//                }else if (money>20000 && money<=50000){
//                    rate = 0.3;
//                }else if (money>50000){
//                    rate = 0.4;
//                }
//                String noticeText = "将扣除" + "<font color='#ef3636' size='20'>" + String.format("%.2f", money * rate) + "<font color='#666666' size='20'>" + "费用" /*+ "(" + withdrawBean.getDescribe() + ")"*/;
//                tvNotice.setText(Html.fromHtml(noticeText));
//
//            }
//        });

        loadData();
    }

    private void loadData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.BALANCE_CASH_DETAILS, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    withdrawBean = JsonUtils.jsonToObject(((JSONObject) responseModel.data).toString(), WithdrawBean.class);
                    tvLaiFuQuota.setText(getString(R.string.can_withdraw_quota) + withdrawBean.getMoney());
                    String noticeText = "";
                    noticeText = "将扣除" + "<font color='#ef3636' size='20'>" + "0.00" + "<font color='#666666' size='20'>" + "费用" /*+ "(" + withdrawBean.getDescribe() + ")"*/;
                    tvNotice.setText(Html.fromHtml(noticeText));
                    textViewOther.setText(TextUtils.isEmpty(withdrawBean.getBankName()) ? getString(R.string.choose_bank_card) : withdrawBean.getBankName());
                    bankID = TextUtils.isEmpty(withdrawBean.getBankID()) ? "" : withdrawBean.getBankID();
                }

            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(WithdrawActivity.this, msg);
            }
        }, loadingView);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all_withdraw://全提
                if (Double.parseDouble(withdrawBean.getMoney()) < 100) {
                    ToastUtils.showLongToast(this, getString(R.string.quota_less_100_tip));
                    return;
                }
                final long money = ((long) (Double.parseDouble(withdrawBean.getMoney())) / 100 * 100);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID",LoginHandler.get().getUserId());
                jsonObject.put("money",money+"");
                ApiManager.get().getData(Const.CALCULATE_HANDING_FEE, jsonObject, new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        if (responseModel.data!=null && responseModel.data instanceof JSONObject){
                            JSONObject jo = (JSONObject) responseModel.data;
                            String noticeText = "将扣除" + "<font color='#ef3636' size='20'>" + jo.optString("handingFee") + "<font color='#666666' size='20'>" + "费用";
                            tvNotice.setText(Html.fromHtml(noticeText));
                        }
                        etWithdrawNum.setText(money+"");
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showLongToast(WithdrawActivity.this,msg);
                    }
                },null);
//                etWithdrawNum.setSelection(etWithdrawNum.getText().length());
                break;
            case R.id.btn_withdraw://提现提交按钮
                if (checkData()) {
                    OrdinaryDialog.Builder builder = new OrdinaryDialog.Builder(WithdrawActivity.this);
                    builder.setMessage(getString(R.string.withdraw_tips))
                            .setTitle(getString(R.string.withdraw_tips_title))
                            .setLeftButton(getString(R.string.cancel))
                            .setRightButton(getString(R.string.confirm))
                            .setDialogInterface(new OrdinaryDialog.DialogInterface() {
                                @Override
                                public void disagree(OrdinaryDialog dialog) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void agree(OrdinaryDialog dialog) {
                                    submit();
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
                break;
            case R.id.include_bank_card://选择银行卡
                startActivityForResult(new Intent(this, MineCardActivity.class), Const.GOTO_SELECT_BANK);
                break;
            case R.id.tv_check_rate:
                CheckRateDialog.Builder builder = new CheckRateDialog.Builder(this);
                CommonUtils.backgroundAlpha(0.5f,WithdrawActivity.this);
                builder.setDialogInterface(new CheckRateDialog.DialogInterface() {
                    @Override
                    public void close(CheckRateDialog dialog) {
                        dialog.dismiss();
                        CommonUtils.backgroundAlpha(1f,WithdrawActivity.this);
                    }
                })
                        .create()
                        .show();
                break;
            case R.id.et_withdraw_num:
//                CommonUtils.showEditTextDialog(WithdrawActivity.this, getString(R.string.withdraw_number_title), getString(R.string.please_input_withdraw_number), getString(R.string.cancel), getString(R.string.confirm), new OnConfirmDialogListener() {
//                    @Override
//                    public void confirm(final String text) {
//                        if (!TextUtils.isEmpty(text)) {
//                            final long money = Long.parseLong(text);
//                            JSONObject jsonObject = new JSONObject();
//                            jsonObject.put("userID",LoginHandler.get().getUserId());
//                            jsonObject.put("money",money+"");
//                            ApiManager.get().getData(Const.CALCULATE_HANDING_FEE, jsonObject, new BusinessBackListener() {
//                                @Override
//                                public void onBusinessSuccess(BaseResponseModel responseModel) {
//                                    if (responseModel.data!=null && responseModel.data instanceof JSONObject){
//                                        JSONObject jo = (JSONObject) responseModel.data;
//                                        String noticeText = "将扣除" + "<font color='#ef3636' size='20'>" + jo.optString("handingFee") + "<font color='#666666' size='20'>" + "费用";
//                                        tvNotice.setText(Html.fromHtml(noticeText));
//                                    }
//                                    etWithdrawNum.setText(money+"");
//                                }
//
//                                @Override
//                                public void onBusinessError(int code, String msg) {
//                                    ToastUtils.showLongToast(WithdrawActivity.this,msg);
//                                }
//                            },null);
//                        }
//                    }
//                    @Override
//                    public void cancel() {
//                    }
//                });
                break;
        }
    }

    private boolean checkData() {
        if (Double.parseDouble(withdrawBean.getMoney()) < 100) {
            ToastUtils.showLongToast(this, getString(R.string.quota_less_100_tip));
            return false;
        }
        if (TextUtils.isEmpty(etWithdrawNum.getText().toString().trim())) {
            ToastUtils.showLongToast(this, getString(R.string.please_input_withdraw_num_new));
            return false;
        }
        if (Long.parseLong(etWithdrawNum.getText().toString().trim()) > Double.parseDouble(withdrawBean.getMoney())) {
            ToastUtils.showLongToast(this, getString(R.string.withdraw_over_tip));
            return false;
        }
        if (TextUtils.isEmpty(bankID)) {
            ToastUtils.showLongToast(this, getString(R.string.choose_bank_card));
            return false;
        }
        if (Long.parseLong(etWithdrawNum.getText().toString().trim()) < 100) {
            ToastUtils.showLongToast(this, getString(R.string.withdraw_100_tip));
            return false;
        }
        if (Long.parseLong(etWithdrawNum.getText().toString().trim()) % 100 != 0) {
            ToastUtils.showLongToast(this, getString(R.string.withdraw_100_times_tip));
            return false;
        }
        return true;
    }

    private void submit() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("amount", etWithdrawNum.getText().toString());
        jsonObject.put("bankID", bankID);
        ApiManager.get().getData(Const.BALANCE_CASH, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showLongToast(WithdrawActivity.this, getString(R.string.withdraw_apply_success_tips));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(WithdrawActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.loading_tips)).create());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.GOTO_SELECT_BANK) {
                CardBean cardBean = (CardBean) data.getSerializableExtra(Const.SELECT_BANK);
                textViewOther.setText(cardBean.getBankName());
                bankID = cardBean.getBankID();
            }
        }
    }

}
