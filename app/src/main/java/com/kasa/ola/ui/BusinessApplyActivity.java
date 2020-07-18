package com.kasa.ola.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.ApplySubmitSuccessDialog;
import com.kasa.ola.dialog.OrdinaryDialog;
import com.kasa.ola.dialog.ShopApplyDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ShopImageAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.GlideLoader;
import com.kasa.ola.utils.ToastUtils;
import com.lcw.library.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BusinessApplyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.et_shop_organ_name)
    EditText etShopOrganName;
    @BindView(R.id.et_corporate_representative)
    EditText etCorporateRepresentative;
    @BindView(R.id.et_shop_organ_phone)
    EditText etShopOrganPhone;
    @BindView(R.id.et_current_account_holder_name)
    EditText etCurrentAccountHolderName;
    @BindView(R.id.tv_shop_organ_address)
    TextView tvShopOrganAddress;
    @BindView(R.id.et_detail_address)
    EditText etDetailAddress;
    @BindView(R.id.switch_is_self_mention_point)
    Switch switchIsSelfMentionPoint;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.iv_business_license)
    ImageView ivBusinessLicense;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    private String province;
    private String city;
    private String area;
    private boolean isSelf = false;
    private String shopName;
    private String corporateRepresentative;
    private String phoneNumber;
    private String accountUser;
    private int isTake = 0;
    private String address;
    private String detailAddress;
    private List<TextBean> bottomList;
    private SelectImagePop selectImagePop;
    private SelectImagePop shopSelectImagePop;
    private File cameraFile;
    private File shopCameraFile;
    private ArrayList<String> mImagePaths;
    private ArrayList<String> shopImagePaths;
    private String license;
    private Bitmap smallBitmap;
    private List<File> files = new ArrayList<>();
    private ShopImageAdapter shopImageAdapter;
    private boolean isChange = false;
    private int selectCount = 0;
    private List<String> images = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_apply);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
    }

    private void initTitle() {
        setActionBar(getString(R.string.business_identification), "");
    }

    private void initView() {
        ShopApplyDialog.Builder builder = new ShopApplyDialog.Builder(BusinessApplyActivity.this);
        String message = getString(R.string.business_apply_intro_1) + getString(R.string.business_apply_intro_2) + getString(R.string.business_apply_intro_3);
        builder.setMessage(message)
                .setDialogInterface(new ShopApplyDialog.DialogInterface() {
                    @Override
                    public void close(ShopApplyDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

        etShopOrganPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        bottomList = getBottomList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(BusinessApplyActivity.this, 5, LinearLayoutManager.VERTICAL,false);
        rvImages.setLayoutManager(gridLayoutManager);
        shopImageAdapter = new ShopImageAdapter(BusinessApplyActivity.this, files);
        shopImageAdapter.setOnItemClickListener(new ShopImageAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (position == getDataSize(files)) {// 点击“+”号位置添加图片
                    isChange = false;
                    shopSelectImagePop = new SelectImagePop(BusinessApplyActivity.this, bottomList, new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            switch (position + 1) {
                                case 1:// 从图库选择
                                    selectImage(Const.SHOP_REQUEST_SELECT_IMAGES_CODE);
                                    shopSelectImagePop.dismiss();
                                    break;
                                case 2:// 拍照
                                    captureImage(Const.SHOP_CAMERA_REQUEST_CODE);
                                    shopSelectImagePop.dismiss();
                                    break;
                                case 3:// 取消
                                    shopSelectImagePop.dismiss();
                                    break;

                                default:
                                    break;
                            }
                        }
                    });
                    shopSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                    shopSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    shopSelectImagePop.showAtLocation(rvImages, Gravity.BOTTOM, 0, 0);
                    shopSelectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            backgroundAlpha(1);
                        }
                    });
                    backgroundAlpha(0.3f);
                }else {
                    selectCount = position;
                    isChange = true;
                    shopSelectImagePop = new SelectImagePop(BusinessApplyActivity.this, bottomList, new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            switch (position + 1) {
                                case 1:// 从图库选择
                                    selectImage(Const.SHOP_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                    shopSelectImagePop.dismiss();
                                    break;
                                case 2:// 拍照
                                    captureImage(Const.SHOP_CAMERA_REQUEST_CODE);
                                    shopSelectImagePop.dismiss();
                                    break;
                                case 3:// 取消
                                    shopSelectImagePop.dismiss();
                                    break;

                                default:
                                    break;
                            }
                        }
                    });
                    shopSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                    shopSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    shopSelectImagePop.showAtLocation(rvImages, Gravity.BOTTOM, 0, 0);
                    shopSelectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            backgroundAlpha(1);
                        }
                    });
                    backgroundAlpha(0.3f);
                }
            }

            @Override
            public void deleteItem(int position) {
                images.remove(position);
                files.remove(position);
                shopImageAdapter.notifyDataSetChanged();
            }
        });
        rvImages.setAdapter(shopImageAdapter);
    }

    private void initEvent() {
        tvShopOrganAddress.setOnClickListener(this);
        switchIsSelfMentionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSelf = true;
                    isTake = 1;
                } else {
                    isSelf = false;
                    isTake = 0;
                }
            }
        });
        ivBusinessLicense.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_shop_organ_address:
                Intent addrIntent = new Intent(this, ProvinceListActivity.class);
                addrIntent.putExtra(Const.SELECT_AREA_ENTER, 2);
                startActivityForResult(addrIntent, Const.ACTREQ_PROVINCE);
                break;
            case R.id.iv_business_license:
                showIntroDialog();
                break;
            case R.id.btn_confirm:
                shopName = etShopOrganName.getText().toString().trim();
                corporateRepresentative = etCorporateRepresentative.getText().toString().trim();
                phoneNumber = etShopOrganPhone.getText().toString().trim();
                accountUser = etCurrentAccountHolderName.getText().toString().trim();
                address = tvShopOrganAddress.getText().toString();
                detailAddress = etDetailAddress.getText().toString().trim();

                if (checkData()) {
                    submit();
                }
                break;
        }
    }

    private void showIntroDialog() {
        OrdinaryDialog.Builder builder = new OrdinaryDialog.Builder(BusinessApplyActivity.this);
        builder.setTitle(BusinessApplyActivity.this.getString(R.string.upload_business_license_intro_title))
                .setMessage(BusinessApplyActivity.this.getString(R.string.upload_business_license_intro_content))
                .setLeftButton(getString(R.string.cancel))
                .setRightButton(getString(R.string.continue_text))
                .setDialogInterface(new OrdinaryDialog.DialogInterface() {
                    @Override
                    public void disagree(OrdinaryDialog dialog) {
                        dialog.dismiss();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void agree(OrdinaryDialog dialog) {
                        selectImagePop = new SelectImagePop(BusinessApplyActivity.this, bottomList, new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position + 1) {
                                    case 1:// 从图库选择
                                        selectImage(Const.REQUEST_SELECT_IMAGES_CODE);
                                        selectImagePop.dismiss();
                                        break;
                                    case 2:// 拍照
                                        captureImage(Const.CAMERA_REQUEST_CODE);
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
                        selectImagePop.showAtLocation(ivBusinessLicense, Gravity.BOTTOM, 0, 0);
                        selectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                backgroundAlpha(1);
                            }
                        });
                        backgroundAlpha(0.3f);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void submit() {
        App app = App.getApp();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("shopName", shopName);
        jsonObject.put("corporate", corporateRepresentative);
        jsonObject.put("mobile", phoneNumber);
        jsonObject.put("name", accountUser);
        jsonObject.put("province", app.getGlobalString(Const.SHOP_APPLY_PROVINCE_CODE));
        jsonObject.put("city", app.getGlobalString(Const.SHOP_APPLY_CITY_CODE));
        jsonObject.put("area", app.getGlobalString(Const.SHOP_APPLY_AREA_CODE));
        jsonObject.put("addressDetail", detailAddress);
        jsonObject.put("isTake", isTake);
        jsonObject.put("license", license);
        jsonObject.put("shopImages", images);
        ApiManager.get().getData(Const.SUPPLIERS_CERTIFICATION, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
//                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
//                    ToastUtils.showLongToast(BusinessApplyActivity.this,getString(R.string.submit_success));
                showSubmitSuccessDialog();
//                    finish();
//                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(BusinessApplyActivity.this, msg);
            }
        }, null);
    }

    private void showSubmitSuccessDialog() {
        ApplySubmitSuccessDialog.Builder builder = new ApplySubmitSuccessDialog.Builder(BusinessApplyActivity.this);
        builder.setMessage(getString(R.string.apply_submit_success_content))
                .setBackButton(getString(R.string.back))
                .setDialogInterface(new ApplySubmitSuccessDialog.DialogInterface() {
                    @Override
                    public void goBack(ApplySubmitSuccessDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void uploadImages(final int requestCode, final File file) {
        ApiManager.get().uploadSinglePicture(Const.UPLOAD_IMAGE, new File(BitmapUtils.compressImage(file.getAbsolutePath())), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (!TextUtils.isEmpty(responseModel.data.toString())) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String imageUrl = jo.optString("imageUrl");
                    if (requestCode == Const.REQUEST_SELECT_IMAGES_CODE || requestCode == Const.CAMERA_REQUEST_CODE) {
                        license = imageUrl;
                    }
                    smallBitmap = BitmapUtils.getSmallBitmap(file.getPath());
                    ivBusinessLicense.setImageBitmap(smallBitmap);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(BusinessApplyActivity.this, msg);
            }
        }, null);
    }

    /**
     * 从图库中选取图片
     */
    public void selectImage(int type) {
        ImagePicker.getInstance()
                .setTitle("标题")//设置标题
                .showCamera(true)//设置是否显示拍照按钮
                .showImage(true)//设置是否展示图片
                .showVideo(false)//设置是否展示视频
                .setMaxCount(5)//设置最大选择图片数目(默认为1，单选)
                .setSingleType(true)//设置图片视频不能同时选择
//                .setImagePaths(mImagePaths)//设置历史选择记录
                .setImageLoader(new GlideLoader())//设置自定义图片加载器
                .setMaxCount(1)
                .start(BusinessApplyActivity.this, type);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode

    }

    public void captureImage(final int type) {
        requestPermissions(BusinessApplyActivity.this, new String[]{Manifest.permission.CAMERA}, new RequestPermissionCallBack() {
            @Override
            public void granted() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (type == Const.CAMERA_REQUEST_CODE){
                    cameraFile = getImageSelectTempFile();
                    //android 7.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(BusinessApplyActivity.this, getPackageName() + ".fileprovider", cameraFile)); //Uri.fromFile(tempFile)
                    } else {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    }
                }else if (type == Const.SHOP_CAMERA_REQUEST_CODE){
                    shopCameraFile = getImageSelectTempFile();
                    //android 7.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(BusinessApplyActivity.this, getPackageName() + ".fileprovider", shopCameraFile)); //Uri.fromFile(tempFile)
                    } else {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(shopCameraFile));
                    }
                }
                startActivityForResult(cameraIntent, type);
            }

            @Override
            public void denied() {
                ToastUtils.showShortToast(BusinessApplyActivity.this, "获取权限失败，正常功能受到影响");
            }


        });
    }

    private File getImageSelectTempFile() {
        File dir = new File(getExternalFilesDir("pictures").getAbsolutePath());
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return new File(dir, System.currentTimeMillis() + ".png");
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        this.getWindow().setAttributes(lp);
    }

    private List<TextBean> getBottomList() {
        List<TextBean> textBeans = new ArrayList<>();
        TextBean textBean1 = new TextBean();
        textBean1.setContent("从手机相册中获取");
        textBean1.setColor(R.color.black);
        TextBean textBean2 = new TextBean();
        textBean2.setContent("拍照");
        textBean2.setColor(R.color.black);
        TextBean textBean3 = new TextBean();
        textBean3.setContent("取消");
        textBean3.setColor(R.color.black);
        textBeans.add(textBean1);
        textBeans.add(textBean2);
        textBeans.add(textBean3);
        return textBeans;
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(shopName)) {
            ToastUtils.showLongToast(BusinessApplyActivity.this, getString(R.string.input_shop_organ_name_tip));
            return false;
        }
        if (TextUtils.isEmpty(corporateRepresentative)) {
            ToastUtils.showLongToast(BusinessApplyActivity.this, getString(R.string.input_corporate_representative_tip));
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showLongToast(BusinessApplyActivity.this, getString(R.string.input_shop_organ_phone_tip));
            return false;
        }
        if (TextUtils.isEmpty(accountUser)) {
            ToastUtils.showLongToast(BusinessApplyActivity.this, getString(R.string.input_current_account_holder_tip));
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showLongToast(BusinessApplyActivity.this, getString(R.string.input_shop_organ_address_tip));
            return false;
        }
        if (TextUtils.isEmpty(detailAddress)) {
            ToastUtils.showLongToast(BusinessApplyActivity.this, getString(R.string.input_detail_address_tip));
            return false;
        }
        if (TextUtils.isEmpty(license)) {
            ToastUtils.showLongToast(BusinessApplyActivity.this, getString(R.string.input_load_business_license_tip));
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCache();
        if (smallBitmap != null && !smallBitmap.isRecycled()) {
            smallBitmap.recycle();
            smallBitmap = null;
        }
    }

    private void removeCache() {
        App.getApp().removeString(Const.SHOP_APPLY_PROVINCE_CODE);
        App.getApp().removeString(Const.SHOP_APPLY_CITY_CODE);
        App.getApp().removeString(Const.SHOP_APPLY_AREA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            App app = App.getApp();
            province = app.getGlobalString(Const.SHOP_APPLY_PROVINCE);
            city = app.getGlobalString(Const.SHOP_APPLY_CITY);
            area = app.getGlobalString(Const.SHOP_APPLY_AREA);
            tvShopOrganAddress.setText(province + city + area);
            if(!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)&& !TextUtils.isEmpty(area)){
                tvShopOrganAddress.setText(province + city + area);
            }
            File file;
            if (requestCode == Const.REQUEST_SELECT_IMAGES_CODE) {
                mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                StringBuffer stringBuffer = new StringBuffer();
//                stringBuffer.append("当前选中图片路径：\n\n");
                for (int i = 0; i < mImagePaths.size(); i++) {
                    stringBuffer.append(mImagePaths.get(i));
                }
                file = new File(stringBuffer.toString());
                uploadImages(requestCode, file);
            } else if (requestCode == Const.CAMERA_REQUEST_CODE) {
                uploadImages(requestCode, cameraFile);
            }

            File shopFile;
            if (requestCode == Const.SHOP_REQUEST_SELECT_IMAGES_CODE || requestCode == Const.SHOP_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                shopImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                StringBuffer stringBuffer = new StringBuffer();
//                stringBuffer.append("当前选中图片路径：\n\n");
                for (int i = 0; i < shopImagePaths.size(); i++) {
                    stringBuffer.append(shopImagePaths.get(i));
                }
                shopFile = new File(stringBuffer.toString());
                uploadShopImages(requestCode, shopFile);
            } else if (requestCode == Const.SHOP_CAMERA_REQUEST_CODE) {
                uploadShopImages(requestCode, shopCameraFile);
            }
        }
    }
    private int getDataSize(List<File> files) {
        return files == null ? 0 : files.size();
    }
    private void uploadShopImages(final int requestCode, final File file) {
        ApiManager.get().uploadSinglePicture(Const.UPLOAD_IMAGE, new File(BitmapUtils.compressImage(file.getAbsolutePath())), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (!TextUtils.isEmpty(responseModel.data.toString())) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String imageUrl = jo.optString("imageUrl");
                    if (requestCode == Const.SHOP_REQUEST_SELECT_IMAGES_CODE) {
                        files.add(file);
                        images.add(imageUrl);
                    } else if (requestCode == Const.SHOP_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                        files.set(selectCount, file);
                        images.set(selectCount, imageUrl);
                    } else if (requestCode == Const.SHOP_CAMERA_REQUEST_CODE) {
                        if (isChange) {
                            files.set(selectCount, file);
                            images.set(selectCount, imageUrl);
                        } else {
                            files.add(file);
                            images.add(imageUrl);
                        }
                    }
                    shopImageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(BusinessApplyActivity.this, msg);
            }
        }, null);
    }
}
