package com.kasa.ola.ui;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.BankCardInfoBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.popwindow.LoopViewPop;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.iv_right_arrow_name)
    ImageView ivRightArrowName;
    @BindView(R.id.view_bank_name)
    RelativeLayout viewBankName;
    @BindView(R.id.et_branch_bank)
    EditText etBranchBank;
    @BindView(R.id.view_branch_bank)
    RelativeLayout viewBranchBank;
    @BindView(R.id.et_card_num)
    EditText etCardNum;
    @BindView(R.id.view_card_num)
    RelativeLayout viewCardNum;
    @BindView(R.id.tv_card_type)
    TextView tvCardType;
    @BindView(R.id.iv_right_arrow)
    ImageView ivRightArrow;
    @BindView(R.id.view_card_type)
    RelativeLayout viewCardType;
    @BindView(R.id.et_card_host)
    EditText etCardHost;
    @BindView(R.id.view_host_name)
    RelativeLayout viewHostName;
    @BindView(R.id.et_id_num)
    EditText etIdNum;
    @BindView(R.id.view_id_num)
    RelativeLayout viewIdNum;
    @BindView(R.id.et_tel_num)
    EditText etTelNum;
    @BindView(R.id.view_tel_num)
    RelativeLayout viewTelNum;
    @BindView(R.id.btn_set_default)
    CheckBox btnSetDefault;
    @BindView(R.id.btn_binding)
    TextView btnBinding;
    @BindView(R.id.bg_view)
    View bgView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private boolean isDefault = false;
    private String cardType = "-1";
    private ArrayList<BankCardInfoBean> bankList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        initView();
    }
    private void initView() {
        setActionBar(getString(R.string.add_card_title), "");
        viewBankName.setOnClickListener(this);
        viewCardType.setOnClickListener(this);
        btnSetDefault.setOnClickListener(this);
        btnBinding.setOnClickListener(this);
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                getBankList();
            }
        });

        getBankList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_bank_name:
                if (bankList.size() == 0) {
                    ToastUtils.showShortToast(CardActivity.this, "获取银行列表失败");
                } else {
                    showBankListDialog();
                }
                break;
            case R.id.view_card_type:
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
            case R.id.btn_binding:
                bindCard();
                break;
        }
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
                ToastUtils.showShortToast(CardActivity.this, msg);
            }
        }, loadingView);
    }
    private void setFocuse(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
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

    private void bindCard() {
        if (TextUtils.isEmpty(tvBankName.getText())) {
            ToastUtils.showShortToast(CardActivity.this, getString(R.string.input_bank_warning));
            return;
        } else if (TextUtils.isEmpty(etBranchBank.getText())) {
            ToastUtils.showShortToast(CardActivity.this, getString(R.string.input_branch_warning));
            setFocuse(etBranchBank);
            return;
        } else if (TextUtils.isEmpty(etCardNum.getText()) || etCardNum.getText().length() < 16) {
            ToastUtils.showShortToast(CardActivity.this, getString(R.string.input_card_warning));
            setFocuse(etCardNum);
            return;
        } else if (TextUtils.equals(cardType, "-1")) {
            ToastUtils.showShortToast(CardActivity.this, getString(R.string.select_card_type_warning));
            return;
        } else if (TextUtils.isEmpty(etCardHost.getText())) {
            ToastUtils.showShortToast(CardActivity.this, getString(R.string.input_host_name_warning));
            setFocuse(etCardHost);
            return;
        } else if (TextUtils.isEmpty(etIdNum.getText())) {
            ToastUtils.showShortToast(CardActivity.this, getString(R.string.input_id_warning));
            setFocuse(etIdNum);
            return;
        } else if (TextUtils.isEmpty(etTelNum.getText())) {
            ToastUtils.showShortToast(CardActivity.this, getString(R.string.input_phone_warning));
            setFocuse(etTelNum);
            return;
        }

        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("bankName", tvBankName.getText().toString());
        jo.put("bankNo", etCardNum.getText().toString());
        jo.put("name", etCardHost.getText().toString());
        jo.put("identityNo", etIdNum.getText().toString());
        jo.put("bankType", cardType);
        jo.put("mobile", etTelNum.getText().toString());
        jo.put("isDefault", isDefault ? "1" : "0");
        jo.put("branchName", etBranchBank.getText().toString());
        ApiManager.get().getData(Const.ADD_BANK_CARD, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(CardActivity.this, responseModel.resultCodeDetail);
                ApiManager.get().getMyInfo(null);
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(CardActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.binding_tips)).create());
    }
}
