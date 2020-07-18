package com.kasa.ola.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.ImmersionOwner;
import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.OrdinaryDialog;
import com.kasa.ola.dialog.SingleBtnCommonDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.AMapSearchPosiActivity;
import com.kasa.ola.ui.AMapSelectPosiActivity;
import com.kasa.ola.ui.adapter.FoundImageAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImagePickerUtils;
import com.kasa.ola.utils.ToastUtils;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class ShopAllowedFragment extends BaseFragment implements ImmersionOwner, View.OnClickListener {

    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.iv_back_frag)
    ImageView ivBackFrag;
    @BindView(R.id.tv_title_frag)
    TextView tvTitleFrag;
    @BindView(R.id.tv_right_text_frag)
    TextView tvRightTextFrag;
    @BindView(R.id.webProgressBar)
    ProgressBar webProgressBar;
    @BindView(R.id.view_actionbar_frag)
    LinearLayout viewActionbarFrag;
    @BindView(R.id.et_shop_organ_name)
    EditText etShopOrganName;
    @BindView(R.id.et_corporate_representative)
    EditText etCorporateRepresentative;
    @BindView(R.id.et_current_account_holder_name)
    EditText etCurrentAccountHolderName;
    @BindView(R.id.et_shop_organ_phone)
    EditText etShopOrganPhone;
    @BindView(R.id.tv_shop_organ_address)
    TextView tvShopOrganAddress;
    @BindView(R.id.et_detail_address)
    EditText etDetailAddress;
    @BindView(R.id.et_business_hours)
    EditText etBusinessHours;
    //    @BindView(R.id.iv_business_license)
