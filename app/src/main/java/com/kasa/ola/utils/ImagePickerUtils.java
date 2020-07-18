package com.kasa.ola.utils;

import android.app.Activity;
import android.content.Context;

import com.kasa.ola.ui.TaskPublishActivity;
import com.kasa.ola.widget.ypx.WeChatPresenter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ImagePickerUtils {
    /**
     * 从图库中选取图片
     */
    public static void selectImageAndCrop(Activity context, OnImagePickCompleteListener onImagePickCompleteListener) {
        WeChatPresenter presenter = new WeChatPresenter();
        Set<MimeType> mimeTypes = new HashSet<>();
        mimeTypes.add(MimeType.JPEG);
        mimeTypes.add(MimeType.PNG);
        MultiPickerBuilder builder = com.ypx.imagepicker.ImagePicker.withMulti(presenter)//指定presenter
                .setColumnCount(4)//设置列数
                .mimeTypes(mimeTypes)//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .setSingleCropCutNeedTop(true)
                .showCamera(false)//显示拍照
                .cropSaveInDCIM(false)
//                .cropRectMinMargin(minMarginProgress)
                .cropStyle(CropConfig.STYLE_FILL)
//                .cropGapBackgroundColor(cropGapBackgroundColor)
                .setCropRatio(1, 1);
        builder.crop(context, onImagePickCompleteListener);
    }
    public static void captureImageAndCrop(Activity context, OnImagePickCompleteListener onImagePickCompleteListener) {
        WeChatPresenter weChatPresenter = new WeChatPresenter();
        //配置剪裁属性
        CropConfig cropConfig = new CropConfig();
        cropConfig.setCropRatio(1, 1);//设置剪裁比例
        cropConfig.setCropRectMargin(0);//设置剪裁框间距，单位px
        cropConfig.setCircle(false);//是否圆形剪裁
        cropConfig.setCropStyle(CropConfig.STYLE_FILL);
        cropConfig.saveInDCIM(false);
        com.ypx.imagepicker.ImagePicker.takePhotoAndCrop(context, weChatPresenter, cropConfig, onImagePickCompleteListener);
    }

    public static File getFileByYImagepicker(ArrayList<ImageItem> items, Context context) {
        File file = null;
        if (items!=null && items.size()==1){
            ImageItem imageItem = items.get(0);
            if (imageItem.getCropUrl() != null && imageItem.getCropUrl().length() > 0) {
                file = new File(imageItem.getCropUrl());
            } else {
                if (imageItem.getUri() != null) {
                    file = FileUtils.uriToFile(imageItem.getUri(), context);
                } else {
                    file = new File(imageItem.path);
                }
            }
        }
        return file;
    }

}
