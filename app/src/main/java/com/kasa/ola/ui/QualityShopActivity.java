package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.animation.ArgbEvaluatorCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.HomeTopBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProductVerticalAdapter;
import com.kasa.ola.ui.adapter.ShopListAdapter;
import com.kasa.ola.ui.listener.AppBarStateChangeListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QualityShopActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {

    @BindView(R.id.rv_quality)
    LoadMoreRecyclerView rvQuality;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    private int currentPage = 1;
//    private List<ProductBean> productBeans = new ArrayList<>();
//    private ProductVerticalAdapter productVerticalAdapter;
    private int topHeight;
    private List<HomeTopBean.HomeQualityBean> qualityShopBeans = new ArrayList<>();
    private ShopListAdapter shopListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_shop);
        ButterKnife.bind(this);
        initView();
        loadPage(true);
    }

    private void initView() {
        ivBack.setImageResource(R.mipmap.back_icon);
        ivBack.setOnClickListener(this);
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        tvTitle.setText(getString(R.string.quality_select_product));
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.COLOR_FF1677FF));
        ImmersionBar.with(this).titleBar(R.id.view_actionbar)
                .fitsSystemWindows(true)
                .navigationBarColor(android.R.color.black)
                .autoDarkModeEnable(true)
                .autoStatusBarDarkModeEnable(true, 0.2f)
                .init();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(QualityShopActivity.this);
        rvQuality.setLayoutManager(linearLayoutManager);
        rvQuality.setLoadingListener(this);
        rvQuality.setLoadingMoreEnabled(true);

        shopListAdapter = new ShopListAdapter(QualityShopActivity.this, qualityShopBeans,1);
        shopListAdapter.setOnCheckMoreListener(new ShopListAdapter.OnCheckMoreListener() {
            @Override
            public void checkMore(int position) {
                Intent intent = new Intent(QualityShopActivity.this,StoreDetailsActivity.class);
                intent.putExtra(Const.SHOP_ID, qualityShopBeans.get(position).getSuppliersID());
                startActivity(intent);
            }
        });
        rvQuality.setAdapter(shopListAdapter);

        View topView = LayoutInflater.from(this).inflate(R.layout.head_quality_shop, rvQuality, false);
        topHeight = DisplayUtils.dip2px(this, 145)- ImmersionBar.getStatusBarHeight(this);
        rvQuality.addHeaderView(topView);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        rvQuality.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;
                if (totalDy < 0) {
                    totalDy = 0;
                }
                Integer barColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / topHeight, getResources().getColor(R.color.white_transparent), Color.WHITE);
                Integer textColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / topHeight, Color.WHITE, Color.BLACK);
                if (totalDy < topHeight) {
                    float alpha = (float) totalDy / topHeight;
                    if (totalDy > 0) {
                        viewActionbar.setAlpha(alpha);
                        ivBack.setImageResource(R.mipmap.return_icon);
                        tvTitle.setTextColor(getResources().getColor(R.color.textColor_actionBar_title));
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    } else if (totalDy == 0) {
                        viewActionbar.setAlpha(1);
                        ivBack.setImageResource(R.mipmap.back_icon);
                        tvTitle.setTextColor(getResources().getColor(R.color.white));
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    ImmersionBar.with(QualityShopActivity.this)
                            .statusBarDarkFont(false)
                            .init();
                } else {
                    viewActionbar.setAlpha(1);
                    ivBack.setImageResource(R.mipmap.return_icon);
                    tvTitle.setTextColor(getResources().getColor(R.color.textColor_actionBar_title));
                    viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    ImmersionBar.with(QualityShopActivity.this)
                            .statusBarDarkFont(true)
                            .init();
                }

            }
        });


    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId())?"":LoginHandler.get().getUserId());
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        ApiManager.get().getData(Const.GET_QUALITY_SHOP_LIST, jsonObject, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("suppliersList");
                    if (!isLoadMore) {
                        qualityShopBeans.clear();
                        shopListAdapter.notifyDataSetChanged();
                    }
                    List<HomeTopBean.HomeQualityBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<HomeTopBean.HomeQualityBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        qualityShopBeans.addAll(list);
                        shopListAdapter.notifyDataSetChanged();
                        rvQuality.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(QualityShopActivity.this, msg);
            }
        }, null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
