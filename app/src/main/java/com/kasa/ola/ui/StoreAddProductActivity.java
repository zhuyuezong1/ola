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

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ShopProductBean;
import com.kasa.ola.bean.entity.StoreProductBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ImageEditAdapter;
import com.kasa.ola.ui.adapter.ImageShowAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.ImagePickerUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreAddProductActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.et_store_product_name)
    EditText etStoreProductName;
    @BindView(R.id.et_store_product_desc)
    EditText etStoreProductDesc;
    @BindView(R.id.et_store_product_special_price)
    EditText etStoreProductSpecialPrice;
    @BindView(R.id.tv_price_title)
    TextView tvPriceTitle;
    @BindView(R.id.et_store_product_price)
    EditText etStoreProductPrice;
    @BindView(R.id.et_store_product_num)
    EditText etStoreProductNum;
    @BindView(R.id.rv_product_images)
    RecyclerView rvProductImages;
    @BindView(R.id.rv_product_details)
    RecyclerView rvProductDetails;
    @BindView(R.id.btn_after_pass_up)
    CheckBox btnAfterPassUp;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.tv_notice_tips)
    TextView tvNoticeTips;
    private List<TextBean> bottomList;
    private List<String> imageUrls = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private ImageEditAdapter imageEditAdapter;
    private boolean isChange = false;
    private int selectCount = 0;
    private SelectImagePop selectImagePop;

    private List<String> detailsImageUrls = new ArrayList<>();
    private List<String> detailsImages = new ArrayList<>();
    private ImageEditAdapter detailsImageEditAdapter;
    private boolean isChangeDetails = false;
    private int selectCountDetails = 0;
    private SelectImagePop detailsSelectImagePop;
    private String isAuto = "0";
    private boolean isAfterPassThenUp = false;
    private String productName;
    private String productDesc;
    private String specialPrice;
    private String price;
    private String productNum;
    private ShopProductBean shopProductBean;

    private List<String> showImageUrls = new ArrayList<>();
    private List<String> showDetailsImageUrls = new ArrayList<>();
    private ImageShowAdapter imageShowAdapter;
    private ImageShowAdapter detailsImageShowAdapter;
    private String productID = "";
    private boolean isEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_add_product);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        shopProductBean = (ShopProductBean) intent.getSerializableExtra(Const.STORE_PRODUCT_DETAILS);
        initTitle();
        initView();
        initEvent();
    }

    private void initEvent() {
        btnAfterPassUp.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(StoreAddProductActivity.this, 4, LinearLayoutManager.VERTICAL, false);
        rvProductImages.setLayoutManager(gridLayoutManager);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(StoreAddProductActivity.this, 4, LinearLayoutManager.VERTICAL, false);
        rvProductDetails.setLayoutManager(gridLayoutManager1);
        bottomList = CommonUtils.getBottomList();
        if (shopProductBean != null && (!shopProductBean.getProductStatus().equals("3")|| !shopProductBean.getProductStatus().equals("1"))) {//不可修改
            isEditable = false;
        } else{
            isEditable = true;
        }
        if (isEditable) {
            tvNoticeTips.setVisibility(View.GONE);
            setCanEdit();
            setEditTextEnable(true);
            setEditTextColor(R.color.COLOR_FF666666);
            tvPriceTitle.setText(getString(R.string.store_product_price_title, getString(R.string.choose_write)));
            btnCommit.setBackgroundResource(R.drawable.shape_blue_corner_22);
            if (shopProductBean != null){
                productID = shopProductBean.getProductID();
            }
        } else {
            tvNoticeTips.setVisibility(View.VISIBLE);
            setCanNotEdit();
            setEditTextEnable(false);
            setEditTextColor(R.color.COLOR_FF999999);
            tvPriceTitle.setText(getString(R.string.store_product_price_title, ""));
            btnCommit.setBackgroundResource(R.drawable.shape_corner_d5d5d5);
            productID = shopProductBean.getProductID();
        }
        if (!TextUtils.isEmpty(productID)) {
            getProductDetails(productID);
        }
    }

    private void getProductDetails(String productID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("productID", productID);
        ApiManager.get().getData(Const.GET_SUPPLIERS_PRODUCT_INFO, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    StoreProductBean storeProductBean = JsonUtils.jsonToObject(jo.toString(), StoreProductBean.class);
                    etStoreProductName.setText(storeProductBean.getProductName());
                    etStoreProductDesc.setText(storeProductBean.getDescribe());
                    etStoreProductNum.setText(storeProductBean.getInventory());
                    isAuto = storeProductBean.getIsAuto();
                    if (isAuto.equals("0")){
                        isAfterPassThenUp = false;
                    }else if (isAuto.equals("1")){
                        isAfterPassThenUp = true;
                    }
                    btnAfterPassUp.setChecked(isAfterPassThenUp);
                    if (isEditable) {//可编辑
                        etStoreProductSpecialPrice.setText(storeProductBean.getSpecialPrice());
                        etStoreProductPrice.setText(storeProductBean.getPrice());
                        imageUrls.clear();
                        images.clear();
                        imageUrls.addAll(storeProductBean.getImgUrlArr());
                        images.addAll(storeProductBean.getImgArr());
                        imageEditAdapter.notifyDataSetChanged();
                        detailsImageUrls.clear();
                        detailsImageUrls.addAll(storeProductBean.getDetailImgUrlArr());
                        detailsImages.addAll(storeProductBean.getDetailImgArr());
                        detailsImageEditAdapter.notifyDataSetChanged();
                    } else {
                        etStoreProductSpecialPrice.setText(getString(R.string.commission_value, storeProductBean.getSpecialPrice()));
                        etStoreProductPrice.setText(getString(R.string.commission_value, storeProductBean.getPrice()));
                        showImageUrls.clear();
                        showImageUrls.addAll(storeProductBean.getImgUrlArr());
                        imageShowAdapter.notifyDataSetChanged();
                        showDetailsImageUrls.clear();
                        showDetailsImageUrls.addAll(storeProductBean.getDetailImgUrlArr());
                        detailsImageShowAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(StoreAddProductActivity.this, msg);
            }
        }, null);
    }

    private void setCanNotEdit() {
        imageShowAdapter = new ImageShowAdapter(StoreAddProductActivity.this, showImageUrls);
        rvProductImages.setAdapter(imageShowAdapter);
        detailsImageShowAdapter = new ImageShowAdapter(StoreAddProductActivity.this, showDetailsImageUrls);
        rvProductDetails.setAdapter(detailsImageShowAdapter);
    }

    private void setEditTextColor(int color) {
        etStoreProductName.setTextColor(getResources().getColor(color));
        etStoreProductDesc.setTextColor(getResources().getColor(color));
        etStoreProductSpecialPrice.setTextColor(isEditable ? getResources().getColor(R.color.red) : getResources().getColor(R.color.COLOR_FF999999));
        etStoreProductPrice.setTextColor(getResources().getColor(color));
        etStoreProductNum.setTextColor(getResources().getColor(color));
    }

    private void setEditTextEnable(boolean editTextEnable) {
        etStoreProductName.setEnabled(editTextEnable);
        etStoreProductDesc.setEnabled(editTextEnable);
        etStoreProductSpecialPrice.setEnabled(editTextEnable);
        etStoreProductPrice.setEnabled(editTextEnable);
        etStoreProductNum.setEnabled(editTextEnable);
        btnAfterPassUp.setEnabled(editTextEnable);
    }

    private void setCanEdit() {

        imageEditAdapter = new ImageEditAdapter(StoreAddProductActivity.this, imageUrls);
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
                selectImagePop = new SelectImagePop(StoreAddProductActivity.this, bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:// 从图库选择
                                if (!isChange) {
                                    selectImage(Const.STORE_PRODUCT_REQUEST_SELECT_IMAGES_CODE);
                                } else {
                                    selectImage(Const.STORE_PRODUCT_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                }
                                selectImagePop.dismiss();
                                break;
                            case 2:// 拍照
                                captureImage(Const.STORE_PRODUCT_CAMERA_REQUEST_CODE);
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
                selectImagePop.showAtLocation(rvProductImages, Gravity.BOTTOM, 0, 0);
                selectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        CommonUtils.backgroundAlpha(1, StoreAddProductActivity.this);
                    }
                });
                CommonUtils.backgroundAlpha(0.3f, StoreAddProductActivity.this);
            }

            @Override
            public void deleteItem(int position) {
                images.remove(position);
                imageUrls.remove(position);
                imageEditAdapter.notifyDataSetChanged();
            }
        });
        rvProductImages.setAdapter(imageEditAdapter);
        detailsImageEditAdapter = new ImageEditAdapter(StoreAddProductActivity.this, detailsImageUrls,8);
        detailsImageEditAdapter.setOnItemClickListener(new ImageEditAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int position) {
                if (position == CommonUtils.getImageDataSize(detailsImages)) {// 点击“+”号位置添加图片
                    isChangeDetails = false;
                } else {
                    selectCountDetails = position;
                    isChangeDetails = true;
                }
                detailsSelectImagePop = new SelectImagePop(StoreAddProductActivity.this, bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:// 从图库选择
                                if (!isChangeDetails) {
                                    selectImage(Const.STORE_PRODUCT_DETAILS_REQUEST_SELECT_IMAGES_CODE);
                                } else {
                                    selectImage(Const.STORE_PRODUCT_DETAILS_REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                }
                                detailsSelectImagePop.dismiss();
                                break;
                            case 2:// 拍照
                                captureImage(Const.STORE_PRODUCT_DETAILS_CAMERA_REQUEST_CODE);
                                detailsSelectImagePop.dismiss();
                                break;
                            case 3:// 取消
                                detailsSelectImagePop.dismiss();
                                break;

                            default:
                                break;
                        }
                    }
                });
                detailsSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                detailsSelectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                detailsSelectImagePop.showAtLocation(rvProductDetails, Gravity.BOTTOM, 0, 0);
                detailsSelectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        CommonUtils.backgroundAlpha(1, StoreAddProductActivity.this);
                    }
                });
                CommonUtils.backgroundAlpha(0.3f, StoreAddProductActivity.this);
            }

            @Override
            public void deleteItem(int position) {
                detailsImages.remove(position);
                detailsImageUrls.remove(position);
                detailsImageEditAdapter.notifyDataSetChanged();
            }
        });
        rvProductDetails.setAdapter(detailsImageEditAdapter);
    }

    /**
     * 从图库中选取图片
     */
    public void selectImage(int type) {
        ImagePickerUtils.selectImageAndCrop(StoreAddProductActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, StoreAddProductActivity.this);
                uploadImages(type, file);
            }
        });
    }

    public void captureImage(final int type) {
        ImagePickerUtils.captureImageAndCrop(StoreAddProductActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, StoreAddProductActivity.this);
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
                        if (requestCode == Const.STORE_PRODUCT_REQUEST_SELECT_IMAGES_CODE) {
                            imageUrls.add(imageUrl);
                            images.add(url);
                            imageEditAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_PRODUCT_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                            imageUrls.set(selectCount, imageUrl);
                            images.set(selectCount, url);
                            imageEditAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_PRODUCT_CAMERA_REQUEST_CODE) {
                            if (isChange) {
                                imageUrls.set(selectCount, imageUrl);
                                images.set(selectCount, url);
                            } else {
                                imageUrls.add(imageUrl);
                                images.add(url);
                            }
                            imageEditAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_PRODUCT_DETAILS_REQUEST_SELECT_IMAGES_CODE) {
                            detailsImageUrls.add(imageUrl);
                            detailsImages.add(url);
                            detailsImageEditAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_PRODUCT_DETAILS_REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                            detailsImageUrls.set(selectCountDetails, imageUrl);
                            detailsImages.set(selectCountDetails, url);
                            detailsImageEditAdapter.notifyDataSetChanged();
                        } else if (requestCode == Const.STORE_PRODUCT_DETAILS_CAMERA_REQUEST_CODE) {
                            if (isChange) {
                                detailsImageUrls.set(selectCountDetails, imageUrl);
                                detailsImages.set(selectCountDetails, url);
                            } else {
                                detailsImageUrls.add(imageUrl);
                                detailsImages.add(url);
                            }
                            detailsImageEditAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(StoreAddProductActivity.this, msg);
            }
        }, null);
    }

    private void initTitle() {
        setActionBar(getString(R.string.add_product), "");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_after_pass_up:
                if (isAfterPassThenUp) {
                    btnAfterPassUp.setChecked(false);
                    isAuto = "0";
                } else {
                    btnAfterPassUp.setChecked(true);
                    isAuto = "1";
                }
                isAfterPassThenUp = !isAfterPassThenUp;
                break;
            case R.id.btn_commit:
                productName = etStoreProductName.getText().toString().trim();
                productDesc = etStoreProductDesc.getText().toString().trim();
                specialPrice = etStoreProductSpecialPrice.getText().toString().trim();
                price = etStoreProductPrice.getText().toString().trim();
                productNum = etStoreProductNum.getText().toString().trim();
                if (checkData()) {
                    submit();
                }
                break;
        }
    }

    private void submit() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("productID", productID);
        jsonObject.put("productName", productName);
        jsonObject.put("describe", productDesc);
        jsonObject.put("specialPrice", specialPrice);
        jsonObject.put("price", price);
        jsonObject.put("inventory", productNum);
        jsonObject.put("imgArr", images);
        jsonObject.put("detailImgArr", detailsImages);
        jsonObject.put("isAuto", isAuto);
        ApiManager.get().getData(Const.ADD_PRODUCT, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.add_success));
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(StoreAddProductActivity.this, msg);
            }
        }, null);
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(productName)) {
            ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.input_product_name_tips));
            return false;
        }
        if (TextUtils.isEmpty(productDesc)) {
            ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.input_product_desc_tips));
            return false;
        }
        if (TextUtils.isEmpty(specialPrice)) {
            ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.input_product_special_price_tips));
            return false;
        }
        if (TextUtils.isEmpty(price)) {
            ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.input_product_price_tips));
            return false;
        }
        if (TextUtils.isEmpty(productNum)) {
            ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.input_product_num_tips));
            return false;
        }
        if (images == null || images.size() < 1) {
            ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.input_product_images_tips));
            return false;
        }
        if (detailsImages == null || detailsImages.size() < 1) {
            ToastUtils.showLongToast(StoreAddProductActivity.this, getString(R.string.input_product_details_tips));
            return false;
        }
        return true;
    }
}
