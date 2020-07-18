package com.kasa.ola.ui.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.adapter.SlidingPageAdapter;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.widget.CustomScrollViewPager;
import com.kasa.ola.widget.slidingtab.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.iv_back_frag)
    ImageView ivBackFrag;
    @BindView(R.id.tv_title_frag)
    TextView tvTitleFrag;
    @BindView(R.id.tv_right_text_frag)
    TextView tvRightTextFrag;
    @BindView(R.id.webProgressBar)
    ProgressBar webProgressBar;
    @BindView(R.id.view_actionbar_frag)
    LinearLayout viewActionbarFrag;
    @BindView(R.id.order_tab)
    PagerSlidingTabStrip orderTab;
    @BindView(R.id.custom_view_pager)
    CustomScrollViewPager customViewPager;
    Unbinder unbinder;
    private LayoutInflater inflater;
//    private String type = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewStatusBar.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        viewStatusBar.setBackgroundColor(Color.parseColor("#000000"));
        setActionBar(rootView,getString(R.string.order));
        ivBackFrag.setVisibility(View.GONE);
        initView();
        return rootView;
    }

    private void initView() {
        setPager();
        setTabs();
    }
    private void setPager() {
        ArrayList<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.all));
        tabList.add(getString(R.string.wait_pay));
        tabList.add(getString(R.string.finish_pay));
        tabList.add(getString(R.string.finished));
//        tabList.add(getString(R.string.after_sale));
        List<Fragment> fragmentList = new ArrayList<>();
        OrderClassifyFragment allFragment = new OrderClassifyFragment();
        Bundle allBundle = new Bundle();
        allBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 0);
//        allBundle.putString(Const.ORDER_TYPE, type);
        allFragment.setArguments(allBundle);
        OrderClassifyFragment payingFragment = new OrderClassifyFragment();
        Bundle payingBundle = new Bundle();
        payingBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 1);
//        payingBundle.putString(Const.ORDER_TYPE, type);
        payingFragment.setArguments(payingBundle);
        OrderClassifyFragment payedFragment = new OrderClassifyFragment();
        Bundle payedBundle = new Bundle();
        payedBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 2);
//        payedBundle.putString(Const.ORDER_TYPE, type);
        payedFragment.setArguments(payedBundle);
        OrderClassifyFragment finishedFragment = new OrderClassifyFragment();
        Bundle finishedBundle = new Bundle();
        finishedBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 3);
//        finishedBundle.putString(Const.ORDER_TYPE, type);
        finishedFragment.setArguments(finishedBundle);
//        ClassifyOrderFragment saleFragment = new ClassifyOrderFragment();
//        Bundle saleBundle = new Bundle();
//        saleBundle.putInt(ClassifyOrderFragment.ORDER_STATUS_KEY, 7);
//        saleFragment.setArguments(saleBundle);
        fragmentList.add(allFragment);
        fragmentList.add(payingFragment);
        fragmentList.add(payedFragment);
        fragmentList.add(finishedFragment);
//        fragmentList.add(saleFragment);
        SlidingPageAdapter mAdapter = new SlidingPageAdapter(getChildFragmentManager(), fragmentList, tabList);
        customViewPager.setAdapter(mAdapter);
        customViewPager.setOffscreenPageLimit(mAdapter.getCacheCount());
        customViewPager.addOnPageChangeListener(this);
    }
//    private void setPager() {
//        ArrayList<String> tabList = new ArrayList<>();
//        tabList.add("VIP商品");
//        tabList.add("商品");
//        List<Fragment> fragmentList = new ArrayList<>();
//        VIPProductOrderFragment vipProductOrderFragment = new VIPProductOrderFragment();
//        ProductOrderFragment productOrderFragment = new ProductOrderFragment();
//        fragmentList.add(vipProductOrderFragment);
//        fragmentList.add(productOrderFragment);
//        SlidingPageAdapter mAdapter = new SlidingPageAdapter(getFragmentManager(), fragmentList, tabList);
//        customViewPager.setAdapter(mAdapter);
//        customViewPager.setOffscreenPageLimit(mAdapter.getCacheCount());
//        customViewPager.addOnPageChangeListener(this);
//    }
    private void setTabs() {
        orderTab.setTextSize(DisplayUtils.sp2px(getContext(), 15));
        orderTab.setTypeface(null, Typeface.BOLD);
        orderTab.setViewPager(customViewPager);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        orderTab.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(final int position) {
        orderTab.onPageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        orderTab.onPageScrollStateChanged(state);
    }
}
