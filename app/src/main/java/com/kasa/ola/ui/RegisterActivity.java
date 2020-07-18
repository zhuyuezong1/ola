package com.kasa.ola.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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


public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private EditText[] et_info;
    private ImageView imgv_phoneDelete, imgv_passwordDelete, imgv_eye;
    private static final int INFO_NUM = 4;
    private Button buttonVerifyCode;
    private boolean hasCode = false;
    private int time = Const.TIME;
    private boolean mbisPasswordHide = true;
    private CountDownTimer timer;
    private CheckBox check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        if (et_info == null) {
            et_info = new EditText[INFO_NUM];
        }

        setActionBar(getString(R.string.register_title), "");

        buttonVerifyCode = findViewById(R.id.button_verify_code);

        View view;
        view = findViewById(R.id.phone_number);
        ImageView imgv = view.findViewById(R.id.img_username);
        imgv.setImageResource(R.mipmap.phone);
        et_info[0] = view.findViewById(R.id.edittext_username);
        et_info[0].setHint(getString(R.string.edittext_phone_number));
        et_info[0].setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        et_info[0].setInputType(InputType.TYPE_CLASS_NUMBER);
        imgv_phoneDelete = view.findViewById(R.id.delete_username);
        imgv_phoneDelete.setOnClickListener(this);
        et_info[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || s.toString().equals("")) {
                    imgv_phoneDelete.setVisibility(View.INVISIBLE);
                } else {
                    imgv_phoneDelete.setVisibility(View.VISIBLE);
                }
            }
        });


        view = findViewById(R.id.password);
        imgv = view.findViewById(R.id.img_password);
        imgv.setImageResource(R.mipmap.password);
        et_info[1] = view.findViewById(R.id.edittext_password);
        et_info[1].setHint(getString(R.string.edittext_password));
        imgv_passwordDelete = view.findViewById(R.id.delete_password);
        imgv_eye = view.findViewById(R.id.eye);
        imgv_passwordDelete.setOnClickListener(this);
        imgv_eye.setOnClickListener(this);
        et_info[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || s.toString().equals("")) {
                    imgv_passwordDelete.setVisibility(View.INVISIBLE);
                } else {
                    imgv_passwordDelete.setVisibility(View.VISIBLE);
                }
            }
        });


        view = findViewById(R.id.verify_code);
        imgv = view.findViewById(R.id.imageView);
        imgv.setImageResource(R.mipmap.verify_code);
        et_info[2] = view.findViewById(R.id.edittext);
        et_info[2].setHint(getString(R.string.edittext_verify_code));
        et_info[2].setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        view = findViewById(R.id.recommend_id);
        imgv = view.findViewById(R.id.img_username);
        imgv.setImageResource(R.mipmap.password);
        et_info[3] = view.findViewById(R.id.edittext_username);
        et_info[3].setHint(getString(R.string.edittext_recommend_id));
        et_info[3].setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        view.findViewById(R.id.tv_choose_write).setVisibility(View.VISIBLE);

        /****************注册协议******************/
        TextView tv_explain = findViewById(R.id.textView_explain);
        SpannableStringBuilder spannable = new SpannableStringBuilder(getString(R.string.register_doc));

        //设置字体颜色
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.COLOR_FF1E90FF)), 13,
                19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置点击监听
        spannable.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                String userProUrl = LoginHandler.get().getBaseUrlBean().getUserProUrl();
                Intent intent = new Intent(RegisterActivity.this, WebActivity.class);
                intent.putExtra(Const.WEB_TITLE, getString(R.string.register_agreement));
                intent.putExtra(Const.INTENT_URL, userProUrl);
                startActivity(intent);
            }

            //去掉下划线，重新updateDrawState并且setUnderlineText(false)
            @Override
            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        }, 13, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_explain.setText(spannable);
        tv_explain.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        tv_explain.setMovementMethod(LinkMovementMethod.getInstance());  // 设置TextView为可点击状态


        check = findViewById(R.id.check);
        check.setChecked(false);
    }

