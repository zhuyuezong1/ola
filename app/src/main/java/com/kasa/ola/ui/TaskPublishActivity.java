package com.kasa.ola.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.SingleBtnCommonDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ImageEditAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DateUtil;
import com.kasa.ola.utils.ImagePickerUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.datepicker.DatePickerPopWin;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskPublishActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.tv_task_effective_time)
    TextView tvTaskEffectiveTime;
    @BindView(R.id.btn_is_platform_assure)
    CheckBox btnIsPlatformAssure;
    @BindView(R.id.et_contact)
    EditText etContact;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.tv_select_area)
    TextView tvSelectArea;
    @BindView(R.id.et_detail_address)
    EditText etDetailAddress;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    private List<TextBean> bottomList;
    private boolean isChange = false;
    private int selectCount = 0;
    private SelectImagePop selectImagePop;
    private File cameraFile;
    private List<String> images = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private ArrayList<String> mImagePaths;
    private long effectiveTime;
    private String province;
    private String city;
    private String area;
    private boolean isPlatformAssure = false;
    private ImageEditAdapter imageEditAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_publish);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
    }

    private void initEvent() {
        tvTaskEffectiveTime.setOnClickListener(this);
    }

    private void initView() {
        bottomList = CommonUtils.getBottomList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(TaskPublishActivity.this, 4, LinearLayoutManager.VERTICAL,false);
        rvImages.setLayoutManager(gridLayoutManager);
        imageEditAdapter = new ImageEditAdapter(TaskPublishActivity.this, imageUrls);
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
                selectImagePop = new SelectImagePop(TaskPublishActivity.this, bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:// 从图库选择
                                if (!isChange){
                                    selectImage(Const.FOUND_REQUEST_SELECT_IMAGES_CODE);
                                }else {
                                    selectImage(Const.FOUND_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                }
                                selectImagePop.dismiss();
                                break;
                            case 2:// 拍照
                                captureImage(Const.FOUND_CAMERA_REQUEST_CODE);
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
                        CommonUtils.backgroundAlpha(1,TaskPublishActivity.this);
                    }
                });
                CommonUtils.backgroundAlpha(0.3f,TaskPublishActivity.this);
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
                ToastUtils.showLongToast(TaskPublishActivity.this, msg);
            }
        }, null);
    }
    private void initTitle() {
        setActionBar(getString(R.string.publish),"");
    }

    public void selectImage(int type) {
        ImagePickerUtils.selectImageAndCrop(TaskPublishActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, TaskPublishActivity.this);
                uploadImages(type, file);
            }
        });
    }
    public void captureImage(final int type) {
        ImagePickerUtils.captureImageAndCrop(TaskPublishActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, TaskPublishActivity.this);
                uploadImages(type, file);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            App app = App.getApp();
            province = app.getGlobalString(Const.COMMON_PROVINCE_CODE);
            city = app.getGlobalString(Const.COMMON_CITY_CODE);
            area = app.getGlobalString(Const.COMMON_AREA_CODE);
//            tvShopOrganAddress.setText(province + city + area);
            if(!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)&& !TextUtils.isEmpty(area)){
                tvSelectArea.setText(province + city + area);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_task_effective_time:
                showDateDialog(tvTaskEffectiveTime);
                break;
            case R.id.tv_select_area:
                Intent addrIntent = new Intent(TaskPublishActivity.this, ProvinceListActivity.class);
                addrIntent.putExtra(Const.SELECT_AREA_ENTER, 1);
                startActivityForResult(addrIntent, Const.ACTREQ_PROVINCE);
                break;
            case R.id.btn_is_platform_assure:
                if (isPlatformAssure) {
                    btnIsPlatformAssure.setChecked(false);
                } else {
                    btnIsPlatformAssure.setChecked(true);
                    showPlatformAssureDialog();
                }
                isPlatformAssure = !isPlatformAssure;
                break;
        }
    }

    private void showPlatformAssureDialog() {
        SingleBtnCommonDialog.Builder builder = new SingleBtnCommonDialog.Builder(TaskPublishActivity.this);
        builder.setTitle(getString(R.string.platform_assure))
                .setMessage("平台担保说明")
                .setRightButton(getString(R.string.know))
                .setDialogInterface(new SingleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void rightButtonClick(SingleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }
                }).create().show();
    }


    private void showDateDialog(final TextView tv) {
        final String currentDay = TextUtils.isEmpty(tv.getText()) ? DateUtil.getToday() : tv.getText().toString();
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(TaskPublishActivity.this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                if (DateUtil.formatDateStr(dateDesc, DateUtil.ymd) > DateUtil.formatDateStr(DateUtil.getToday(), DateUtil.ymd)) {
                    ToastUtils.showShortToast(TaskPublishActivity.this, "不能超过当前日期");
                    return;
                }

                effectiveTime = DateUtil.formatDateStr(dateDesc, DateUtil.ymd);
                tv.setText(dateDesc);

            }
        }).minYear(1990) //min year in loop
                .maxYear(2250) // max year in loop
                .dateChose(currentDay) // date chose when init popwindow
                .build();
        pickerPopWin.showAtLocation(TaskPublishActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }
}
