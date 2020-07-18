package com.kasa.ola.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProductVerticalAdapter;
import com.kasa.ola.ui.listener.AppBarStateChangeListener;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QualityActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.coll_toolbar)
    CollapsingToolbarLayout collToolbar;
    @BindView(R.id.rv_quality)
    LoadMoreRecyclerView rvQuality;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.iv_back)
//    ImageView ivBack;
//    @BindView(R.id.tv_title)
//    TextView tvTitle;
//    @BindView(R.id.tv_right_text)
//    TextView tvRightText;
//    @BindView(R.id.view_actionbar)
//    LinearLayout viewActionbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    private int currentPage = 1;
    private List<ProductBean> productBeans = new ArrayList<>();
    private ProductVerticalAdapter productVerticalAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality);
        ButterKnife.bind(this);
        initView();
        loadPage(true);
    }

    private void initView() {
        setSupportActionBar(toolbar);
        collToolbar.setTitle("精选商品");
        collToolbar.setExpandedTitleColor(Color.WHITE);//设置还没收缩时状态下字体颜色
        collToolbar.setCollapsedTitleTextColor(Color.BLACK);//设置收缩后Toolbar上字体的颜色
        if (collToolbar != null) {
            //设置隐藏图片时候ToolBar的颜色
            collToolbar.setContentScrimColor(Color.WHITE);
            //设置工具栏标题
            collToolbar.setTitle("编程是一种信仰");
            collToolbar.setTitleEnabled(true);
            collToolbar.setCollapsedTitleGravity(Gravity.CENTER);
        }
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d("STATE", state.name());
                if (state == State.EXPANDED) {
                    ToastUtils.showLongToast(QualityActivity.this,"展开");
                    toolbar.setNavigationIcon(R.mipmap.return_back);

                } else if (state == State.COLLAPSED) {
                    ToastUtils.showLongToast(QualityActivity.this,"折叠状态");
                    toolbar.setNavigationIcon(R.mipmap.return_icon);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        tvTitle.setTextColor(getColor(R.color.black));
                    }
                } else {

                    //中间状态
//                    Toast.makeText(getActivity(),"中间状态",Toast.LENGTH_SHORT).show();

                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(QualityActivity.this);
        rvQuality.setLayoutManager(linearLayoutManager);
        rvQuality.setLoadingListener(this);
        rvQuality.setLoadingMoreEnabled(true);
        productVerticalAdapter = new ProductVerticalAdapter(QualityActivity.this, productBeans);
        rvQuality.setAdapter(productVerticalAdapter);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE / 2);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        jsonObject.put("otherType", "6");
        ApiManager.get().getData(Const.GET_PRODUCT_LIST_TAG, jsonObject, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        productVerticalAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        productVerticalAdapter.notifyDataSetChanged();
                        rvQuality.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
//                    if (!isLoadMore) {
//                        if (list != null && list.size() > 0) {
//                            rvHotSale.setVisibility(View.VISIBLE);
//                        } else {
//                            rvHotSale.setVisibility(View.GONE);
//                        }
//                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(QualityActivity.this, msg);
            }
        }, null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
}
