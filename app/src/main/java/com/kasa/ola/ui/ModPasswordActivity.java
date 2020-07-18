package com.kasa.ola.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModPasswordActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.textView_phoneNumber)
    TextView textViewPhoneNumber;
    @BindView(R.id.img_username)
    ImageView imgPhone;
    @BindView(R.id.edittext_username)
    EditText edittextPhone;
    @BindView(R.id.delete_username)
    ImageView deletePhone;
    @BindView(R.id.tv_choose_write)
    TextView tvChooseWrite;
    @BindView(R.id.ll_phone_number)
    LinearLayout llPhoneNumber;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.edittext)
    EditText etVerifyCode;
    @BindView(R.id.button_verify_code)
    Button buttonVerifyCode;
    @BindView(R.id.img_password)
    ImageView imgPassword;
    @BindView(R.id.edittext_password)
    EditText edittextPassword;
    @BindView(R.id.delete_password)
    ImageView deletePassword;
    @BindView(R.id.eye)
    ImageView eye;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private CountDownTimer timer;
    private int time = Const.TIME;
    private boolean hasCode = false;
    private int type = 1;       //1.忘记密码 ,修改登录密码 2.修改支付密码
    private int enterType =0;
    private boolean mbShowPassword_0 = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_password);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra(Const.PWD_TYPE_KEY, 1);
        enterType = getIntent().getIntExtra(Const.FORGET_PASSWORD, 1);
        init();
    }

    private void init() {
        if (type == 1) {
            switch (enterType){
                case 0:
                    setActionBar(getString(R.string.forget_Password_title), "");
                    break;
                case 1:
                    setActionBar(getString(R.string.modLoginPassword_title), "");
                    break;
            }
            textViewPhoneNumber.setVisibility(View.GONE);
            llPhoneNumber.setVisibility(View.VISIBLE);
            imgPhone.setImageResource(R.mipmap.phone);
            edittextPhone.setSelectAllOnFocus(true);
            edittextPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
            edittextPhone.setHint(getString(R.string.edittext_phone_number));
            edittextPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            edittextPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
            edittextPassword.setHint(getString(R.string.input_new_password));
        }else {
            setActionBar(getString(R.string.modPayPassword_title), "");
            textViewPhoneNumber.setVisibility(View.VISIBLE);
            llPhoneNumber.setVisibility(View.GONE);
            String mobileStr = LoginHandler.get().getMyInfo().optString("mobile");
            if (!TextUtils.isEmpty(mobileStr) && mobileStr.length() > 7) {
                StringBuilder mobile = new StringBuilder(mobileStr);
                mobile.replace(3, 7, "****");
                textViewPhoneNumber.setText(getString(R.string.phone_number, mobile));
            }
            edittextPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            edittextPassword.setHint(getString(R.string.input_new_pay_password));
        }
        imageView.setImageResource(R.mipmap.verify_code);
        etVerifyCode.setSelectAllOnFocus(true);
        etVerifyCode.setHint(getString(R.string.edittext_verify_code));
        etVerifyCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        imgPassword.setImageResource(R.mipmap.password);
        edittextPassword.setSelectAllOnFocus(true);
        deletePassword.setOnClickListener(this);
        eye.setOnClickListener(this);
        edittextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    deletePassword.setVisibility(View.INVISIBLE);
                    eye.setVisibility(View.INVISIBLE);
                } else {
                    deletePassword.setVisibility(View.VISIBLE);
                    eye.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:

                break;
            case R.id.delete_password://删除密码
                edittextPassword.setText("");
                deletePassword.setVisibility(View.INVISIBLE);
                deletePassword.setVisibility(View.INVISIBLE);
                break;
            case R.id.eye:
                if (mbShowPassword_0) {// 显示密码
                    showPassword(eye, edittextPassword);
                } else {// 隐藏密码
                    hidePassword(eye, edittextPassword);
                }
                mbShowPassword_0 = !mbShowPassword_0;
                break;
        }
    }
    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
    private CountDownTimer setTimer(final int millisInFuture, int countDownInterval) {
        return new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (!isFinishing()) {
                    buttonVerifyCode.setText((time--) + "s");
                }
            }

            @Override
            public void onFinish() {
                hasCode = false;
                time = Const.TIME;
                buttonVerifyCode.setText(getString(R.string.get_verify_code));
            }
        };
    }
    public void onGetVerifyCode(View v) {
        if (hasCode) {
            ToastUtils.showShortToast(this, getString(R.string.not_get_warning));
            return;
        }
        String mobile;
        if (type == 1) {
            mobile = edittextPhone.getText().toString();
            if (mobile.length() != 11) {
                ToastUtils.showShortToast(ModPasswordActivity.this, getString(R.string.write_mobile_warning));
                return;
            }

        } else {
            mobile = LoginHandler.get().getMyInfo().optString("mobile");
        }
        ApiManager.get().getVerifyCode(ModPasswordActivity.this, mobile,  type, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                hasCode = true;
                timer = setTimer(Const.TIME * 1000, 1000);
                timer.start();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(ModPasswordActivity.this, msg);
            }
        });
    }
    public void onConfirm(View v) {
        String pwd = edittextPassword.getText().toString();
        String verify = etVerifyCode.getText().toString();
        String mobile;
        if (type == 1) {
            mobile = edittextPhone.getText().toString();
            if (mobile.length() != 11) {
                ToastUtils.showShortToast(ModPasswordActivity.this, getString(R.string.write_mobile_warning));
                setFocuse(edittextPhone);
                return;
            }
            if(pwd.length()<8){
                ToastUtils.showShortToast(ModPasswordActivity.this, getString(R.string.password_tips));
                return;
            }
        } else {
            mobile = LoginHandler.get().getMyInfo().optString("mobile");
            if(pwd.length()<6 ||pwd.length()>6){
                ToastUtils.showShortToast(ModPasswordActivity.this, getString(R.string.pay_password_tips));
                return;
            }
        }
        if (TextUtils.isEmpty(verify)) {
            ToastUtils.showShortToast(ModPasswordActivity.this, getString(R.string.input_verify_warning));
            setFocuse(etVerifyCode);
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShortToast(ModPasswordActivity.this, getString(R.string.input_new_password));
            setFocuse(edittextPassword);
            return;
        }

        JSONObject jo = new JSONObject();
        jo.put("mobile", mobile);
        jo.put("verifyCode", verify);
        jo.put("password", pwd);
        LoadingDialog modDialog = new LoadingDialog.Builder(this)
                .setMessage(getString(R.string.get_verify_tips)).create();
        ApiManager.get().getData(type == 2 ? Const.SET_PAY_PASSWORD : Const.SET_USER_PASSWORD, jo, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(ModPasswordActivity.this, responseModel.resultCodeDetail);
                if (type == 2) {
                    LoginHandler.get().updateMyInfoCache("hasPayPwd", "1");
                }
                ApiManager.get().getMyInfo(null);
                if (type == 1) {
                    finish();
                } else {
                    edittextPassword.setText("");
                    etVerifyCode.setText("");
                    finish();
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(ModPasswordActivity.this, msg);
            }
        }, modDialog);
    }


    // 显示密码
    private void showPassword(ImageView imgv, EditText editText) {
        imgv.setImageDrawable(getResources().getDrawable(R.mipmap.k_eye));
        editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        editText.setSelection(editText.getText().toString().length());
    }

    // 隐藏密码
    private void hidePassword(ImageView imgv, EditText editText) {
        imgv.setImageDrawable(getResources().getDrawable(R.mipmap.b_eye));
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editText.setSelection(editText.getText().toString().length());
    }
    private void setFocuse(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
    }
}
