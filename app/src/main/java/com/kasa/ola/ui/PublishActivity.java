package com.kasa.ola.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.AppointmentBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.PublishImageAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.GlideLoader;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.lcw.library.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_describe)
    TextView tvDescribe;
    @BindView(R.id.et_publish_content)
    EditText etPublishContent;
    @BindView(R.id.iv_lesson_logo)
    ImageView ivLessonLogo;
    //    private int commentType;//0:商品评论，1:课程评论(没有商品评论，独立页面)
    private List<File> files = new ArrayList<>();
    private boolean isChange = false;
    private SelectImagePop selectImagePop;
    private List<TextBean> bottomList;
    private File cameraFile;
    private ArrayList<String> mImagePaths;
    private int selectCount = 0;
    private PublishImageAdapter publishImageAdapter;
    private List<String> images = new ArrayList<>();
    private AppointmentBean appointmentBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        Intent intent = getIntent();
//        commentType = intent.getIntExtra(Const.COMMENT_TYPE, 0);
        appointmentBean = (AppointmentBean) intent.getSerializableExtra(Const.PUBLISH_BEAN_TAG);
        initTitle();
        initView();
    }

    private void initView() {
        ImageLoaderUtils.imageLoad(PublishActivity.this,appointmentBean.getLessonLogo(),ivLessonLogo);
        tvName.setText(appointmentBean.getLessonName());
        etPublishContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
    }



    private void initTitle() {
        setActionBar(getString(R.string.publish_comments), getString(R.string.publish));
        tvRightText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_text:
//                ToastUtils.showLongToast(PublishActivity.this, "调发布接口");
                String commentContent = etPublishContent.getText().toString();
                if (!TextUtils.isEmpty(commentContent)){
                    publish(commentContent);
                }else {
                    ToastUtils.showLongToast(PublishActivity.this,getString(R.string.comment_content_tips));
                }
                break;
        }
    }

    private void publish(String commentContent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("comment", commentContent);
//        if (commentType==0){
//            jsonObject.put("productID", productID);
//            jsonObject.put("imageArr", images);
//            ApiManager.get().getData(Const.PUBLISH_PRODUCT_COMMENT_TAG, jsonObject, new BusinessBackListener() {
//                @Override
//                public void onBusinessSuccess(BaseResponseModel responseModel) {
//                    ToastUtils.showLongToast(PublishActivity.this,responseModel.resultCodeDetail);
//                    finish();
//                }
//
//                @Override
//                public void onBusinessError(int code, String msg) {
//                    ToastUtils.showLongToast(PublishActivity.this,msg);
//                }
//            },null);
//        }else if (commentType==1){
        jsonObject.put("classID", appointmentBean.getClassID());
        ApiManager.get().getData(Const.PUBLISH_EDUCATION_COMMENT_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showLongToast(PublishActivity.this, responseModel.resultCodeDetail);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(PublishActivity.this, msg);
            }
        }, null);
//        }
    }

}
