package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
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
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.ToastUtils;
import com.webank.mbank.ocr.net.EXIDCardResult;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.iv_id_image_font)
    ImageView ivIdImageFont;
    @BindView(R.id.iv_id_image_back)
    ImageView ivIdImageBack;
    @BindView(R.id.et_real_name)
    EditText etRealName;
    @BindView(R.id.et_id_number)
    EditText etIdNumber;
    @BindView(R.id.tv_confirm_auth)
    TextView tvConfirmAuth;
    private EXIDCardResult exidCardResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        exidCardResult = (EXIDCardResult) intent.getParcelableExtra(Const.ID_CARD_INFO);
        initTitle();
        initView();
        initEvent();
    }

    private void initEvent() {
        tvConfirmAuth.setOnClickListener(this);
    }

    private void initView() {
        if (exidCardResult != null) {
            Bitmap bitmap1 = BitmapUtils.getLoacalBitmap(exidCardResult.frontFullImageSrc);
            Bitmap bitmap2 = BitmapUtils.getLoacalBitmap(exidCardResult.backFullImageSrc);
            ivIdImageFont.setImageBitmap(bitmap1);
            ivIdImageBack.setImageBitmap(bitmap2);
//            ImageLoaderUtils.imageLoad(PersonalInfoActivity.this,exidCardResult.frontFullImageSrc,ivIdImageFont);
//            ImageLoaderUtils.imageLoad(PersonalInfoActivity.this,exidCardResult.backFullImageSrc,ivIdImageBack);
            etRealName.setText(exidCardResult.name);
            etIdNumber.setText(exidCardResult.cardNum);
        }
    }

    private void initTitle() {
        setActionBar(getString(R.string.certification_title), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm_auth:
                commit();
                break;
        }
    }

    private void commit() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("name", etRealName.getText().toString().trim());
        jsonObject.put("idNumber", etIdNumber.getText().toString().trim());
        ApiManager.get().getData(Const.NAME_CERTIFICATION, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(PersonalInfoActivity.this, responseModel.resultCodeDetail);
                ApiManager.get().getMyInfo(new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
                            LoginHandler.get().saveMyInfo(jo);
                            finish();
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                    }
                });
            }

            @Override
            public void onBusinessError(int code, String msg) {

            }
        },new LoadingDialog.Builder(PersonalInfoActivity.this).setMessage(getString(R.string.submitting_tips)).create());
    }
}
