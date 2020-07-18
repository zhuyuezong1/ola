package com.kasa.ola.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RealNameActivity extends BaseActivity implements View.OnClickListener {

    private EditText etName, etIdNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realname);
        initView();
    }

    private void initView() {
        View view;
        setActionBar(getString(R.string.certification_title), getString(R.string.skip_title));
        findViewById(R.id.tv_right_text).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

        TextView tvNotice = findViewById(R.id.textView_notice);
        tvNotice.setText(getString(R.string.notice));

        view = findViewById(R.id.include_name);
        TextView tvName = view.findViewById(R.id.textView);
        tvName.setText(getString(R.string.real_name));
        etName = view.findViewById(R.id.edittext);
        etName.setHint(getString(R.string.input_realName));

        view = findViewById(R.id.include_idNum);
        TextView tvPhoneNum = view.findViewById(R.id.textView);
        tvPhoneNum.setText(getString(R.string.ID_number));
        etIdNum = view.findViewById(R.id.edittext);
        etIdNum.setHint(getString(R.string.input_IDNumber));
    }

    public void onSave(View v) {
//        if (TextUtils.isEmpty(etName.getText()) || etName.getText().length()<2||
//                TextUtils.isEmpty(etIdNum.getText()) ||
//                null == frontFile ||
//                null == backFile) {
//            ToastUtils.showShortToast(RealNameActivity.this, getString(R.string.write_msg_warning));
//            return;
//        }
        if (TextUtils.isEmpty(etName.getText())|| etName.getText().length()<2){
            ToastUtils.showShortToast(RealNameActivity.this, getString(R.string.please_input_name));
            return;
        }else if (TextUtils.isEmpty(etIdNum.getText())){
            ToastUtils.showShortToast(RealNameActivity.this, getString(R.string.please_input_id));
            return;
        }/*else if (null == frontFile || null == backFile){
            ToastUtils.showShortToast(RealNameActivity.this, getString(R.string.please_input_picture));
            return;
        }*/
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("name", etName.getText());
        jo.put("idNumber", etIdNum.getText());
        ApiManager.get().getData(Const.NAME_CERTIFICATION, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(RealNameActivity.this, responseModel.resultCodeDetail);
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
        }, new LoadingDialog.Builder(RealNameActivity.this).setMessage(getString(R.string.submitting_tips)).create());
//        requestPermissions(RealNameActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
//            @Override
//            public void granted() {
//
////                List<File> files = new ArrayList<>();
////                files.add(new File(BitmapUtils.compressImage(frontFile.getAbsolutePath())));
////                files.add(new File(BitmapUtils.compressImage(backFile.getAbsolutePath())));
////                List<String> fileKeys = new ArrayList<>();
////                fileKeys.add("positive");
////                fileKeys.add("opposite");
//
//            }
//
//            @Override
//            public void denied() {
//                ToastUtils.showShortToast(RealNameActivity.this, "获取权限失败，正常功能受到影响");
//                finish();
//            }
//
//
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.include_photoFront:
//                Intent frontIntent = new Intent(RealNameActivity.this, ImageSelectActivity.class);
//                startActivityForResult(frontIntent, Const.FRONT_PIC_INTENT_INT);
//                break;
//            case R.id.include_photoBack:
//                Intent backIntent = new Intent(RealNameActivity.this, ImageSelectActivity.class);
//                startActivityForResult(backIntent, Const.BACK_PIC_INTENT_INT);
//                break;
            case R.id.tv_right_text:
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            File file;
//            if (requestCode == Const.FRONT_PIC_INTENT_INT) {
//                file = (File) data.getSerializableExtra(Const.SELECTED_FILE_KEY);
//                frontFile = file;
//                ImageLoaderUtils.imageLoadRoundFile(this, file, ivPhotoFront, 5);
//            } else if (requestCode == Const.BACK_PIC_INTENT_INT) {
//                file = (File) data.getSerializableExtra(Const.SELECTED_FILE_KEY);
//                backFile = file;
//                ImageLoaderUtils.imageLoadRoundFile(this, file, ivPhotoBack, 5);
//            }
        }
    }
}
