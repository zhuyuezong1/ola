package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ImageEditAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.ImagePickerUtils;
import com.kasa.ola.utils.ToastUtils;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishFoundActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_publish)
    TextView tvPublish;
    private boolean isChange = false;
    private SelectImagePop foundSelectImagePop;
    private List<TextBean> bottomList;
    private int selectCount = 0;
    private List<String> images = new ArrayList<>();
    private File cameraFile;
    private ArrayList<String> mImagePaths;
    private List<String> imageUrls = new ArrayList<>();
    private ImageEditAdapter imageEditAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_found);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initView() {
        bottomList = CommonUtils.getBottomList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PublishFoundActivity.this, 4, LinearLayoutManager.VERTICAL,false);
        rvImages.setLayoutManager(gridLayoutManager);
        imageEditAdapter = new ImageEditAdapter(PublishFoundActivity.this, imageUrls);
        imageEditAdapter.setOnItemClickListener(new ImageEditAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (position == CommonUtils.getImageDataSize(imageUrls)) {// 点击“+”号位置添加图片
                    isChange = false;
                }else {
                    selectCount = position;
                    isChange = true;
                }
                foundSelectImagePop = new SelectImagePop(PublishFoundActivity.this, bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:// 从图库选择
                                if (!isChange){
                                    selectImage(Const.FOUND_REQUEST_SELECT_IMAGES_CODE);
                                }else {
                                    selectImage(Const.FOUND_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                }
                                foundSelectImagePop.dismiss();
                                break;
                            case 2:// 拍照
                                captureImage(Const.FOUND_CAMERA_REQUEST_CODE);
                                foundSelectImagePop.dismiss();
                                break;
                            case 3:// 取消
                                foundSelectImagePop.dismiss();
                                break;

                            default:
                                break;
                        }
                    }
                });
                foundSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                foundSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                foundSelectImagePop.showAtLocation(rvImages, Gravity.BOTTOM, 0, 0);
                foundSelectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        CommonUtils.backgroundAlpha(1,PublishFoundActivity.this);
                    }
                });
                CommonUtils.backgroundAlpha(0.3f,PublishFoundActivity.this);
            }

            @Override
            public void deleteItem(int position) {
                images.remove(position);
                imageUrls.remove(position);
                imageEditAdapter.notifyDataSetChanged();
            }
        });
        rvImages.setAdapter(imageEditAdapter);
    }
    /**
     * 从图库中选取图片
     */
    public void selectImage(int type) {
        ImagePickerUtils.selectImageAndCrop(PublishFoundActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, PublishFoundActivity.this);
                uploadImages(type, file);
            }
        });
    }
    public void captureImage(final int type) {
        ImagePickerUtils.captureImageAndCrop(PublishFoundActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, PublishFoundActivity.this);
                uploadImages(type, file);
            }
        });
    }

    private void initEvent() {
        tvCancel.setOnClickListener(this);
        tvPublish.setOnClickListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
        }
    }

    private void uploadImages(final int requestCode, final File file) {
        ApiManager.get().uploadSinglePicture(Const.UPLOAD_IMAGE, new File(BitmapUtils.compressImage(file.getAbsolutePath())), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (!TextUtils.isEmpty(responseModel.data.toString())) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String url = jo.optString("imageUrl");
                    if (!TextUtils.isEmpty(url)) {
                        String imageUrl = Const.IMAGE_URL_PREFIX + url;
                        if (requestCode == Const.FOUND_REQUEST_SELECT_IMAGES_CODE) {
                            imageUrls.add(imageUrl);
                            images.add(url);
                        } else if (requestCode == Const.FOUND_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                            imageUrls.set(selectCount, imageUrl);
                            images.set(selectCount, url);
                        } else if (requestCode == Const.FOUND_CAMERA_REQUEST_CODE) {
                            if (isChange) {
                                imageUrls.set(selectCount, imageUrl);
                                images.set(selectCount, url);
                            } else {
                                imageUrls.add(imageUrl);
                                images.add(url);
                            }
                        }
                        imageEditAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(PublishFoundActivity.this, msg);
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_publish:
                publish();
                break;
        }
    }

    private void publish() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("images",images);
        jsonObject.put("publishContent",etContent.getText().toString());
        jsonObject.put("userID",TextUtils.isEmpty(LoginHandler.get().getUserId())?"":LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.PUBLISH, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showLongToast(PublishFoundActivity.this,getString(R.string.publish_success));
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(PublishFoundActivity.this,msg);
            }
        },new LoadingDialog.Builder(PublishFoundActivity.this).setMessage(getString(R.string.publishing_tips)).create());
    }
}