//    ImageView ivBusinessLicense;
//    @BindView(R.id.iv_delete)
//    ImageView ivDelete;
//    @BindView(R.id.rl_upload)
//    RelativeLayout rlUpload;
//    @BindView(R.id.rl_publish)
//    RelativeLayout rlPublish;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.btn_is_self)
    CheckBox btnIsSelf;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.sl_apply)
    ScrollView slApply;
    @BindView(R.id.tv_checking)
    TextView tvChecking;
    @BindView(R.id.rv_business_license)
    RecyclerView rvBusinessLicense;
    @BindView(R.id.tv_self_mention_point_title)
    TextView tvSelfMentionPointTitle;
    private LayoutInflater inflater;
    Unbinder unbinder;
    private File businessLicense = null;
    private List<TextBean> bottomList;
    private List<File> files = new ArrayList<>();
    private List<File> licenseFiles = new ArrayList<>();
    private FoundImageAdapter foundImageAdapter;
    private boolean isChange = false;
    private boolean isLicenseChange = false;
    private int selectCount = 0;
    private int licenseSelectCount = 0;
    private SelectImagePop selectImagePop;
    private SelectImagePop selectLicenseImagePop;
    private List<String> images = new ArrayList<>();
    private List<String> licenseImages = new ArrayList<>();
    private ArrayList<String> mImagePaths;
    private boolean isSelf = false;
    private String province;
    private String city;
    private String area;
    private String businessLvl;
    private FoundImageAdapter licenseFoundImageAdapter;
    private boolean isFirst = true;
    private String shopName;
    private String corporateRepresentative;
    private String accountUser;
    private String phoneNumber;
    private String detailAddress;
    private String address;
    private String license;
    private String isTake = "0";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_shop_allowed, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        businessLvl = LoginHandler.get().getMyInfo().optString("businessLvl");
        initTitle(rootView);
        initView();
        initEvent();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeCache();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {   // 不在最前端显示 相当于调用了onPause();
            ImmersionBar.with(this)
                    .statusBarDarkFont(false)
                    .init();
        } else {  // 在最前端显示 相当于调用了onResume();
            ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .init();
        }
    }

    private void initEvent() {
        tvSelfMentionPointTitle.setOnClickListener(this);
        btnIsSelf.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        tvShopOrganAddress.setOnClickListener(this);
    }

    private void initView() {
        if (businessLvl.equals("0")) {
            slApply.setVisibility(View.VISIBLE);
            tvChecking.setVisibility(View.GONE);
        } else if (businessLvl.equals("2")) {
            slApply.setVisibility(View.GONE);
            tvChecking.setVisibility(View.VISIBLE);
        }
       etShopOrganName .setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
       etCurrentAccountHolderName .setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
       etCorporateRepresentative .setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
       etShopOrganPhone .setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        rvBusinessLicense.setLayoutManager(gridLayoutManager1);
        licenseFoundImageAdapter = new FoundImageAdapter(getContext(), licenseFiles, 1);
        licenseFoundImageAdapter.setOnItemClickListener(new FoundImageAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (position == getDataSize(licenseFiles)) {// 点击“+”号位置添加图片
                    isLicenseChange = false;
                } else {
                    licenseSelectCount = position;
                    isLicenseChange = true;
                }
                if (isFirst) {
                    showIntroDialog();
                } else {
                    selectLicenseImagePop = new SelectImagePop(getContext(), bottomList, new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            switch (position + 1) {
                                case 1:// 从图库选择
                                    if (!isLicenseChange) {
                                        selectImage(Const.LICENSE_REQUEST_SELECT_IMAGES_CODE);
                                    } else {
                                        selectImage(Const.LICENSE_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                    }
                                    selectLicenseImagePop.dismiss();
                                    break;
                                case 2:// 拍照
                                    captureImage(Const.LICENSE_CAMERA_REQUEST_CODE);
                                    selectLicenseImagePop.dismiss();
                                    break;
                                case 3:// 取消
                                    selectLicenseImagePop.dismiss();
                                    break;

                                default:
                                    break;
                            }
                        }
                    });
                    selectLicenseImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                    selectLicenseImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    selectLicenseImagePop.showAtLocation(rvImages, Gravity.BOTTOM, 0, 0);
                    selectLicenseImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            CommonUtils.backgroundAlpha(1, getActivity());
                        }
                    });
                    CommonUtils.backgroundAlpha(0.3f, getActivity());
                }
            }

            @Override
            public void deleteItem(int position) {
                licenseImages.remove(position);
                licenseFiles.remove(position);
                licenseFoundImageAdapter.notifyDataSetChanged();
            }
        });
        rvBusinessLicense.setAdapter(licenseFoundImageAdapter);

        bottomList = CommonUtils.getBottomList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        rvImages.setLayoutManager(gridLayoutManager);
        foundImageAdapter = new FoundImageAdapter(getContext(), files);
        foundImageAdapter.setOnItemClickListener(new FoundImageAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (position == getDataSize(files)) {// 点击“+”号位置添加图片
                    isChange = false;
                } else {
                    selectCount = position;
                    isChange = true;
                }
                selectImagePop = new SelectImagePop(getContext(), bottomList, new OnItemClickListener() {
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
                        CommonUtils.backgroundAlpha(1, getActivity());
                    }
                });
                CommonUtils.backgroundAlpha(0.3f, getActivity());
            }

            @Override
            public void deleteItem(int position) {
                images.remove(position);
                files.remove(position);
                foundImageAdapter.notifyDataSetChanged();
            }
        });
        rvImages.setAdapter(foundImageAdapter);
    }

    public void selectImage(int type) {
        ImagePickerUtils.selectImageAndCrop(getActivity(), new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, getContext());
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
                    String imageUrl = jo.optString("imageUrl");
                    if (!TextUtils.isEmpty(imageUrl)) {
                        if (requestCode == Const.LICENSE_REQUEST_SELECT_IMAGES_CODE) {
                            licenseFiles.add(file);
                            licenseImages.add(imageUrl);
                            licenseFoundImageAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.LICENSE_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                            licenseFiles.set(licenseSelectCount, file);
                            licenseImages.set(licenseSelectCount, imageUrl);
                            licenseFoundImageAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.LICENSE_CAMERA_REQUEST_CODE) {
                            if (isLicenseChange) {
                                licenseFiles.set(licenseSelectCount, file);
                                licenseImages.set(licenseSelectCount, imageUrl);
                            } else {
                                licenseFiles.add(file);
                                licenseImages.add(imageUrl);
                            }
                            licenseFoundImageAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_REQUEST_SELECT_IMAGES_CODE) {
                            files.add(file);
                            images.add(imageUrl);
                            foundImageAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                            files.set(selectCount, file);
                            images.set(selectCount, imageUrl);
                            foundImageAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_CAMERA_REQUEST_CODE) {
                            if (isChange) {
                                files.set(selectCount, file);
                                images.set(selectCount, imageUrl);
                            } else {
                                files.add(file);
                                images.add(imageUrl);
                            }
                            foundImageAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, null);
    }

    public void captureImage(final int type) {
        ImagePickerUtils.captureImageAndCrop(getActivity(), new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, getContext());
                uploadImages(type, file);
            }
        });
    }
