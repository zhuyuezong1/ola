package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.APKVersionCodeUtils;
import com.kasa.ola.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.setting_title), "");

        View view;
        TextView tv;

        view = findViewById(R.id.include_mod_login_password);
        tv = view.findViewById(R.id.textView);
        tv.setText(getString(R.string.setting_login_password_title));
        view.setOnClickListener(this);

        view = findViewById(R.id.include_mod_pay_password);
        tv = view.findViewById(R.id.textView);
        tv.setText(getString(R.string.setting_pay_password_title));
        view.setOnClickListener(this);

        view = findViewById(R.id.include_bank_card);
        tv = view.findViewById(R.id.textView);
        TextView tv_other = view.findViewById(R.id.textView_other);
        tv.setText(getString(R.string.my_bank_card));
        tv_other.setVisibility(View.VISIBLE);
        if (null != LoginHandler.get().getMyInfo()) {
            tv_other.setText(getString(R.string.card_quantity, LoginHandler.get().getMyInfo().optString("myBankNum")));
        }
        view.setOnClickListener(this);

        view = findViewById(R.id.include_address_manage);
        tv = view.findViewById(R.id.textView);
        tv.setText(getString(R.string.address_manage));
        view.setOnClickListener(this);

        view = findViewById(R.id.include_about);
        tv = view.findViewById(R.id.textView);
        tv.setText(getString(R.string.about_ola));
        TextView tv_version = view.findViewById(R.id.textView_other);
        tv_version.setText(getString(R.string.version_name, APKVersionCodeUtils.getVersionName(this) + ""));
        tv_version.setVisibility(View.VISIBLE);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_mod_login_password:
                Intent intent = new Intent(SettingActivity.this, ModPasswordActivity.class);
                intent.putExtra(Const.PWD_TYPE_KEY, 1);
                intent.putExtra(Const.FORGET_PASSWORD, 1);
                startActivity(intent);
                break;

            case R.id.include_mod_pay_password:
                Intent intent1 = new Intent(SettingActivity.this, ModPasswordActivity.class);
                intent1.putExtra(Const.PWD_TYPE_KEY, 2);
                startActivity(intent1);
                break;

            case R.id.include_bank_card:
                startActivity(new Intent(SettingActivity.this, MineCardActivity.class));
                break;
            case R.id.include_address_manage:
                startActivity(new Intent(SettingActivity.this, AddressManagerActivity.class));
                break;
            case R.id.include_about:
                startActivity(new Intent(SettingActivity.this, OlaActivity.class));
                break;
        }
    }
    public void onExit(View v) {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        builder.setMessage(getString(R.string.exit_tips))
                .setLeftButton(getString(R.string.cancel))
                .setRightButton(getString(R.string.exit))
                .setDialogInterface(new CommonDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                        loginOut();
                    }
                })
                .create()
                .show();
    }
    private void loginOut() {
        ApiManager.get().loginOut(new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                LoginHandler.get().logout();
                EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
                EventBus.getDefault().post(new EventCenter.RefreshMyInfo());
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(SettingActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.login_out_tips)).create());
    }
}
