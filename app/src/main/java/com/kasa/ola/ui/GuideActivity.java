package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.SPUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;

public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.banner_guide_background)
    BGABanner bannerGuideBackground;
    @BindView(R.id.btn_guide_enter)
    Button btnGuideEnter;
    @BindView(R.id.rl_first)
    RelativeLayout rlFirst;
    private List<String> images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        setTranslucentStatus();
        images = (List<String>) getIntent().getSerializableExtra(Const.GUIDE_LIST_TAG);
        firstEnter();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        bannerGuideBackground.setBackgroundResource(android.R.color.white);
    }
    private void firstEnter() {
//        SPUtils.putString(GuideActivity.this, Const.ISFIRST_USE,"0");
        bannerGuideBackground.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter,  R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
            }
        });
        if (images==null || images.size()==0){
            // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
//            BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
//            // 设置数据源
//            bannerGuideBackground.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
//                    R.mipmap.guide_page1,
//                    R.mipmap.guide_page2,
//                    R.mipmap.guide_page3);
        }else {
            bannerGuideBackground.setAdapter(new BGABanner.Adapter<ImageView, String>() {
                @Override
                public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                    Glide.with(GuideActivity.this)
                            .load(model)
                            .centerCrop()
                            .dontAnimate()
                            .into(itemView);
                }
            });
            bannerGuideBackground.setData(images, Arrays.asList("", "", ""));
        }
    }
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
