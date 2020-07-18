package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RegAndLoginAndModActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_ask_content)
    TextView tvAskContent;
    @BindView(R.id.rb_register)
    RadioButton rbRegister;
    @BindView(R.id.rb_login)
    RadioButton rbLogin;
    @BindView(R.id.rb_mod)
    RadioButton rbMod;
    @BindView(R.id.rg_register_login_mod)
    RadioGroup rgRegisterLoginMod;
    @BindView(R.id.et_register_phone)
    EditText etRegisterPhone;
    @BindView(R.id.et_register_password)
    EditText etRegisterPassword;
    @BindView(R.id.et_register_verify_code)
    EditText etRegisterVerifyCode;
    @BindView(R.id.button_register_verify_code)
    Button buttonRegisterVerifyCode;
    @BindView(R.id.et_register_recommend_id)
    EditText etRegisterRecommendId;
    @BindView(R.id.ll_register)
    LinearLayout llRegister;
    @BindView(R.id.et_login_phone)
    EditText etLoginPhone;
    @BindView(R.id.et_login_password)
    EditText etLoginPassword;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.et_mod_phone)
    EditText etModPhone;
    @BindView(R.id.et_mod_new_password)
    EditText etModNewPassword;
    @BindView(R.id.ll_mod)
    LinearLayout llMod;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.et_mod_verify_code)
    EditText etModVerifyCode;
    @BindView(R.id.button_mod_verify_code)
    Button buttonModVerifyCode;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.view_shadow)
    View viewShadow;
    private int enterType;
    private CountDownTimer timer;
    private int time = Const.TIME;
    private boolean hasCode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login_mod);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(RegAndLoginAndModActivity.this);
        Intent intent = getIntent();
        enterType = intent.getIntExtra(Const.REGISTER_LOGIN_MOD_TYPR, 1);
        initView();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    private void initView() {
        rgRegisterLoginMod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_register:
                        enterType = 0;
                        llRegister.setVisibility(View.VISIBLE);
                        llLogin.setVisibility(View.GONE);
                        llMod.setVisibility(View.GONE);
                        tvAskContent.setText(getString(R.string.register_ask_content));
                        btnSubmit.setText(getString(R.string.register_submit_new));
                        break;
                    case R.id.rb_login:
                        enterType = 1;
                        llRegister.setVisibility(View.GONE);
                        llLogin.setVisibility(View.VISIBLE);
                        llMod.setVisibility(View.GONE);
                        tvAskContent.setText(getString(R.string.login_ask_content));
                        btnSubmit.setText(getString(R.string.login_submit_new));
                        break;
                    case R.id.rb_mod:
                        enterType = 2;
                        llRegister.setVisibility(View.GONE);
                        llLogin.setVisibility(View.GONE);
                        llMod.setVisibility(View.VISIBLE);
                        tvAskContent.setText(getString(R.string.mod_ask_content));
                        btnSubmit.setText(getString(R.string.mod_submit_new));
                        break;
                }
            }
        });
        switch (enterType) {
            case 0:
                rgRegisterLoginMod.check(R.id.rb_register);
                break;
            case 1:
                rgRegisterLoginMod.check(R.id.rb_login);
                break;
            case 2:
                rgRegisterLoginMod.check(R.id.rb_mod);
                break;
        }
        buttonRegisterVerifyCode.setOnClickListener(this);
        buttonModVerifyCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        etRegisterPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        etLoginPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        etModPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        etRegisterPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        etLoginPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        etModNewPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
    }

    private CountDownTimer setTimer(final int millisInFuture, int countDownInterval) {
        return new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (enterType == 0) {
                    buttonRegisterVerifyCode.setText((time--) + "s");
                } else if (enterType == 2) {
                    buttonModVerifyCode.setText((time--) + "s");
                }

            }

            @Override
            public void onFinish() {
                hasCode = false;
                time = Const.TIME;
                if (enterType == 0) {
                    buttonRegisterVerifyCode.setText(getString(R.string.get_verify_code));
                } else if (enterType == 2) {
                    buttonModVerifyCode.setText(getString(R.string.get_verify_code));
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.button_register_verify_code:
                onGetVerifyCode();
                break;
            case R.id.button_mod_verify_code:
                onGetVerifyCode();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    private void submit() {
        String mobile = "";
        String password = "";
        String verifyCode = "";
        String recommendId = "";
        if (enterType == 0) {
            mobile = etRegisterPhone.getText().toString();
            password = etRegisterPassword.getText().toString();
            verifyCode = etRegisterVerifyCode.getText().toString();
            recommendId = etRegisterRecommendId.getText().toString();
            if (mobile.length() < 11) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.phone_tips));
                return;
            }
            if (password.length() < 8) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.password_tips));
                return;
            }
            if (verifyCode.length() < 6) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.verify_code_tips));
                return;
            }
            JSONObject jo = new JSONObject();
            jo.put("mobile", mobile);
            jo.put("password", password);
            jo.put("verifyCode", verifyCode);
            jo.put("recommendID", recommendId);
            final String finalMobile = mobile;
            final String finalPassword = password;
            ApiManager.get().getData(Const.USER_REGISTER, jo, new BusinessBackListener() {
                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    ApiManager.get().Login(RegAndLoginAndModActivity.this, finalMobile, finalPassword,
                            new BusinessBackListener() {
                                @Override
                                public void onBusinessSuccess(BaseResponseModel responseModel) {
                                    if (responseModel.data instanceof JSONObject) {
                                        JSONObject jo = (JSONObject) responseModel.data;
//                            Const.REFRESH_WEB = true;
                                        LoginHandler.get().saveToken(jo.optString("tokenID"));
                                        LoginHandler.get().successLogin(jo.optString("userID"), jo);
                                        EventBus.getDefault().post(new EventCenter.LoginNotice());
//                                CommonUtils.checkCertification(RegAndLoginAndModActivity.this);
                                        finish();
                                    }
                                }

                                @Override
                                public void onBusinessError(int code, String msg) {
                                    ToastUtils.showShortToast(RegAndLoginAndModActivity.this, msg);
                                }
                            });
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    ToastUtils.showShortToast(RegAndLoginAndModActivity.this, msg);
                }
            }, new LoadingDialog.Builder(RegAndLoginAndModActivity.this)
                    .setMessage(getString(R.string.register_tips)).create());
        } else if (enterType == 1) {
            mobile = etLoginPhone.getText().toString();
            password = etLoginPassword.getText().toString();
            if (TextUtils.isEmpty(mobile)) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.write_username_warning));
                setFocuse(etLoginPhone);
                return;
            } else if (TextUtils.isEmpty(password)) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.input_pwd_warning));
                setFocuse(etLoginPassword);
                return;
            }
            if (mobile.length() < 11) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.phone_tips));
                return;
            }
            if (password.length() < 8) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.password_tips));
                return;
            }
            ApiManager.get().Login(this, mobile, password,
                    new BusinessBackListener() {
                        @Override
                        public void onBusinessSuccess(BaseResponseModel responseModel) {
                            if (responseModel.data instanceof JSONObject) {
                                JSONObject jo = (JSONObject) responseModel.data;
//                            Const.REFRESH_WEB = true;
                                LoginHandler.get().saveToken(jo.optString("tokenID"));
                                LoginHandler.get().successLogin(jo.optString("userID"), jo);
                                EventBus.getDefault().post(new EventCenter.LoginNotice());
//                                CommonUtils.checkCertification(RegAndLoginAndModActivity.this);
                                finish();
                            }
                        }

                        @Override
                        public void onBusinessError(int code, String msg) {
                            ToastUtils.showShortToast(RegAndLoginAndModActivity.this, msg);
                        }
                    });
        } else if (enterType == 2) {
            mobile = etModPhone.getText().toString();
            verifyCode = etModVerifyCode.getText().toString();
            password = etModNewPassword.getText().toString();
            if (mobile.length() < 11) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.write_mobile_warning));
                setFocuse(etModPhone);
                return;
            }
            if (verifyCode.length() < 6) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.verify_code_tips));
                return;
            }
            if (password.length() < 8) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.password_tips));
                return;
            }
            JSONObject jo = new JSONObject();
            jo.put("mobile", mobile);
            jo.put("verifyCode", verifyCode);
            jo.put("password", password);
            ApiManager.get().getData(Const.SET_USER_PASSWORD, jo, new BusinessBackListener() {

                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    ToastUtils.showShortToast(RegAndLoginAndModActivity.this, responseModel.resultCodeDetail);
                    ApiManager.get().getMyInfo(null);
                    finish();
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    ToastUtils.showShortToast(RegAndLoginAndModActivity.this, msg);
                }
            }, new LoadingDialog.Builder(this)
                    .setMessage(getString(R.string.get_verify_tips)).create());
        }
    }

    public void onGetVerifyCode() {
        if (hasCode) {
            ToastUtils.showShortToast(this, getString(R.string.not_get_warning));
            return;
        }
        String mobile = "";
        int type = 0;
        if (enterType == 0) {
            mobile = etRegisterPhone.getText().toString();
            type = 0;
        } else if (enterType == 2) {
            mobile = etModPhone.getText().toString();
            type = 1;
        }
        if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            ToastUtils.showShortToast(RegAndLoginAndModActivity.this, getString(R.string.write_mobile_warning));
            return;
        }
        ApiManager.get().getVerifyCode(this, mobile, type, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                hasCode = true;
                timer = setTimer(Const.TIME * 1000, 1000);
                timer.start();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(RegAndLoginAndModActivity.this, msg);
            }
        });
    }

    private void setFocuse(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
    }

}
