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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.MallOrderBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.bean.entity.PublishProductBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.PublishProductCommentAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.ImagePickerUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishProductCommentActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_publish_comment)
    LoadMoreRecyclerView rvPublishComment;
    private List<MallOrderBean.ProductOrder> productOrderList;
    private List<List<File>> filesList = new ArrayList<>();
//    private List<File> files = new ArrayList<>();
    private SelectImagePop selectImagePop;
    private List<TextBean> bottomList;
    private boolean isChange = false;
    private int selectCount = 0;
    private int selectItem = 0;
    private File cameraFile;
    private ArrayList<String> mImagePaths;
//    private List<String> images = new ArrayList<>();
    private PublishProductCommentAdapter publishProductCommentAdapter;
    private List<PublishProductBean> publishList = new ArrayList<>();
    private String orderNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_product_comment);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        productOrderList = (List<MallOrderBean.ProductOrder>) intent.getSerializableExtra(Const.PUBLISH_PRODUCT_COMMENT);
        orderNo = intent.getStringExtra(Const.ORDER_ID_KEY);
        initTitle();
        initView();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPublishComment.setLayoutManager(linearLayoutManager);
        bottomList = CommonUtils.getBottomList();
        for (int i = 0;i<productOrderList.size();i++){
            List<File> objects = new ArrayList<>();
            filesList.add(objects);
            PublishProductBean publishProductBean = new PublishProductBean();
            publishProductBean.setProductID(productOrderList.get(i).getProductID());
            publishProductBean.setSpe(productOrderList.get(i).getSpe());
            List<String> images = new ArrayList<>();
            publishProductBean.setImageArr(images);
            publishList.add(publishProductBean);
        }
        publishProductCommentAdapter = new PublishProductCommentAdapter(PublishProductCommentActivity.this, productOrderList,filesList);
        publishProductCommentAdapter.setProductPublishImageListener(new PublishProductCommentAdapter.ProductPublishImageListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(int publishPosition, final int imagePosition) {
                selectItem = publishPosition;
                List<File> files = filesList.get(selectItem);
                if (imagePosition == CommonUtils.getDataSize(files)) {// 点击“+”号位置添加图片
                    isChange = false;
                }else {
                    selectCount = imagePosition;
                    isChange = true;
                }
                selectImagePop = new SelectImagePop(PublishProductCommentActivity.this, bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:// 从图库选择
                                if (!isChange) {
                                    selectImage(Const.REQUEST_SELECT_IMAGES_CODE);
                                } else {
                                    selectImage(Const.REQUEST_SELECT_IMAGES_CHANGE_CODE);
                                }
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
                selectImagePop.showAtLocation(rvPublishComment, Gravity.BOTTOM, 0, 0);
                selectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        CommonUtils.backgroundAlpha(1,PublishProductCommentActivity.this);
                    }
                });
                CommonUtils.backgroundAlpha(0.3f,PublishProductCommentActivity.this);
            }

            @Override
            public void deleteItem(int position, int imagePosition) {
                List<File> files = filesList.get(selectItem);
                PublishProductBean publishProductBean = publishList.get(selectCount);
                publishProductBean.getImageArr().remove(imagePosition);
                files.remove(imagePosition);
                publishProductCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void commentContent(int position, String commentContent) {
                publishList.get(position).setComment(commentContent);
            }
        });
        rvPublishComment.setAdapter(publishProductCommentAdapter);
    }
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            File file;
//            if (requestCode == Const.REQUEST_SELECT_IMAGES_CODE || requestCode == Const.REQUEST_SELECT_IMAGES_CHANGE_CODE) {
//                mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
//                StringBuffer stringBuffer = new StringBuffer();
////                stringBuffer.append("当前选中图片路径：\n\n");
//                for (int i = 0; i < mImagePaths.size(); i++) {
//                    stringBuffer.append(mImagePaths.get(i));
//                }
//                file = new File(stringBuffer.toString());
//                uploadImages(selectItem, requestCode, file);
//            } else if (requestCode == Const.CAMERA_REQUEST_CODE) {
//                uploadImages(selectItem, requestCode, cameraFile);
//            }
        }
    }
    private void uploadImages(final int selectItem,int requestCode, File file) {
        ApiManager.get().uploadSinglePicture(Const.UPLOAD_IMAGE, new File(BitmapUtils.compressImage(file.getAbsolutePath())), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (!TextUtils.isEmpty(responseModel.data.toString())) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String imageUrl = jo.optString("imageUrl");
                    List<File> files = filesList.get(selectItem);
                    List<String> imageArr = publishList.get(selectItem).getImageArr();
                    if (requestCode == Const.REQUEST_SELECT_IMAGES_CODE) {
                        files.add(file);
                        imageArr.add(imageUrl);
                    } else if (requestCode == Const.REQUEST_SELECT_IMAGES_CHANGE_CODE) {
                        files.set(selectCount, file);
                        imageArr.set(selectCount,imageUrl);
                    } else if (requestCode == Const.CAMERA_REQUEST_CODE) {
                        if (isChange) {
                            files.set(selectCount, file);
                            imageArr.set(selectCount,imageUrl);
                        } else {
                            files.add(file);
                            imageArr.add(imageUrl);
                        }
                    }
                    publishProductCommentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(PublishProductCommentActivity.this, msg);
            }
        }, null);
    }
