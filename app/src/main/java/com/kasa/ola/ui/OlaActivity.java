package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.BaseUrlBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.APKVersionCodeUtils;
import com.kasa.ola.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OlaActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    @BindView(R.id.view_version_update)
    RelativeLayout viewVersionUpdate;
    @BindView(R.id.view_register)
    RelativeLayout viewRegister;
    @BindView(R.id.view_privacy_ensure)
    RelativeLayout viewPrivacyEnsure;
    private BaseUrlBean baseUrlBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        baseUrlBean = LoginHandler.get().getBaseUrlBean();
        setActionBar(getString(R.string.about_ola), "");
        tvVersionName.setText(getString(R.string.version_code, APKVersionCodeUtils.getVersionName(this) + ""));
        viewVersionUpdate.setOnClickListener(this);
        viewRegister.setOnClickListener(this);
        viewPrivacyEnsure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(OlaActivity.this, WebActivity.class);
        switch (v.getId()) {
            case R.id.view_version_update:
                if (!pBar.isShowing()) {
                    ApiManager.get().getData(Const.GET_MY_INFO, new JSONObject().put("userID", LoginHandler.get().getUserId()),
                            new BusinessBackListener() {
                                @Override
                                public void onBusinessSuccess(BaseResponseModel responseModel) {
                                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                                        JSONObject jo = (JSONObject) responseModel.data;
                                        LoginHandler.get().saveMyInfo(jo);
                                        if (!checkUpdate(false)) {
                                            ToastUtils.showShortToast(OlaActivity.this, getString(R.string.new_version_tips));
                                        }
                                    }
                                }

                                @Override
                                public void onBusinessError(int code, String msg) {
                                    ToastUtils.showShortToast(OlaActivity.this, msg);
                                }
                            }, new LoadingDialog.Builder(this).setMessage(getString(R.string.checking_version_tips)).create());
                }
                break;
            case R.id.view_register:
                intent.putExtra(Const.WEB_TITLE, getString(R.string.register_agreement));
                intent.putExtra(Const.INTENT_URL, baseUrlBean.getUserProUrl());
                startActivity(intent);
                break;
            case R.id.view_privacy_ensure:
                intent.putExtra(Const.WEB_TITLE, getString(R.string.privacy_ensure_agreement));
                intent.putExtra(Const.INTENT_URL, baseUrlBean.getPrivacyEnsureUrl());
                startActivity(intent);
                break;
        }
    }
}
