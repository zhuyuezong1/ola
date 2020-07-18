package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.entity.CheckOrderInfoBean;
import com.kasa.ola.manager.Const;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanFailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_error_info)
    TextView tvErrorInfo;
    @BindView(R.id.tv_error_desc)
    TextView tvErrorDesc;
    CheckOrderInfoBean checkOrderInfoBean;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_fail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        checkOrderInfoBean = (CheckOrderInfoBean) intent.getSerializableExtra(Const.CHECK_ORDER_INFO);
        initTitle();
        initView();
    }

    private void initView() {
        if (checkOrderInfoBean!=null){
            tvErrorInfo.setText(TextUtils.isEmpty(checkOrderInfoBean.getFailTitle())?"":checkOrderInfoBean.getFailTitle());
            tvErrorDesc.setText(TextUtils.isEmpty(checkOrderInfoBean.getFailMessage())?"":checkOrderInfoBean.getFailMessage());
        }
    }

    private void initTitle() {
        setActionBar(getString(R.string.verification_fail_title), "");
    }
}
