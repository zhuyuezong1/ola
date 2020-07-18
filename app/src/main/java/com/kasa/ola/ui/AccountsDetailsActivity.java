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
import com.kasa.ola.ui.adapter.SlidingPageAdapter;
import com.kasa.ola.ui.fragment.DetailsFragment;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.widget.slidingtab.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountsDetailsActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

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
        setContentView(R.layout.activity_accounts_details);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initView() {
        setPager();
        setTabs();
    }

    private void initTitle() {
        setActionBar(getString(R.string.account_detail),"");
    }
    private void setTabs() {
        pagerTab.setTextSize(DisplayUtils.sp2px(this, 13));
        pagerTab.setTypeface(null, Typeface.NORMAL);
        pagerTab.setViewPager(pager);
    }
    private void setPager() {
        ArrayList<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.commission_details));
        tabList.add(getString(R.string.vacancies_record));
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Const.DETAIL_TYPE,i);
            detailsFragment.setArguments(bundle);
            fragmentList.add(detailsFragment);
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
