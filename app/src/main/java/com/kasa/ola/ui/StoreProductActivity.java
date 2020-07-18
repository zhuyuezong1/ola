package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.ui.fragment.StoreProductFrament;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreProductActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.tv_add_product)
    TextView tvAddProduct;
    @BindView(R.id.rb_all_product)
    RadioButton rbAllProduct;
    @BindView(R.id.rb_checking_product)
    RadioButton rbCheckingProduct;
    @BindView(R.id.rb_checked_product)
    RadioButton rbCheckedProduct;
    @BindView(R.id.rb_undercarriage_product)
    RadioButton rbUndercarriageProduct;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.rg_store_product)
    RadioGroup rgStoreProduct;
    private List<Fragment> fragmentList = new ArrayList<>();
    private Fragment currentFragment;
    private static int currentIndex = 0;//tab切换

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product);
        ButterKnife.bind(this);
        initTitle();
        initEvent();
        initFragment();
        switchFragment(0);
    }

    private void initTitle() {
        setActionBar(getString(R.string.shop_products), "");
    }

    private void initEvent() {
        rgStoreProduct.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_all_product:
                        switchFragment(0);
                        currentIndex = 0;
                        break;
                    case R.id.rb_checking_product:
                        switchFragment(1);
                        currentIndex = 1;
                        break;
                    case R.id.rb_checked_product:
                        switchFragment(2);
                        currentIndex = 2;
                        break;
                    case R.id.rb_undercarriage_product:
                        switchFragment(3);
                        currentIndex = 3;
                        break;
                }
            }
        });
        tvAddProduct.setOnClickListener(this);
    }

    private void initFragment() {
        StoreProductFrament allFragment = new StoreProductFrament();
        Bundle allBundle = new Bundle();
        allBundle.putInt(StoreProductFrament.STORE_PRODUCT_STATUS_KEY, 0);
        allFragment.setArguments(allBundle);
        StoreProductFrament checkingFragment = new StoreProductFrament();
        Bundle checkingBundle = new Bundle();
        checkingBundle.putInt(StoreProductFrament.STORE_PRODUCT_STATUS_KEY, 1);
        checkingFragment.setArguments(checkingBundle);
        StoreProductFrament checkedFragment = new StoreProductFrament();
        Bundle checkBundle = new Bundle();
        checkBundle.putInt(StoreProductFrament.STORE_PRODUCT_STATUS_KEY, 2);
        checkedFragment.setArguments(checkBundle);
        StoreProductFrament undercarriageFragment = new StoreProductFrament();
        Bundle undercarriageBundle = new Bundle();
        undercarriageBundle.putInt(StoreProductFrament.STORE_PRODUCT_STATUS_KEY, 3);
        undercarriageFragment.setArguments(undercarriageBundle);

        fragmentList.add(allFragment);
        fragmentList.add(checkingFragment);
        fragmentList.add(checkedFragment);
        fragmentList.add(undercarriageFragment);
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
        switch (v.getId()){
            case R.id.tv_add_product:
                Intent intent = new Intent(StoreProductActivity.this, StoreAddProductActivity.class);
                startActivity(intent);
                break;
        }
    }
}
