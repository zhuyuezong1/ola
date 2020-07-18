package com.kasa.ola.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.widget.xbanner.XBanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_task_title)
    TextView tvTaskTitle;
    @BindView(R.id.tv_task_desc)
    TextView tvTaskDesc;
    @BindView(R.id.tv_back_value)
    TextView tvBackValue;
    @BindView(R.id.tv_notice_tips)
    TextView tvNoticeTips;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.tv_contact_mobile)
    TextView tvContactMobile;
    @BindView(R.id.tv_detail_address)
    TextView tvDetailAddress;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.ll_special)
    LinearLayout llSpecial;
    @BindView(R.id.iv_phone)
    ImageView ivPhone;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.iv_nav)
    ImageView ivNav;
    @BindView(R.id.ll_nav)
    LinearLayout llNav;
    @BindView(R.id.sv_task_detail)
    NestedScrollView svTaskDetail;
    @BindView(R.id.task_banner)
    XBanner taskBanner;
    private int dy;
    private int mBannerHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        ivPhone.setOnClickListener(this);
        ivNav.setOnClickListener(this);
    }

    private void initView() {
        ivBack.setImageResource(R.mipmap.back_icon);
        ivBack.setOnClickListener(this);
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        ImmersionBar.with(this).titleBar(R.id.view_actionbar)
                .fitsSystemWindows(true)
                .navigationBarColor(android.R.color.black)
                .autoDarkModeEnable(true)
                .autoStatusBarDarkModeEnable(true, 0.2f)
                .init();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) taskBanner.getLayoutParams();
        int screenWidth = DisplayUtils.getScreenWidth(TaskDetailsActivity.this);
        lp.width = screenWidth;
        lp.height = screenWidth*260/375;

        taskBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                if (model instanceof String) {
                    ImageView imageView = (ImageView) view;
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ImageLoaderUtils.imageLoad(TaskDetailsActivity.this, (String) model, imageView);
                }
            }
        });

        mBannerHeight = screenWidth*260/375-DisplayUtils.getStatusBarHeight2(TaskDetailsActivity.this)-DisplayUtils.dip2px(TaskDetailsActivity.this,45);
        svTaskDetail.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                dy += scrollY - oldScrollY;
                if (dy < 0) {
                    dy = 0;
                }
                if (dy < mBannerHeight) {
                    float alpha = (float) dy / mBannerHeight;
                    if (dy > 0) {
                        viewActionbar.setAlpha(alpha);
                        ivBack.setImageResource(R.mipmap.return_icon);
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    } else if (dy == 0) {
                        viewActionbar.setAlpha(1);
                        ivBack.setImageResource(R.mipmap.back_icon);
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    ImmersionBar.with(TaskDetailsActivity.this)
                            .statusBarDarkFont(false)
                            .init();
                } else {
                    viewActionbar.setAlpha(1);
                    ivBack.setImageResource(R.mipmap.return_icon);
                    viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    ImmersionBar.with(TaskDetailsActivity.this)
                            .statusBarDarkFont(true)
                            .init();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_phone:

                break;
            case R.id.iv_nav:

                break;
        }
    }
}