//    public void onScan(View v) {
//        requestPermissions(this, new String[]{Manifest.permission.CAMERA}, new BaseActivity.RequestPermissionCallBack() {
//            @Override
//            public void granted() {
//                Intent intent = new Intent(RegisterActivity.this, CaptureActivity.class);
//                startActivityForResult(intent, Const.SCAN_CODE);
//            }
//
//            @Override
//            public void denied() {
//                ToastUtils.showShortToast(RegisterActivity.this, "获取权限失败，正常功能受到影响");
//            }
//        });
//    }
    public void onGetVerifyCode(View v) {
        if (hasCode) {
            ToastUtils.showShortToast(this, getString(R.string.not_get_warning));
            return;
        }
        String mobile = et_info[0].getText().toString();
        if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            ToastUtils.showShortToast(RegisterActivity.this, getString(R.string.write_mobile_warning));
            return;
        }
        ApiManager.get().getVerifyCode(this, mobile, 0, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                hasCode = true;
                timer = setTimer(Const.TIME * 1000, 1000);
                timer.start();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(RegisterActivity.this, msg);
            }
        });
    }

    private CountDownTimer setTimer(final int millisInFuture, int countDownInterval) {
        return new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {

                buttonVerifyCode.setText((time--) + "s");
            }

            @Override
            public void onFinish() {
                hasCode = false;
                time = Const.TIME;
                buttonVerifyCode.setText(getString(R.string.get_verify_code));
            }
        };
    }

    public void onRegister(View v) {

       if(et_info[0].getText().length()<11){
            ToastUtils.showShortToast(RegisterActivity.this, getString(R.string.phone_tips));
            return;
        }
        if(et_info[1].getText().length()<8){
            ToastUtils.showShortToast(RegisterActivity.this, getString(R.string.password_tips));
            return;
        }
        if(et_info[2].getText().length()<6){
            ToastUtils.showShortToast(RegisterActivity.this, getString(R.string.verify_code_tips));
            return;
        }
        if (!check.isChecked()){
            ToastUtils.showShortToast(RegisterActivity.this, getString(R.string.register_agreement_check));
            return;
        }
        JSONObject jo = new JSONObject();
        jo.put("mobile", et_info[0].getText());
        jo.put("password", et_info[1].getText());
        jo.put("verifyCode", et_info[2].getText());
        jo.put("recommendID", et_info[3].getText());
        final LoadingDialog registerDialog = new LoadingDialog.Builder(RegisterActivity.this)
                .setMessage(getString(R.string.register_tips)).create();
        ApiManager.get().getData(Const.USER_REGISTER, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(RegisterActivity.this, responseModel.resultCodeDetail);
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(RegisterActivity.this, msg);
            }
        }, registerDialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_username:
                et_info[0].setText("");
                imgv_phoneDelete.setVisibility(View.INVISIBLE);
                break;

            case R.id.delete_password:
                et_info[1].setText("");
                imgv_passwordDelete.setVisibility(View.INVISIBLE);
                imgv_eye.setVisibility(View.INVISIBLE);
                break;

            case R.id.eye:
                if (mbisPasswordHide) {
                    showPassword();
                } else {
                    hidePassword();
                }
                mbisPasswordHide = !mbisPasswordHide;
                break;
        }
    }

    // 显示密码
    private void showPassword() {
        imgv_eye.setImageDrawable(getResources().getDrawable(R.mipmap.k_eye));
        et_info[2].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        et_info[2].setSelection(et_info[2].getText().toString().length());
    }

    // 隐藏密码
    private void hidePassword() {
        imgv_eye.setImageDrawable(getResources().getDrawable(R.mipmap.b_eye));
        et_info[2].setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_info[2].setSelection(et_info[2].getText().toString().length());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == Const.SCAN_CODE) {
//                if (data != null) {
//                    String content = data.getStringExtra("barCode");
//                    if (content.contains("http://www.caixiaolv.com?userID=")) {
//                        String[] split = content.split("=");
//                        if (split.length>1){
//                            et_info[4].setText(split[1]);
//                        }
//                    } else {
//                        ToastUtils.showShortToast(this, getString(R.string.scan_right_code_tip));
//                    }
//                }
//            }
//        }
//    }
}
