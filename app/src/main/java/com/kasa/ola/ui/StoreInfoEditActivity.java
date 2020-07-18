package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.kasa.ola.bean.entity.StoreInfoBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.SingleBtnCommonDialog;
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

public class StoreInfoEditActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_shop_organ_name)
    TextView tvShopOrganName;
    @BindView(R.id.et_shop_organ_phone)
    EditText etShopOrganPhone;
    @BindView(R.id.tv_shop_organ_address)
    TextView tvShopOrganAddress;
    @BindView(R.id.tv_detail_address)
    TextView tvDetailAddress;
    @BindView(R.id.et_business_hours)
    EditText etBusinessHours;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.tv_self_mention_point_title)
    TextView tvSelfMentionPointTitle;
    @BindView(R.id.btn_is_self)
    CheckBox btnIsSelf;
    private List<TextBean> bottomList;
    private boolean isChange = false;
    private int selectCount = 0;
    private SelectImagePop selectImagePop;
    private List<String> images = new ArrayList<>();
    private StoreInfoBean storeInfoBean;
    private List<String> imageUrls = new ArrayList<>();
    private ImageEditAdapter imageEditAdapter;
    private String mobile;
    private String businessHours;
    private boolean isSelf = false;
    private String isTake = "0";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info_edit);
        ButterKnife.bind(this);
        storeInfoBean = (StoreInfoBean) getIntent().getSerializableExtra(Const.STORE_INFO_EDIT);
        initTitle();
        initView();
        initData(storeInfoBean);
        initEvent();
    }

    private void initData(StoreInfoBean storeInfoBean) {
        if (storeInfoBean == null) {
            return;
        }
        tvShopOrganName.setText(storeInfoBean.getShopName());
        etShopOrganPhone.setText(storeInfoBean.getMobile());
        tvShopOrganAddress.setText(storeInfoBean.getProvince() + storeInfoBean.getCity() + storeInfoBean.getArea());
        tvDetailAddress.setText(storeInfoBean.getAddressDetail());
        etBusinessHours.setText(storeInfoBean.getBusinessHours());
        imageUrls.clear();
        images.clear();
        images.addAll(storeInfoBean.getShopImages());
        imageUrls.addAll(storeInfoBean.getShopImageUrls());
        imageEditAdapter.notifyDataSetChanged();
        if (storeInfoBean.getIsTake().equals("0")){
            btnIsSelf.setChecked(false);
            isSelf = false;
            isTake = "0";
        }else if (storeInfoBean.getIsTake().equals("1")){
            btnIsSelf.setChecked(true);
            isSelf = true;
            isTake = "1";
        }else {
            btnIsSelf.setChecked(false);
            isSelf = false;
            isTake = "0";
        }
    }

    private void initEvent() {
        tvSelfMentionPointTitle.setOnClickListener(this);
        btnIsSelf.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(StoreInfoEditActivity.this, 4, LinearLayoutManager.VERTICAL, false);
        rvImages.setLayoutManager(gridLayoutManager);
        bottomList = CommonUtils.getBottomList();
        imageEditAdapter = new ImageEditAdapter(StoreInfoEditActivity.this, imageUrls);
        imageEditAdapter.setOnItemClickListener(new ImageEditAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (position == CommonUtils.getImageDataSize(imageUrls)) {// 点击“+”号位置添加图片
                    isChange = false;
                } else {
                    selectCount = position;
                    isChange = true;
                }
                selectImagePop = new SelectImagePop(StoreInfoEditActivity.this, bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:// 从图库选择
                                if (!isChange) {
                                    selectImage(Const.STORE_REQUEST_SELECT_IMAGES_CODE);
                                } else {
                                    selectImage(Const.STORE_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                }
                                selectImagePop.dismiss();
                                break;
                            case 2:// 拍照
                                captureImage(Const.STORE_CAMERA_REQUEST_CODE);
                                selectImagePop.dismiss();
                                break;
                            case 3:// 取消
                                selectImagePop.dismiss();
                                break;

                            default:
                                break;
                        }
                    }
                });
                selectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                selectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                selectImagePop.showAtLocation(rvImages, Gravity.BOTTOM, 0, 0);
                selectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        CommonUtils.backgroundAlpha(1, StoreInfoEditActivity.this);
                    }
                });
                CommonUtils.backgroundAlpha(0.3f, StoreInfoEditActivity.this);
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
        ImagePickerUtils.selectImageAndCrop(StoreInfoEditActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, StoreInfoEditActivity.this);
                uploadImages(type, file);
            }
        });
    }

    private void uploadImages(int requestCode, File file) {
        ApiManager.get().uploadSinglePicture(Const.UPLOAD_IMAGE, new File(BitmapUtils.compressImage(file.getAbsolutePath())), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (!TextUtils.isEmpty(responseModel.data.toString())) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String url = jo.optString("imageUrl");
                    if (!TextUtils.isEmpty(url)) {
                        String imageUrl = Const.IMAGE_URL_PREFIX + url;
                        if (requestCode == Const.STORE_REQUEST_SELECT_IMAGES_CODE) {
                            imageUrls.add(imageUrl);
                            images.add(url);
                            imageEditAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                            imageUrls.set(selectCount, imageUrl);
                            images.set(selectCount, url);
                            imageEditAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_CAMERA_REQUEST_CODE) {
                            if (isChange) {
                                imageUrls.set(selectCount, imageUrl);
                                images.set(selectCount, url);
                            } else {
                                imageUrls.add(imageUrl);
                                images.add(url);
                            }
                            imageEditAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(StoreInfoEditActivity.this, msg);
            }
        }, null);
    }

    public void captureImage(final int type) {
        ImagePickerUtils.captureImageAndCrop(StoreInfoEditActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, StoreInfoEditActivity.this);
                uploadImages(type, file);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initTitle() {
        setActionBar(getString(R.string.store_info_edit_title), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                if (storeInfoBean != null) {
                    mobile = etShopOrganPhone.getText().toString();
                    businessHours = etBusinessHours.getText().toString();
                    if (checkData()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userID", LoginHandler.get().getUserId());
                        jsonObject.put("suppliersID", storeInfoBean.getSuppliersID());
                        jsonObject.put("shopName", storeInfoBean.getShopName());
                        jsonObject.put("corporate", storeInfoBean.getCorporate());
                        jsonObject.put("mobile", mobile);
                        jsonObject.put("name", storeInfoBean.getName());
                        jsonObject.put("province", storeInfoBean.getProvince());
                        jsonObject.put("city", storeInfoBean.getCity());
                        jsonObject.put("area", storeInfoBean.getArea());
                        jsonObject.put("addressDetail", storeInfoBean.getAddressDetail());
                        jsonObject.put("isTake", isTake);
                        jsonObject.put("license", storeInfoBean.getLicense());
                        jsonObject.put("shopImages", images);
                        jsonObject.put("businessHours", businessHours);
                        jsonObject.put("longitude", storeInfoBean.getLongitude());
                        jsonObject.put("latitude", storeInfoBean.getLatitude());
                        ApiManager.get().getData(Const.SUPPLIERS_CERTIFICATION, jsonObject, new BusinessBackListener() {
                            @Override
                            public void onBusinessSuccess(BaseResponseModel responseModel) {
                                ToastUtils.showLongToast(StoreInfoEditActivity.this,getString(R.string.submit_success));
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onBusinessError(int code, String msg) {
                                ToastUtils.showLongToast(StoreInfoEditActivity.this,msg);
                            }
                        }, new LoadingDialog.Builder(StoreInfoEditActivity.this).setMessage(getString(R.string.submitting_tips)).create());
                    }
                } else {
                    ToastUtils.showLongToast(StoreInfoEditActivity.this, "商家信息不存在");
                }
                break;
            case R.id.tv_self_mention_point_title:
                showSelfDialog();
                break;
            case R.id.btn_is_self:
                if (isSelf) {
                    btnIsSelf.setChecked(false);
                    isTake = "0";
                } else {
                    btnIsSelf.setChecked(true);
                    isTake = "1";
                }
                isSelf = !isSelf;
                break;
        }
    }
    private void showSelfDialog() {
        SingleBtnCommonDialog.Builder builder = new SingleBtnCommonDialog.Builder(StoreInfoEditActivity.this);
        builder.setTitle(getString(R.string.self_mention_point_intro_title))
                .setMessage(getString(R.string.self_mention_point_intro_content))
                .setRightButton(getString(R.string.know))
                .setDialogInterface(new SingleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void rightButtonClick(SingleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
    private boolean checkData() {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showLongToast(StoreInfoEditActivity.this, getString(R.string.input_shop_organ_phone_tip));
            return false;
        }
        if (TextUtils.isEmpty(businessHours)) {
            ToastUtils.showLongToast(StoreInfoEditActivity.this, getString(R.string.input_business_hours_tip));
            return false;
        }
        if (images == null || images.size() < 1) {
            ToastUtils.showLongToast(StoreInfoEditActivity.this, getString(R.string.input_shop_images_tip));
            return false;
        }
        return true;
    }
}
