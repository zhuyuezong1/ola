package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.dialog.SingleBtnCommonDialog;
import com.kasa.ola.ui.adapter.SelectFirstProductAdapter;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectFirstProductActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.business_tab)
    TabLayout businessTab;
    @BindView(R.id.rv_good_product)
    LoadMoreRecyclerView rvGoodProduct;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    private ArrayList<String> tabList;
    private int currentPage = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_first_product);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        SingleBtnCommonDialog.Builder builder = new SingleBtnCommonDialog.Builder(SelectFirstProductActivity.this);
        builder.setTitle("注意")
                .setMessage("该页面仅供测试")
                .setRightButton("确定")
                .setDialogInterface(new SingleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void rightButtonClick(SingleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void initEvent() {
        //测试
        tvTitle.setOnClickListener(this);
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SelectFirstProductActivity.this, 2);
        rvGoodProduct.setLayoutManager(gridLayoutManager);
        rvGoodProduct.setLoadingListener(this);
        rvGoodProduct.setLoadingMoreEnabled(true);

        SelectFirstProductAdapter selectFirstProductAdapter = new SelectFirstProductAdapter(SelectFirstProductActivity.this, null);
        rvGoodProduct.setAdapter(selectFirstProductAdapter);

        View headView = LayoutInflater.from(SelectFirstProductActivity.this).inflate(R.layout.view_select_first_head, rvGoodProduct, false);
        rvGoodProduct.addHeaderView(headView);

        initTab();
    }

    private void initTitle() {
        setStatusBar(R.color.COLOR_FF1677FF);
        setActionBar(getString(R.string.home_select),"");
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.COLOR_FF1677FF));
        ivBack.setImageResource(R.mipmap.return_back);
    }
    private void initTab() {
        tabList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            businessTab.addTab(businessTab.newTab().setText("美食" + i));
            tabList.add("美食" + i);
        }
        for (int i = 0; i < businessTab.getTabCount(); i++) {
            TabLayout.Tab tab = businessTab.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        updateTabTextView(businessTab.getTabAt(businessTab.getSelectedTabPosition()), true);

        businessTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabTextView(tab, true);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabTextView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(SelectFirstProductActivity.this).inflate(R.layout.tab_item, null);
        TextView textView = (TextView) view.findViewById(R.id.tab_item_textview);
        textView.setText(tabList.get(currentPosition));
        return view;
    }

    private void updateTabTextView(TabLayout.Tab tab, boolean isSelect) {
        if (isSelect) {
            //选中加粗
            TextView tabSelect = (TextView) tab.getCustomView().findViewById(R.id.tab_item_textview);
            tabSelect.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tabSelect.setText(tab.getText());
        } else {
            TextView tabUnSelect = (TextView) tab.getCustomView().findViewById(R.id.tab_item_textview);
            tabUnSelect.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tabUnSelect.setText(tab.getText());
        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title:
                Intent intent = new Intent(SelectFirstProductActivity.this, SelectFirstProductDetailsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