//    private void uploadImages(final int selectItem, final int requestCode, final File file) {
//        ApiManager.get().uploadSinglePicture(Const.UPLOAD_IMAGE,new File(BitmapUtils.compressImage(file.getAbsolutePath())), new BusinessBackListener() {
//            @Override
//            public void onBusinessSuccess(BaseResponseModel responseModel) {
//                if (!TextUtils.isEmpty(responseModel.data.toString())) {
//                    JSONObject jo = (JSONObject) responseModel.data;
//                    String imageUrl = jo.optString("imageUrl");
//                    List<File> files = filesList.get(selectItem);
//                    List<String> imageArr = publishList.get(selectItem).getImageArr();
//                    if (requestCode == Const.REQUEST_SELECT_IMAGES_CODE) {
//                        files.add(file);
//                        imageArr.add(imageUrl);
//                    } else if (requestCode == Const.REQUEST_SELECT_IMAGES_CHANGE_CODE) {
//                        files.set(selectCount, file);
//                        imageArr.set(selectCount,imageUrl);
//                    } else if (requestCode == Const.CAMERA_REQUEST_CODE) {
//                        if (isChange) {
//                            files.set(selectCount, file);
//                            imageArr.set(selectCount,imageUrl);
//                        } else {
//                            files.add(file);
//                            imageArr.add(imageUrl);
//                        }
//                    }
//                    publishProductCommentAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onBusinessError(int code, String msg) {
//
//            }
//        },null);
//    }
    private void initTitle() {
        setActionBar(getString(R.string.publish_comments),getString(R.string.publish));
        tvRightText.setTextColor(getResources().getColor(R.color.COLOR_FF1E90FF));
        tvRightText.setOnClickListener(this);
    }

    /**
     * 从图库中选取图片
     */
    private void selectImage(int type) {
        ImagePickerUtils.selectImageAndCrop(PublishProductCommentActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, PublishProductCommentActivity.this);
                uploadImages(selectItem,type, file);
            }
        });
    }
    public void captureImage(int type) {
        ImagePickerUtils.captureImageAndCrop(PublishProductCommentActivity.this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                File file = ImagePickerUtils.getFileByYImagepicker(items, PublishProductCommentActivity.this);
                uploadImages(selectItem,type, file);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right_text:
                publishProductsComments();
                break;
        }
    }

    private void publishProductsComments() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("orderNo", orderNo);
        jsonObject.put("productList", publishList);
        ApiManager.get().getData(Const.PUBLISH_PRODUCT_COMMENTS_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(PublishProductCommentActivity.this,msg);
            }
        },null);
    }
}
