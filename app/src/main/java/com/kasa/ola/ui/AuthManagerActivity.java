package com.kasa.ola.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.OcrSignBean;
import com.kasa.ola.bean.entity.WithdrawBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.listener.OcrListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.RoundImageView;
import com.webank.mbank.ocr.WbCloudOcrSDK;
import com.webank.mbank.ocr.net.EXBankCardResult;
import com.webank.mbank.ocr.net.EXIDCardResult;
import com.webank.mbank.ocr.net.ResultOfDriverLicense;
import com.webank.mbank.ocr.net.VehicleLicenseResultOriginal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthManagerActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_go_personal_info)
    TextView tvGoPersonalInfo;
    @BindView(R.id.rl_personal_info)
    RelativeLayout rlPersonalInfo;
    @BindView(R.id.tv_go_bank_card_info)
    TextView tvGoBankCardInfo;
    @BindView(R.id.rl_bank_info)
    RelativeLayout rlBankInfo;
    @BindView(R.id.iv_head)
    RoundImageView ivHead;
    @BindView(R.id.tv_auth_head_text)
    TextView tvAuthHeadText;
    private ProgressDialog progressDlg;
    private OcrSignBean ocrSignBean;
    private boolean isReal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_manager);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        getOcrSign();
        initProgress();
    }

    private void initView() {
        String headImage = LoginHandler.get().getMyInfo().optString("headImage");
        ImageLoaderUtils.imageLoad(AuthManagerActivity.this, headImage, ivHead, R.mipmap.auth_head_image, false);
        isReal = LoginHandler.get().getMyInfo().optInt("isCertification") == 1;
        int myBankNum = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("myBankNum"))?0:Integer.parseInt(LoginHandler.get().getMyInfo().optString("myBankNum"));
        if (isReal) {
            tvAuthHeadText.setText(getString(R.string.have_auth_tips));
            tvGoPersonalInfo.setText(getString(R.string.have_auth));
            DisplayUtils.setViewDrawableRight(tvGoPersonalInfo,0);
            tvGoPersonalInfo.setTextColor(getResources().getColor(R.color.COLOR_FF999999));
        } else {
            tvAuthHeadText.setText(getString(R.string.please_auth_tips));
            tvGoPersonalInfo.setText(getString(R.string.wait_for_perfect));
            tvGoPersonalInfo.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
        }
        if (myBankNum>0){
            tvGoBankCardInfo.setTextColor(getResources().getColor(R.color.COLOR_FF999999));
            getDefaultBankCard();
        }else {
            tvGoBankCardInfo.setText(getString(R.string.wait_for_perfect));
            tvGoBankCardInfo.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
        }
    }

    private void getDefaultBankCard() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID",LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.BALANCE_CASH_DETAILS, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    WithdrawBean withdrawBean = JsonUtils.jsonToObject(((JSONObject) responseModel.data).toString(), WithdrawBean.class);
                    if (TextUtils.isEmpty(withdrawBean.getBankNo())){
                        tvGoBankCardInfo.setText(withdrawBean.getBankName()+"("+withdrawBean.getBankNo()+")");
                    }else {
                        String bankID = TextUtils.isEmpty(withdrawBean.getBankID()) ? "" : withdrawBean.getBankID();
                        tvGoBankCardInfo.setText(withdrawBean.getBankName()+"("+bankID.substring(bankID.length()-4,bankID.length())+")");
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(AuthManagerActivity.this, msg);
            }
        },null);
    }

    private void initEvent() {
        rlPersonalInfo.setOnClickListener(this);
        rlBankInfo.setOnClickListener(this);
    }

    private void initTitle() {
        setActionBar(getString(R.string.auth_manager), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_personal_info:
                if (isReal){
                    return;
                }
                String orderNo1 = "ocr_orderNo" + System.currentTimeMillis();
                progressDlg.show();
                if (ocrSignBean != null) {
                    CommonUtils.startOcrDemo(AuthManagerActivity.this, ocrSignBean.getSign(), "身份证识别", orderNo1, Const.appId, "1.0.0", ocrSignBean.getNonce(), LoginHandler.get().getUserId(), progressDlg, new OcrListener() {
                        @Override
                        public void bankOcrResult(EXBankCardResult exBankCardResult) {

                        }

                        @Override
                        public void idResult(EXIDCardResult exidCardResult) {
//                            ToastUtils.showLongToast(AuthManagerActivity.this,exidCardResult.cardNum);
                            Intent personalIntent = new Intent(AuthManagerActivity.this, PersonalInfoActivity.class);
                            personalIntent.putExtra(Const.ID_CARD_INFO, exidCardResult);
                            startActivity(personalIntent);
                        }

                        @Override
                        public void driverLicenseOcrResult(ResultOfDriverLicense resultOfDriverLicense) {

                        }

                        @Override
                        public void vehicleLicenseOcrResult(VehicleLicenseResultOriginal vehicleLicenseResultOriginal) {

                        }
                    }, WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeNormal);
                }
                break;
            case R.id.rl_bank_info:
//                Intent personalIntent = new Intent(AuthManagerActivity.this, PersonalInfoActivity.class);
//                startActivity(personalIntent);
//                String orderNo = "ocr_orderNo" + System.currentTimeMillis();
//                progressDlg.show();
//                if (ocrSignBean!=null){
//                    CommonUtils.startOcrDemo(AuthManagerActivity.this,ocrSignBean.getSign(),"银行卡识别",orderNo,Const.appId,"1.0.0",ocrSignBean.getNonce(),LoginHandler.get().getUserId(),progressDlg, new OcrListener() {
//                        @Override
//                        public void bankOcrResult(EXBankCardResult exBankCardResult) {
//                            ToastUtils.showLongToast(AuthManagerActivity.this,exBankCardResult.bankcardNo);
//                        }
//
//                        @Override
//                        public void idResult(EXIDCardResult exidCardResult) {
//
//                        }
//
//                        @Override
//                        public void driverLicenseOcrResult(ResultOfDriverLicense resultOfDriverLicense) {
//
//                        }
//
//                        @Override
//                        public void vehicleLicenseOcrResult(VehicleLicenseResultOriginal vehicleLicenseResultOriginal) {
//
//                        }
//                    });
//                }
                Intent bankCardIntent = new Intent(AuthManagerActivity.this, BankCardListActivity.class);
                startActivity(bankCardIntent);
                break;
        }
    }

    private void getOcrSign() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.GET_OCR_SIGN, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    ocrSignBean = JsonUtils.jsonToObject(jo.toString(), OcrSignBean.class);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(AuthManagerActivity.this, msg);
            }
        }, null);
    }

    private void initProgress() {
        if (progressDlg != null) {
            progressDlg.dismiss();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            progressDlg = new ProgressDialog(this);
        } else {
            progressDlg = new ProgressDialog(this);
            progressDlg.setInverseBackgroundForced(true);
        }
        progressDlg.setMessage("加载中...");
        progressDlg.setIndeterminate(true);
        progressDlg.setCanceledOnTouchOutside(false);
        progressDlg.setCancelable(true);
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setCancelable(false);
    }
}
