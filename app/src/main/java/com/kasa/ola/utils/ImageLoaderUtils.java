package com.kasa.ola.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kasa.ola.R;

import java.io.File;

/**
 * 图片加载类封装
 * diskCacheStrategy(DiskCacheStrategy strategy).设置缓存策略。
 * DiskCacheStrategy.SOURCE：缓存原始数据，
 * DiskCacheStrategy.RESULT：缓存变换(如缩放、裁剪等)后的资源数据，
 * DiskCacheStrategy.NONE：什么都不缓存，
 * DiskCacheStrategy.ALL：缓存SOURC和RESULT。
 * 默认采用DiskCacheStrategy.RESULT策略，对于download only操作要使用DiskCacheStrategy.SOURCE
 * Created by guan on 2018/7/21.
 */

public class ImageLoaderUtils {

    public static void imageLoadRoundFace(Context context, String url, ImageView imageView) {
        imageLoadRound(context, url, imageView, R.mipmap.face_s_270, true);
    }

    public static void imageLoadCircleFace(Context context, String url, ImageView imageView) {
        imageLoadCircle(context, url, imageView, R.mipmap.face_270, true);
    }

    public static void imageLoadRound(Context context, String url, ImageView imageView, int defaultResId, boolean diskSrc) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(diskSrc ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.DATA)
                    .placeholder(defaultResId)
                    .transform(new CenterCrop(), new GlideRoundTransform(context, 5))
                    .dontTransform()
                    .into(imageView);
        }
    }
    public static void imageLoadRound(Context context, String url, ImageView imageView, int defaultResId, boolean diskSrc,int roundDp) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(diskSrc ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.DATA)
                    .placeholder(defaultResId)
                    .transform(new CenterCrop(), new GlideRoundTransform(context, roundDp))
                    .dontTransform()
                    .into(imageView);
        }
    }
    public static void imageLoadRound1(Context context, String url, ImageView imageView, int defaultResId) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .placeholder(defaultResId)

                    .transform(new CenterCrop(), new GlideRoundTransform(context, 5))
                    .into(imageView);
        }
    }
    public static void imageLoadRound(Context context, String url, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .transform(new CenterCrop(), new GlideRoundTransform(context, 5))
                    .into(imageView);
        }
    }

    public static void imageLoadRound(Context context, String url, ImageView imageView, int roundDp) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .transform(new CenterCrop(), new GlideRoundTransform(context, roundDp))
                    .into(imageView);
        }
    }
    public static void imageLoadRound(Context context, String url, ImageView imageView,int defaultResId, int roundDp) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .placeholder(defaultResId)
                    .transform(new CenterCrop(), new GlideRoundTransform(context, roundDp))
                    .into(imageView);
        }
    }
    public static void imageLoadRoundWithDefault(Context context, String url, ImageView imageView, int roundDp, int defaultResId) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .placeholder(defaultResId)
                    .transform(new CenterCrop(), new GlideRoundTransform(context, roundDp))
                    .into(imageView);
        }
    }

    public static void imageLoadTopRound(Context context, String url, ImageView imageView, int roundDp) {
        if (null != context) {
            CornerTransform transformation = new CornerTransform(context, roundDp);
            //只是绘制左上角和右上角圆角,更改可任意选择要绘制的圆角位置
            transformation.setExceptCorner(false, false, true, true);
            Glide.with(context).
                    asBitmap().
                    load(url).
                    skipMemoryCache(true).
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    transform(transformation).
                    into(imageView);
        }
    }

    public static void imageLoadCircle(Context context, String url, ImageView imageView, int defaultResId, boolean diskSrc) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(diskSrc ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.DATA)
                    .transform(new GlideCircleTransform(context))
                    .into(imageView);
        }
    }
    public static void imageLoadCircle(Context context,String url,int defaultResId, ImageView imageView, boolean diskSrc) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(diskSrc ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.DATA)
                    .placeholder(defaultResId)
                    .transform(new GlideCircleTransform(context))
                    .into(imageView);
        }
    }
    public static void imageLoadCircle(Context context, String url, ImageView imageView, boolean diskSrc) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(diskSrc ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.DATA)
                    .transform(new GlideCircleTransform(context))
                    .into(imageView);
        }
    }

    public static void imageLoadCircle(Context context, String url, SimpleTarget target, boolean diskSrc) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(diskSrc ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.DATA)
                    .transform(new GlideCircleTransform(context))
                    .into(target);
        }
    }

    public static void imageLoad(Context context, String url, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .into(imageView);
        }
    }
    public static void imageLoadSkipMemoryCache(Context context, String url, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imageView);
        }
    }
    public static void imageLoadLargeNumber(Context context, String url, final ImageView imageView) {
        if (null != context) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).dontAnimate();
            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if(resource!=null){
                                imageView.setImageDrawable(resource);
                            }

                        }
                    });
        }
    }

    public static void imageLoad(Context context, String url, ImageView imageView, int defaultResId, boolean diskSrc) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(diskSrc ? DiskCacheStrategy.RESOURCE : DiskCacheStrategy.DATA)
                    .placeholder(defaultResId)
                    .into(imageView);
        }
    }

    public static void imageLoadFile(Context context, File file, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(file)
                    .into(imageView);
        }
    }
    public static void imageLoadFile(Context context, File file, ImageView imageView, int defaultResId) {
        if (null != context) {
            Glide.with(context)
                    .load(file)
                    .placeholder(defaultResId)
                    .into(imageView);
        }
    }
    public static void imageLoadRoundFile(Context context, File file, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(file)
                    .transform(new GlideRoundTransform(context, 5))
                    .into(imageView);
        }
    }

    public static void imageLoadRoundFile(Context context, File file, ImageView imageView, int roundDp) {
        if (null != context) {
            Glide.with(context)
                    .load(file)
                    .transform(new CenterCrop(), new GlideRoundTransform(context, roundDp))
                    .into(imageView);
        }
    }

    public static void imageLoadRoundRes(Context context, int resId, int roundDp, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(resId)
                    .transform(new GlideRoundTransform(context, roundDp))
                    .into(imageView);
        }
    }
    public static void imageLoad(Context context, int resId, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(resId)
                    .into(imageView);
        }
    }
    public static void imageLoadRoundRes(Context context, int resId, int roundDp, int defaultResId, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(resId)
                    .transform(new GlideRoundTransform(context, roundDp))
                    .placeholder(defaultResId)
                    .into(imageView);
        }
    }

    public static void imageLoadCircleRes(Context context, int resId, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(resId)
                    .transform(new GlideCircleTransform(context))
                    .into(imageView);
        }
    }

    public static void imageLoadBlur(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .transform(new BlurTransformation(context))
                .into(imageView);
    }
    public static void clear(Context context,ImageView imageView) {
        Glide.with(context)
                .clear(imageView);
    }

    public static void imageLoudRoundReletiveLayout(Context context, String url, RelativeLayout relativeLayout,int defaultResId,int roundDp){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .placeholder(defaultResId)
                .transform(new GlideRoundTransform(context, roundDp))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(resource);
                        relativeLayout.setBackground(drawable);// 设置背景
                    }
                });
    }
    public static void imageLoadUri(Context context, Uri uri, ImageView imageView) {
        Glide.with(context)
                .load(uri)
                .transform(new BlurTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(imageView);
    }
    public static void imageTrendsLoad(Context context, String url, ImageView imageView) {
        if (null != context) {
            Glide.with(context)
                    .load(url)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();
                            int screenWidth = DisplayUtils.getScreenWidth(context);
//                            if(width>screenWidth){
//                                float multiple = ((float) width)/screenWidth+0.5f;
//                                width = (int) (width/multiple);
//                                height = (int) (height/multiple);
//                            }
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                            params.width = screenWidth;
                            params.height = screenWidth*height/width;
                            imageView.setLayoutParams(params);
                            imageView.setImageDrawable(resource);
                        }
                    });
        }
    }
}
