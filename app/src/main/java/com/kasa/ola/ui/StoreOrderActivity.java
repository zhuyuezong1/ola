package com.kasa.ola.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.fragment.StoreOrderFrament;
import com.kasa.ola.ui.popwindow.CornerBtnFilterPopWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreOrderActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_uncheck)
    RadioButton rbUncheck;
    @BindView(R.id.rb_checked)
    RadioButton rbChecked;
    @BindView(R.id.rg_filter)
    RadioGroup rgFilter;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.bg_view)
    View bgView;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Fragment currentFragment;
    private static int currentIndex = 0;//tab切换
    private int type;
    private long startTime = 0;
    private long endTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_order);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra(Const.STORE_ORDER_TAG, 0);
        startTime = intent.getLongExtra(Const.START_TIME, 0);
        endTime = intent.getLongExtra(Const.END_TIME, 0);
        initTitle();
        initEvent();
        initFragment();
        if (type == 0) {
            rbAll.setChecked(true);
        } else if (type == 1) {
            rbUncheck.setChecked(true);
        } else if (type == 2) {
            rbChecked.setChecked(true);
        }
    }

    private void initTitle() {
        setActionBar(getString(R.string.order), "");
    }

    private void initFragment() {
        StoreOrderFrament allFragment = new StoreOrderFrament();
        Bundle allBundle = new Bundle();
        allBundle.putInt(StoreOrderFrament.STORE_ORDER_STATUS_KEY, 0);
        allFragment.setArguments(allBundle);
        StoreOrderFrament uncheckFragment = new StoreOrderFrament();
        Bundle uncheckBundle = new Bundle();
        uncheckBundle.putInt(StoreOrderFrament.STORE_ORDER_STATUS_KEY, 1);
        uncheckFragment.setArguments(uncheckBundle);
        StoreOrderFrament checkFragment = new StoreOrderFrament();
        Bundle checkBundle = new Bundle();
        checkBundle.putInt(StoreOrderFrament.STORE_ORDER_STATUS_KEY, 2);
        checkFragment.setArguments(checkBundle);

        fragmentList.add(allFragment);
        fragmentList.add(uncheckFragment);
        fragmentList.add(checkFragment);
    }

    private void initEvent() {
        rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = group.findViewById(checkedId);
                if (!button.isChecked()) {
                    return;
                }
                switch (checkedId) {
                    case R.id.rb_all:
                        switchFragment(0);
                        currentIndex = 0;
                        break;
                    case R.id.rb_uncheck:
                        switchFragment(1);
                        currentIndex = 1;
                        break;
                    case R.id.rb_checked:
                        switchFragment(2);
                        currentIndex = 2;
                        break;
                }
            }
        });
        tvFilter.setOnClickListener(this);
    }

    public void switchFragment(int whichFragment) {
        Fragment fragment = fragmentList.get(whichFragment);
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (currentFragment != null) {
                    transaction.hide(currentFragment).show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (currentFragment != null) {
                    transaction.hide(currentFragment).add(R.id.fl_container, fragment);
                } else {
                    transaction.add(R.id.fl_container, fragment);
                }
            }
            currentFragment = fragment;
            transaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_filter:
                showDatePop(tvFilter);
                break;
        }
    }

    private void showDatePop(View view) {

        CornerBtnFilterPopWindow filterPopWindow = new CornerBtnFilterPopWindow(StoreOrderActivity.this, startTime, endTime);
        filterPopWindow.setFilterListener(new CornerBtnFilterPopWindow.FilterListener() {
            @Override
            public void confirmClick(long startTime1, long endTime1) {
                startTime = startTime1;
                endTime = endTime1;
                EventBus.getDefault().post(new EventCenter.TimePost(startTime,endTime));
            }

            @Override
            public void resetClick() {
                startTime = 0;
                endTime = 0;
                EventBus.getDefault().post(new EventCenter.TimePost(startTime,endTime));
            }
        });
        filterPopWindow.showAsDropDown(view);
        bgView.setVisibility(View.VISIBLE);
        ObjectAnimator inAnimator = ObjectAnimator.ofFloat(bgView, "alpha", 0f, 1f);
        inAnimator.setDuration(300);
        inAnimator.start();
        filterPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgView.setVisibility(View.VISIBLE);
                ObjectAnimator outAnimator = ObjectAnimator.ofFloat(bgView, "alpha", bgView.getAlpha(), 0f);
                outAnimator.setDuration(300);
                outAnimator.start();
                outAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bgView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });

    }

}
