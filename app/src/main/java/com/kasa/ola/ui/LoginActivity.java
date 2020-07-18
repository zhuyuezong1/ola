package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.img_username)
    ImageView imgUsername;
    @BindView(R.id.edittext_username)
    EditText edittextUsername;
    @BindView(R.id.delete_username)
    ImageView deleteUsername;
    @BindView(R.id.tv_choose_write)
    TextView tvChooseWrite;
    @BindView(R.id.img_password)
    ImageView imgPassword;
    @BindView(R.id.edittext_password)
    EditText edittextPassword;
    @BindView(R.id.delete_password)
    ImageView deletePassword;
    @BindView(R.id.eye)
    ImageView eye;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.forgetPassword)
    TextView forgetPassword;

    private boolean mbisPasswordHide = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.login_title), "");
        imgUsername.setImageResource(R.mipmap.phone);
        edittextUsername.setSelectAllOnFocus(true);
        edittextUsername.setHint(getString(R.string.edittext_phone_number));
        edittextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || s.toString().equals("")) {
                    deleteUsername.setVisibility(View.INVISIBLE);
                } else {
                    deleteUsername.setVisibility(View.VISIBLE);
                }
            }
        });

        imgPassword.setImageResource(R.mipmap.password);
        edittextPassword.setSelectAllOnFocus(true);
        edittextPassword.setHint(getString(R.string.edittext_login_password));
        edittextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || s.toString().equals("")) {
                    deletePassword.setVisibility(View.INVISIBLE);
                    eye.setVisibility(View.INVISIBLE);
                } else {
                    deletePassword.setVisibility(View.VISIBLE);
                    eye.setVisibility(View.VISIBLE);
                }
            }
        });
        String strPassword = getString(R.string.forget_password);
        SpannableStringBuilder builder = new SpannableStringBuilder(strPassword);

        int nEndLen = strPassword.length();

        // 设置点击监听
        builder.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, ModPasswordActivity.class);
                intent.putExtra(Const.PWD_TYPE_KEY, 1);
                intent.putExtra(Const.FORGET_PASSWORD,0);
                startActivity(intent);
            }

            //去掉下划线，重新updateDrawState并且setUnderlineText(false)
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 0, nEndLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgetPassword.setText(builder);
        forgetPassword.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        forgetPassword.setMovementMethod(LinkMovementMethod.getInstance());  // 设置TextView为可点击状态


        deleteUsername.setOnClickListener(this);
        deletePassword.setOnClickListener(this);
        eye.setOnClickListener(this);
        tvRegister.setOnClickListener(this);



    }

    public void onLogin(View v) {
        String userName = edittextUsername.getText().toString();
        String password = edittextPassword.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShortToast(LoginActivity.this, getString(R.string.write_username_warning));
            setFocuse(edittextUsername);
            return;
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.showShortToast(LoginActivity.this, getString(R.string.input_pwd_warning));
            setFocuse(edittextPassword);
            return;
        }
        ApiManager.get().Login(this, edittextUsername.getText().toString(), edittextPassword.getText().toString(),
                new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        if (responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
//                            Const.REFRESH_WEB = true;
                            LoginHandler.get().saveToken(jo.optString("tokenID"));
                            LoginHandler.get().successLogin(jo.optString("userID"), jo);
                            CommonUtils.checkCertification(LoginActivity.this);
                            finish();
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(LoginActivity.this, msg);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_username:
                edittextUsername.setText("");
                deleteUsername.setVisibility(View.INVISIBLE);
                break;
            case R.id.delete_password:
                edittextPassword.setText("");
                deletePassword.setVisibility(View.INVISIBLE);
                break;
            case R.id.eye:
                if (mbisPasswordHide) {
                    showPassword();
                } else {
                    hidePassword();
                }
                mbisPasswordHide = !mbisPasswordHide;
                break;
            case R.id.tv_register:
                onRegister();
                break;
        }

    }

    private void setFocuse(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
    }

    public void onRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    // 显示密码
    private void showPassword() {
        eye.setImageDrawable(getResources().getDrawable(R.mipmap.k_eye));
        edittextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        edittextPassword.setSelection(edittextPassword.getText().toString().length());
    }

    // 隐藏密码
    private void hidePassword() {
        eye.setImageDrawable(getResources().getDrawable(R.mipmap.b_eye));
        edittextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edittextPassword.setSelection(edittextPassword.getText().toString().length());
    }
}
