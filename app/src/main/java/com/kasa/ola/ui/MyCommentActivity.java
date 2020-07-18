package com.kasa.ola.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.ui.adapter.MyCommentsAdapter;
import com.kasa.ola.ui.adapter.SlidingPageAdapter;
import com.kasa.ola.ui.fragment.CommentFragment;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.widget.CustomScrollViewPager;
import com.kasa.ola.widget.slidingtab.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCommentActivity extends BaseActivity implements ViewPager.OnPageChangeListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.comment_tab)
    PagerSlidingTabStrip commentTab;
    @BindView(R.id.custom_view_pager)
    CustomScrollViewPager customViewPager;
    private int type = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        setPager();
        setTabs();
        customViewPager.setCurrentItem(type);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        rvMyComments.setLayoutManager(linearLayoutManager);
//
//        MyCommentsAdapter myCommentsAdapter = new MyCommentsAdapter(MyCommentActivity.this, null);
//        rvMyComments.setAdapter(myCommentsAdapter);

    }
    private void setPager() {
        ArrayList<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.product_comment));
        tabList.add(getString(R.string.education_comment));
//        tabList.add(getString(R.string.after_sale));
        List<Fragment> fragmentList = new ArrayList<>();
        CommentFragment productFragment = new CommentFragment();
        Bundle allBundle = new Bundle();
        allBundle.putInt(CommentFragment.COMMENT_TYPE_KEY, 0);
        productFragment.setArguments(allBundle);
        CommentFragment educationFragment = new CommentFragment();
        Bundle payingBundle = new Bundle();
        payingBundle.putInt(CommentFragment.COMMENT_TYPE_KEY, 1);
        educationFragment.setArguments(payingBundle);
        fragmentList.add(productFragment);
        fragmentList.add(educationFragment);
        SlidingPageAdapter mAdapter = new SlidingPageAdapter(getSupportFragmentManager(), fragmentList, tabList);
        customViewPager.setAdapter(mAdapter);
        customViewPager.setOffscreenPageLimit(mAdapter.getCacheCount());
        customViewPager.addOnPageChangeListener(this);
    }
    private void setTabs() {
        commentTab.setTextSize(DisplayUtils.sp2px(MyCommentActivity.this, 15));
        commentTab.setTypeface(null, Typeface.BOLD);
        commentTab.setViewPager(customViewPager);
    }
    private void initTitle() {
        setActionBar(getString(R.string.my_comments), "");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        commentTab.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        commentTab.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        commentTab.onPageScrollStateChanged(state);
    }
}
