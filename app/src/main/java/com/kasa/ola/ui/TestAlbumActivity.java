package com.kasa.ola.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.ypx.WeChatPresenter;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.adapter.PickerItemAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.data.ICameraExecutor;
import com.ypx.imagepicker.data.IReloadExecutor;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.ProgressSceneEnum;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.views.PickerUiConfig;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestAlbumActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_select_image)
    TextView tvSelectImage;
    @BindView(R.id.tv_camera)
    TextView tvCamera;
//    ArrayList<ImageItem> images = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_album);
        ButterKnife.bind(this);
        tvSelectImage.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_select_image:
                selectImageByYPX();
//                selectImage();
                break;
            case R.id.tv_camera:
                takePhotoByYPX();
//                requestPermissions(TestAlbumActivity.this, new String[]{Manifest.permission.CAMERA}, new BaseActivity.RequestPermissionCallBack() {
//                    @Override
//                    public void granted() {
//                        takePhoto();
//                    }
//
//                    @Override
//                    public void denied() {
//                        ToastUtils.showShortToast(TestAlbumActivity.this, "获取权限失败，正常功能受到影响");
//                    }
//                });
                break;
        }
    }

    private void selectImageByYPX() {
        WeChatPresenter presenter = new WeChatPresenter();
        Set<MimeType> mimeTypes = new HashSet<>() ;
        mimeTypes.add(MimeType.JPEG);
        MultiPickerBuilder builder = ImagePicker.withMulti(presenter)//指定presenter
                .setColumnCount(4)//设置列数
                .mimeTypes(mimeTypes)//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .setSingleCropCutNeedTop(true)
                .showCamera(true)//显示拍照
                .cropSaveInDCIM(false)
//                .cropRectMinMargin(minMarginProgress)
                .cropStyle(CropConfig.STYLE_FILL)
//                .cropGapBackgroundColor(cropGapBackgroundColor)
                .setCropRatio(1, 1);
        builder.crop(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //图片选择回调，主线程
                ImageItem imageItem = items.get(0);
                Uri uri = imageItem.getUri();
                ToastUtils.showLongToast(TestAlbumActivity.this, uri.toString());
            }
        });
    }

    private void takePhotoByYPX() {
        WeChatPresenter weChatPresenter = new WeChatPresenter();
        //配置剪裁属性
        CropConfig cropConfig = new CropConfig();
        cropConfig.setCropRatio(1, 1);//设置剪裁比例
        cropConfig.setCropRectMargin(1);//设置剪裁框间距，单位px
        cropConfig.setCircle(false);//是否圆形剪裁
        cropConfig.setCropStyle( CropConfig.STYLE_FILL);
        ArrayList<ImageItem> resultList = new ArrayList<>();
        ImagePicker.takePhotoAndCrop(this, weChatPresenter, cropConfig, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //剪裁回调，主线程
                ImageItem imageItem = items.get(0);
                Uri uri = imageItem.getUri();
                ToastUtils.showLongToast(TestAlbumActivity.this, uri.toString());
            }
        });
    }

    private void takePhoto() {
        File PICTURES = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //图片名称
        String mFileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        //图片路径
        String mFilePath = PICTURES.getAbsolutePath()+"/"+mFileName;

        File fileDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        String fileName = "/IMG_" + System.currentTimeMillis() + ".jpg";
        //--------------------------
        //--------------------------
        //设置参数
        Uri uri = null;
//		设置保存参数到ContentValues中
        ContentValues contentValues = new ContentValues();
        //设置文件名
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Pictures");
        } else {
            //Android Q以下版本
            contentValues.put(MediaStore.Images.Media.DATA, mFilePath);
        }

        //设置文件类型
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
        //执行insert操作，向系统文件夹中添加文件
        //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//        若生成了uri，则表示该文件添加成功

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1111);
    }

//    private void selectImage() {
//        ImagePicker imagePicker = ImagePicker.getInstance();
//        imagePicker.setImageLoader(new GlideImageLoader());
//        imagePicker.setIToaster(this, new InnerToaster.IToaster() {
//            @Override
//            public void show(String msg) {
//                Toast.makeText(TestAlbumActivity.this, msg, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void show(int resId) {
//                Toast.makeText(TestAlbumActivity.this, resId, Toast.LENGTH_SHORT).show();
//            }
//        });
//        imagePicker.setCropCacheFolder(new File(getExternalCacheDir(),"test/crop"));
//        imagePicker.setMultiMode(false);
//        Integer width = Integer.valueOf("280");
//        Integer height = Integer.valueOf("280");
//        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
//        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
//        imagePicker.setFocusWidth(width);
//        imagePicker.setFocusHeight(height);
//        imagePicker.setOutPutX(Integer.valueOf("800"));
//        imagePicker.setOutPutY(Integer.valueOf("800"));
//        imagePicker.setShowCamera(true);                      //显示拍照按钮
//        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
//        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
//        imagePicker.setSelectLimit(1);              //选中数量限制
//        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
//        if (!imagePicker.isMultiMode()&&images!=null) {
//            images.clear();
//        }
//        Intent intent = new Intent(this, ImageGridActivity.class);
//        intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
//        startActivityForResult(intent, 100);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
//            if (data != null && requestCode == 100) {
//                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                ToastUtils.showLongToast(TestAlbumActivity.this,images.toString());
//            } else {
//                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
