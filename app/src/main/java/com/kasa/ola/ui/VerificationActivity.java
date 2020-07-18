package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CheckOrderInfoBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.VerificationResultDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.ToastUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerificationActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_customer_info)
    TextView tvCustomerInfo;
    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @BindView(R.id.tv_sale_value)
    TextView tvSaleValue;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_effective_time)
    TextView tvEffectiveTime;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    private CheckOrderInfoBean checkOrderInfoBean;
    private String orderNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        checkOrderInfoBean = (CheckOrderInfoBean) intent.getSerializableExtra(Const.CHECK_ORDER_INFO);
        orderNo = intent.getStringExtra(Const.CHECK_ORDER_NO);
        initTitle();
        initView();
        initEvent();

    }

    private void initView() {
        tvCustomerInfo.setText(checkOrderInfoBean.getName());
        tvProductName.setText(checkOrderInfoBean.getProductName());
        tvSaleValue.setText(getString(R.string.commission_value,checkOrderInfoBean.getPrice()));
    }

    private void initEvent() {
        btnCommit.setOnClickListener(this);
    }


    private void initTitle() {
        tvTitle.setText(R.string.verification_fail_title);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                showCancelVerificationDialog();
                break;
            case R.id.btn_commit:
                check(orderNo);
                break;
        }
    }

    private void check(String orderNo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("orderNo", orderNo);
        ApiManager.get().getData(Const.VERIFICATION_ORDER, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                showVerificationResultDialog();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                showVerificaitonFailDialog();
            }
        },new LoadingDialog.Builder(VerificationActivity.this).setMessage(getString(R.string.verification_tips)).create());
    }

    private void showVerificaitonFailDialog(){
        VerificationResultDialog.Builder builder = new VerificationResultDialog.Builder(this);
        builder.setMessage(getString(R.string.verification_fail))
                .setMessageDesc(getString(R.string.store_order_verification_fail))
                .setStatusImage(R.mipmap.icon_store_order_error)
                .setRightButton(getString(R.string.verification_again))
                .setDialogInterface(new VerificationResultDialog.DialogInterface() {
                    @Override
                    public void leftButtonClick(VerificationResultDialog dialog) {
                    }

                    @Override
                    public void rightButtonClick(VerificationResultDialog dialog) {
                        dialog.dismiss();
                        check(orderNo);
                    }
                })
                .create()
                .show();
    }

    private void showCancelVerificationDialog() {
        VerificationResultDialog.Builder builder = new VerificationResultDialog.Builder(this);
        builder.setMessage(getString(R.string.verification_cancel))
                .setMessageDesc(getString(R.string.verification_cancel_tip))
                .setLeftButton(getString(R.string.verification_continue))
                .setRightButton(getString(R.string.verification_cancel))
                .setDialogInterface(new VerificationResultDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(VerificationResultDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(VerificationResultDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void showVerificationResultDialog() {
        VerificationResultDialog.Builder builder = new VerificationResultDialog.Builder(this);
        builder.setMessage(getString(R.string.verification_success))
                .setMessageDesc(getString(R.string.verification_success_tips))
                .setStatusImage(R.mipmap.verification_success)
                .setLeftButton(getString(R.string.complete))
                .setRightButton(getString(R.string.verification_continue))
                .setDialogInterface(new VerificationResultDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(VerificationResultDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void rightButtonClick(VerificationResultDialog dialog) {
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .create()
                .show();
    }
}
