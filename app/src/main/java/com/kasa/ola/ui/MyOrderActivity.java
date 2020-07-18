package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.adapter.SlidingPageAdapter;
import com.kasa.ola.ui.fragment.OrderClassifyFragment;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.widget.CustomScrollViewPager;
import com.kasa.ola.widget.slidingtab.PagerSlidingTabStrip;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.order_tab)
    PagerSlidingTabStrip orderTab;
    @BindView(R.id.custom_view_pager)
    CustomScrollViewPager customViewPager;
    private int type;
    private int enterType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        setActionBar(getString(R.string.order),"");
        Intent intent = getIntent();
        type = intent.getIntExtra(Const.ORDER_TAG,0);
        enterType = intent.getIntExtra(Const.ORDER_ENTER_TYPE, 0);
        ivBack.setOnClickListener(this);
        initView();
    }

    private void initView() {
        setPager();
        setTabs();
        customViewPager.setCurrentItem(type);
    }
    private void setPager() {
        ArrayList<String> tabList = new ArrayList<>();
        tabList.add(getString(R.string.all));
        tabList.add(getString(R.string.can_use));
        tabList.add(getString(R.string.wait_pay));
        tabList.add(getString(R.string.wait_send));
        tabList.add(getString(R.string.wait_for_accept));
        tabList.add(getString(R.string.wait_for_discuss));
//        tabList.add(getString(R.string.after_sale));
        List<Fragment> fragmentList = new ArrayList<>();
        OrderClassifyFragment allFragment = new OrderClassifyFragment();
        Bundle allBundle = new Bundle();
        allBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 0);
//        allBundle.putString(Const.ORDER_TYPE, type);
        allFragment.setArguments(allBundle);
        OrderClassifyFragment canUseFragment = new OrderClassifyFragment();
        Bundle canUseBundle = new Bundle();
        canUseBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 7);
        canUseFragment.setArguments(canUseBundle);
        OrderClassifyFragment payingFragment = new OrderClassifyFragment();
        Bundle payingBundle = new Bundle();
        payingBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 1);
//        payingBundle.putString(Const.ORDER_TYPE, type);
        payingFragment.setArguments(payingBundle);
        OrderClassifyFragment waitSendFragment = new OrderClassifyFragment();
        Bundle payedBundle = new Bundle();
        payedBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 2);
//        payedBundle.putString(Const.ORDER_TYPE, type);
        waitSendFragment.setArguments(payedBundle);
        OrderClassifyFragment waitAcceptFragment = new OrderClassifyFragment();
        Bundle finishedBundle = new Bundle();
        finishedBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 3);
//        finishedBundle.putString(Const.ORDER_TYPE, type);
        waitAcceptFragment.setArguments(finishedBundle);
        OrderClassifyFragment waitDiscussFragment = new OrderClassifyFragment();
        Bundle saleBundle = new Bundle();
        saleBundle.putInt(OrderClassifyFragment.ORDER_STATUS_KEY, 4);
        waitDiscussFragment.setArguments(saleBundle);

        fragmentList.add(allFragment);
        fragmentList.add(canUseFragment);
        fragmentList.add(payingFragment);
        fragmentList.add(waitSendFragment);
        fragmentList.add(waitAcceptFragment);
        fragmentList.add(waitDiscussFragment);
        SlidingPageAdapter mAdapter = new SlidingPageAdapter(getSupportFragmentManager(), fragmentList, tabList);
        customViewPager.setAdapter(mAdapter);
        customViewPager.setOffscreenPageLimit(mAdapter.getCacheCount());
        customViewPager.addOnPageChangeListener(this);
    }
    private void setTabs() {
        orderTab.setTextSize(DisplayUtils.sp2px(MyOrderActivity.this, 15));
        orderTab.setTypeface(null, Typeface.BOLD);
        orderTab.setViewPager(customViewPager);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                if (enterType==3 | enterType==1){
                    EventBus.getDefault().post(new EventCenter.HomeSwitch(4));
                    EventBus.getDefault().post(new EventCenter.EventBean());
                    ActivityUtils.finishTopActivity(MainActivity.class);
                }else if (enterType==0){
                    finish();
                }else if (enterType == 2){
                    EventBus.getDefault().post(new EventCenter.EventBean());
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (enterType==3 | enterType==1){
            EventBus.getDefault().post(new EventCenter.HomeSwitch(4));
            EventBus.getDefault().post(new EventCenter.EventBean());
            ActivityUtils.finishTopActivity(MainActivity.class);
        }else if (enterType==0){
            finish();
        }else if (enterType == 2){
            EventBus.getDefault().post(new EventCenter.EventBean());
            finish();
        }
    }
}
