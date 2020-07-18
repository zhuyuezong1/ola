package com.kasa.ola.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.adapter.SlidingPageAdapter;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.widget.slidingtab.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProductOrderFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.order_tab)
    PagerSlidingTabStrip orderTab;
    @BindView(R.id.vp_container)
    ViewPager vpContainer;
    Unbinder unbinder;
    private String type = "0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_normal_order, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        setPager();
        setTabs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private void setTabs() {
        orderTab.setTextSize(DisplayUtils.sp2px(getContext(), 13));
        orderTab.setTypeface(null, Typeface.NORMAL);
        orderTab.setViewPager(vpContainer);
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
        allBundle.putString(Const.ORDER_TYPE, type);
        allFragment.setArguments(allBundle);
        OrderClassifyFragment payingFragment = new OrderClassifyFragment();
        Bundle payingBundle = new Bundle();
        payingBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 1);
        payingBundle.putString(Const.ORDER_TYPE, type);
        payingFragment.setArguments(payingBundle);
        OrderClassifyFragment payedFragment = new OrderClassifyFragment();
        Bundle payedBundle = new Bundle();
        payedBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 2);
        payedBundle.putString(Const.ORDER_TYPE, type);
        payedFragment.setArguments(payedBundle);
        OrderClassifyFragment finishedFragment = new OrderClassifyFragment();
        Bundle finishedBundle = new Bundle();
        finishedBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 3);
        finishedBundle.putString(Const.ORDER_TYPE, type);
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
        vpContainer.setAdapter(mAdapter);
        vpContainer.setOffscreenPageLimit(mAdapter.getCacheCount());
        vpContainer.addOnPageChangeListener(this);
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
