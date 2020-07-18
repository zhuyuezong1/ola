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
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.adapter.SlidingPageAdapter;
import com.kasa.ola.ui.fragment.MemberFragment;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.widget.slidingtab.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTeamActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.pager_tab)
    PagerSlidingTabStrip pagerTab;
    @BindView(R.id.pager)
    ViewPager pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myteam);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.my_extension), "");
        pagerTab = findViewById(R.id.pager_tab);
        pager = findViewById(R.id.pager);
        setPager();
        setTabs();
    }
    private void setTabs() {
        pagerTab.setTextSize(DisplayUtils.sp2px(this, 13));
        pagerTab.setTypeface(null, Typeface.NORMAL);
        pagerTab.setViewPager(pager);
    }

    private void setPager() {
        String memberLvl = LoginHandler.get().getMyInfo().optString("memberLvl");
//        if (memberLvl.equals("1")){
//
//        }else if (memberLvl.equals("2")){
//
//        }
        ArrayList<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.one_level));
        tabList.add(getString(R.string.two_level));
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MemberFragment memberFragment = new MemberFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Const.MEMBER_STATUS_KEY, i + 1);
            memberFragment.setArguments(bundle);
            fragmentList.add(memberFragment);
        }

        SlidingPageAdapter mAdapter = new SlidingPageAdapter(getSupportFragmentManager(), fragmentList, tabList);
        pager.setAdapter(mAdapter);
        pager.setOffscreenPageLimit(mAdapter.getCacheCount());
        pager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        pagerTab.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        pagerTab.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        pagerTab.onPageScrollStateChanged(state);
    }
}
