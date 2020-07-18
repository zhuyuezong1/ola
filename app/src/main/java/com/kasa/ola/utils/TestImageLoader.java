package com.kasa.ola.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kasa.ola.R;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;
/**
 * Created by yangc on 2017/9/4.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */

public class TestImageLoader implements IZoomMediaLoader {


    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
        Glide.with(context).asBitmap()
                .load(path)
                .error(R.drawable.ic_default_image)
              //  .placeholder(android.R.color.darker_gray)
                .fitCenter()
                .centerInside()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        simpleTarget.onLoadFailed(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        simpleTarget.onResourceReady();
                        return false;
                    }

//
//                    @Override
//                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                        simpleTarget.onLoadFailed(null);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        simpleTarget.onResourceReady();
//                        return false;
//                    }
                })
                .into(imageView);
    }

    @Override
    public void displayGifImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
        Glide.with(context).asGif()
                .load(path)
                //可以解决gif比较几种时 ，加载过慢  //DiskCacheStrategy.NONE
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_default_image)
                .dontAnimate() //去掉显示动画
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        simpleTarget.onLoadFailed(null);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        simpleTarget.onResourceReady();
                        return false;
                    }
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<String> target, boolean isFirstResource) {
//                        simpleTarget.onLoadFailed(null);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(String resource, Object model, Target<String> target, DataSource dataSource, boolean isFirstResource) {
//                        simpleTarget.onResourceReady();
//                        return false;
//                    }

//                    @Override
//                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
//                        simpleTarget.onResourceReady();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        simpleTarget.onLoadFailed(null);
//                        return false;
//                    }
                })
                .into(imageView);
    }
    @Override
    public void onStop(@NonNull Fragment context) {
          Glide.with(context).onStop();

    }

    @Override
    public void clearMemory(@NonNull Context c) {
             Glide.get(c).clearMemory();
    }
}
