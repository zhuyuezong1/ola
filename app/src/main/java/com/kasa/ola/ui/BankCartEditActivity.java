package com.kasa.ola.ui;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.BankCardInfoBean;
import com.kasa.ola.bean.entity.OcrSignBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.listener.OcrListener;
import com.kasa.ola.ui.popwindow.LoopViewPop;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;
import com.webank.mbank.ocr.WbCloudOcrSDK;
import com.webank.mbank.ocr.net.EXBankCardResult;
import com.webank.mbank.ocr.net.EXIDCardResult;
import com.webank.mbank.ocr.net.ResultOfDriverLicense;
import com.webank.mbank.ocr.net.VehicleLicenseResultOriginal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BankCartEditActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_take_photo)
    TextView tvTakePhoto;
    @BindView(R.id.et_branch_bank_name)
    EditText etBranchBankName;
    @BindView(R.id.et_card_number)
    EditText etCardNumber;
    @BindView(R.id.tv_card_type)
    TextView tvCardType;
    @BindView(R.id.et_card_host)
    EditText etCardHost;
    @BindView(R.id.et_id_number)
    EditText etIdNumber;
    @BindView(R.id.et_tel_number)
    EditText etTelNumber;
    @BindView(R.id.btn_set_default)
    CheckBox btnSetDefault;
    @BindView(R.id.tv_confirm_bind)
    TextView tvConfirmBind;
    @BindView(R.id.bg_view)
    View bgView;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.ll_bank_name)
    LinearLayout llBankName;
    @BindView(R.id.ll_card_type)
    LinearLayout llCardType;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private OcrSignBean ocrSignBean;
    private ProgressDialog progressDlg;
    private String cardType = "-1";
    private boolean isDefault = false;
    private ArrayList<BankCardInfoBean> bankList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_edit);
        ButterKnife.bind(this);
        initTitle();
        initView();
        getOcrSign();
        getBankList();
        initProgress();
        initEvent();
    }

    private void initEvent() {
        tvTakePhoto.setOnClickListener(this);
        llBankName.setOnClickListener(this);
        llCardType.setOnClickListener(this);
        btnSetDefault.setOnClickListener(this);
        tvConfirmBind.setOnClickListener(this);
    }

    private void initView() {

    }

    private void initTitle() {
        setActionBar(getString(R.string.add_card), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_take_photo:
                callOcr();
                //测试
//                setResult(RESULT_OK);
//                finish();
                break;
            case R.id.ll_bank_name:
                if (bankList.size() == 0) {
                    ToastUtils.showShortToast(BankCartEditActivity.this, "获取银行列表失败");
                } else {
                    showBankListDialog();
                }
                break;
            case R.id.ll_card_type:
                showTypeDialog();
                break;
            case R.id.btn_set_default:
                if (isDefault) {
                    btnSetDefault.setChecked(false);
                } else {
                    btnSetDefault.setChecked(true);
                }
                isDefault = !isDefault;
                break;
            case R.id.tv_confirm_bind:
                bindCard();
                break;
        }
    }

    private void bindCard() {
        if (TextUtils.isEmpty(tvBankName.getText())) {
            ToastUtils.showShortToast(BankCartEditActivity.this, getString(R.string.input_bank_warning));
            return;
        } else if (TextUtils.isEmpty(etBranchBankName.getText())) {
            ToastUtils.showShortToast(BankCartEditActivity.this, getString(R.string.input_branch_warning));
            setFocuse(etBranchBankName);
            return;
        } else if (TextUtils.isEmpty(etCardNumber.getText()) || etCardNumber.getText().length() < 16) {
            ToastUtils.showShortToast(BankCartEditActivity.this, getString(R.string.input_card_warning));
            setFocuse(etCardNumber);
            return;
        } else if (TextUtils.equals(cardType, "-1")) {
            ToastUtils.showShortToast(BankCartEditActivity.this, getString(R.string.select_card_type_warning));
            return;
        } else if (TextUtils.isEmpty(etCardHost.getText())) {
            ToastUtils.showShortToast(BankCartEditActivity.this, getString(R.string.input_host_name_warning));
            setFocuse(etCardHost);
            return;
        } else if (TextUtils.isEmpty(etIdNumber.getText())) {
            ToastUtils.showShortToast(BankCartEditActivity.this, getString(R.string.input_id_warning));
            setFocuse(etIdNumber);
            return;
        } else if (TextUtils.isEmpty(etTelNumber.getText())) {
            ToastUtils.showShortToast(BankCartEditActivity.this, getString(R.string.input_phone_warning));
            setFocuse(etTelNumber);
            return;
        }

        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("bankName", tvBankName.getText().toString());
        jo.put("bankNo", etCardNumber.getText().toString());
        jo.put("name", etCardHost.getText().toString());
        jo.put("identityNo", etIdNumber.getText().toString());
        jo.put("bankType", cardType);
        jo.put("mobile", etTelNumber.getText().toString());
        jo.put("isDefault", isDefault ? "1" : "0");
        jo.put("branchName", etBranchBankName.getText().toString());
        ApiManager.get().getData(Const.ADD_BANK_CARD, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(BankCartEditActivity.this, responseModel.resultCodeDetail);
                ApiManager.get().getMyInfo(null);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(BankCartEditActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.binding_tips)).create());
    }

    private void getBankList() {
        ApiManager.get().getData(Const.GET_BANK_LIST, null, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("bankList");
                    if (null != ja && ja.length() > 0) {
                        for (int i = 0; i < ja.length(); i++) {
                            BankCardInfoBean bankCardInfoBean = new BankCardInfoBean();
                            bankCardInfoBean.setId(i + "");
                            bankCardInfoBean.setDesc(ja.optJSONObject(i).optString("bankName"));
                            bankList.add(bankCardInfoBean);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(BankCartEditActivity.this, msg);
            }
        }, loadingView);
    }

    private void callOcr() {
        String orderNo = "ocr_orderNo" + System.currentTimeMillis();
        progressDlg.show();
        if (ocrSignBean != null) {
            CommonUtils.startOcrDemo(BankCartEditActivity.this, ocrSignBean.getSign(), "银行卡识别", orderNo, Const.appId, "1.0.0", ocrSignBean.getNonce(), LoginHandler.get().getUserId(), progressDlg, new OcrListener() {
                @Override
                public void bankOcrResult(EXBankCardResult exBankCardResult) {
                    etCardNumber.setText(exBankCardResult.bankcardNo);
                }

                @Override
                public void idResult(EXIDCardResult exidCardResult) {

                }

                @Override
                public void driverLicenseOcrResult(ResultOfDriverLicense resultOfDriverLicense) {

                }

                @Override
                public void vehicleLicenseOcrResult(VehicleLicenseResultOriginal vehicleLicenseResultOriginal) {

                }
            }, WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeBankSide);
        }
    }
    private void showBankListDialog() {
        LoopViewPop loopViewPop = new LoopViewPop(this, bankList);
        loopViewPop.setTypeListener(new LoopViewPop.TypeListener() {
            @Override
            public void onConfirm(String id, String desc) {
                tvBankName.setText(desc);
            }
        });
        loopViewPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgView.setVisibility(View.GONE);
            }
        });
        loopViewPop.showAtLocation(tvCardType, Gravity.CENTER, 0, 0);
        bgView.setVisibility(View.VISIBLE);
        ObjectAnimator inAnimator = ObjectAnimator.ofFloat(bgView, "alpha", 0f, 1f);
        inAnimator.setDuration(200);
        inAnimator.start();
    }
    private void showTypeDialog() {
        ArrayList<BankCardInfoBean> mList = new ArrayList<>();
        BankCardInfoBean bankCardInfoBean1 = new BankCardInfoBean();
        bankCardInfoBean1.setId("0");
        bankCardInfoBean1.setDesc(getString(R.string.deposit_card));
        BankCardInfoBean bankCardInfoBean2 = new BankCardInfoBean();
        bankCardInfoBean2.setId("1");
        bankCardInfoBean2.setDesc(getString(R.string.credit_card));
        mList.add(bankCardInfoBean1);
        mList.add(bankCardInfoBean2);
        LoopViewPop loopViewPop = new LoopViewPop(this, mList);
        loopViewPop.setTypeListener(new LoopViewPop.TypeListener() {
            @Override
            public void onConfirm(String id, String desc) {
                cardType = id;
                tvCardType.setText(desc);
            }
        });
        loopViewPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgView.setVisibility(View.GONE);
            }
        });
        loopViewPop.showAtLocation(tvCardType, Gravity.CENTER, 0, 0);
        bgView.setVisibility(View.VISIBLE);
        ObjectAnimator inAnimator = ObjectAnimator.ofFloat(bgView, "alpha", 0f, 1f);
        inAnimator.setDuration(200);
        inAnimator.start();
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
                ToastUtils.showLongToast(BankCartEditActivity.this, msg);
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
    private void setFocuse(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
    }
}