//    public void captureImage(final int type) {
//        ((MainActivity) getActivity()).requestPermissions(getContext(), new String[]{Manifest.permission.CAMERA}, new BaseActivity.RequestPermissionCallBack() {
//            @Override
//            public void granted() {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                cameraFile = getImageSelectTempFile();
//                //android 7.0
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".fileprovider", cameraFile)); //Uri.fromFile(tempFile)
//                } else {
//                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
//                }
//                startActivityForResult(cameraIntent, type);
//            }
//
//            @Override
//            public void denied() {
//                ToastUtils.showShortToast(getContext(), "获取权限失败，正常功能受到影响");
//            }
//        });
//    }

    private void initTitle(View view) {
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setActionBar(view, getString(R.string.shop_allowed));
            viewActionbarFrag.setBackgroundColor(getContext().getColor(R.color.white));
            tvTitleFrag.setTextColor(getContext().getColor(R.color.COLOR_FF333333));
            ivBackFrag.setVisibility(View.GONE);
            viewActionbarFrag.setPadding(0, DisplayUtils.getStatusBarHeight2(getActivity()), 0, 0);
        }
    }

    @Override
    public void onLazyBeforeView() {

    }

    @Override
    public void onLazyAfterView() {

    }

    @Override
    public void onVisible() {

    }

    @Override
    public void onInvisible() {

    }

    @Override
    public void initImmersionBar() {

    }

    private void removeCache() {
        App.getApp().removeString(Const.SHOP_APPLY_PROVINCE_CODE);
        App.getApp().removeString(Const.SHOP_APPLY_CITY_CODE);
        App.getApp().removeString(Const.SHOP_APPLY_AREA_CODE);
    }

    @Override
    public boolean immersionBarEnabled() {
        return false;
    }

    private int getDataSize(List<File> files) {
        return files == null ? 0 : files.size();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            App app = App.getApp();
            province = app.getGlobalString(Const.SHOP_APPLY_PROVINCE);
            city = app.getGlobalString(Const.SHOP_APPLY_CITY);
            area = app.getGlobalString(Const.SHOP_APPLY_AREA);
//            tvShopOrganAddress.setText(province + city + area);
            if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(area)) {
                tvShopOrganAddress.setText(province + city + area);
            }
            if (requestCode==Const.SELECT_AMAP_LOCATION){
                PoiItem poiItem = data.getParcelableExtra(Const.POSITION_INFO);
                String provinceName = poiItem.getProvinceName();
                String cityName = poiItem.getCityName();
                String adName = poiItem.getAdName();
                String snippet = poiItem.getSnippet();
                String title = poiItem.getTitle();
                tvShopOrganAddress.setText(provinceName+cityName+adName);
                etDetailAddress.setText(snippet+title);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btn_commit:
                shopName = etShopOrganName.getText().toString().trim();
                corporateRepresentative = etCorporateRepresentative.getText().toString().trim();
                accountUser = etCurrentAccountHolderName.getText().toString().trim();
                phoneNumber = etShopOrganPhone.getText().toString().trim();
                address = tvShopOrganAddress.getText().toString();
                detailAddress = etDetailAddress.getText().toString().trim();
                if (checkData()){
                    license = images.get(0);
                    submit();
                }
                break;
            case R.id.tv_shop_organ_address:
//                Intent addrIntent = new Intent(getContext(), ProvinceListActivity.class);
//                addrIntent.putExtra(Const.SELECT_AREA_ENTER, 2);
//                startActivityForResult(addrIntent, Const.ACTREQ_PROVINCE);
//                Intent intent = new Intent(getContext(), AmapActivity.class);
                Intent intent = new Intent(getContext(), AMapSelectPosiActivity.class);
//                Intent intent = new Intent(getContext(), AMapSearchPosiActivity.class);
                startActivityForResult(intent,Const.SELECT_AMAP_LOCATION);
                break;
            case R.id.rl_publish:
                showIntroDialog();
                break;

        }
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
                slApply.setVisibility(View.GONE);
                tvChecking.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, null);
    }
    private boolean checkData() {
        if (TextUtils.isEmpty(shopName)) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_shop_organ_name_tip));
            return false;
        }
        if (TextUtils.isEmpty(corporateRepresentative)) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_corporate_representative_tip));
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_shop_organ_phone_tip));
            return false;
        }if (phoneNumber.length()<8) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_shop_organ_phone_length_tip));
            return false;
        }
        if (TextUtils.isEmpty(accountUser)) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_current_account_holder_tip));
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_shop_organ_address_tip));
            return false;
        }
        if (TextUtils.isEmpty(detailAddress)) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_detail_address_tip));
            return false;
        }
        if (licenseImages==null || licenseImages.size()<1) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_load_business_license_tip));
            return false;
        }
        if (images==null || images.size()<1) {
            ToastUtils.showLongToast(getContext(), getString(R.string.input_shop_images_tip));
            return false;
        }
        return true;
    }

    private void showSelfDialog() {
        SingleBtnCommonDialog.Builder builder = new SingleBtnCommonDialog.Builder(getContext());
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

    private void showIntroDialog() {
        OrdinaryDialog.Builder builder = new OrdinaryDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.upload_business_license_intro_title))
                .setMessage(getContext().getString(R.string.upload_business_license_intro_content))
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
                        dialog.dismiss();
                        isFirst = false;
                        selectLicenseImagePop = new SelectImagePop(getContext(), bottomList, new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position + 1) {
                                    case 1:// 从图库选择
                                        if (!isLicenseChange) {
                                            selectImage(Const.LICENSE_REQUEST_SELECT_IMAGES_CODE);
                                        } else {
                                            selectImage(Const.LICENSE_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                        }
                                        selectLicenseImagePop.dismiss();
                                        break;
                                    case 2:// 拍照
                                        captureImage(Const.LICENSE_CAMERA_REQUEST_CODE);
                                        selectLicenseImagePop.dismiss();
                                        break;
                                    case 3:// 取消
                                        selectLicenseImagePop.dismiss();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                        selectLicenseImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                        selectLicenseImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        selectLicenseImagePop.showAtLocation(rvImages, Gravity.BOTTOM, 0, 0);
                        selectLicenseImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                CommonUtils.backgroundAlpha(1, getActivity());
                            }
                        });
                        CommonUtils.backgroundAlpha(0.3f, getActivity());
                    }
                })
                .create()
                .show();
    }

}
