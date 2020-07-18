package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.animation.ArgbEvaluatorCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
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
import com.kasa.ola.ui.adapter.ProductSpecialAdapter;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductSpecialActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rv_product)
    LoadMoreRecyclerView rvProduct;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.ll_special)
    LinearLayout llSpecial;
    private int mBannerHeight;
    private int enterType;//1：9.9专区，5：包邮专区,2:高价特返,3:抵扣专场,4:团购专场
    private String title = "";
    private int currentPage = 1;
    private ProductSpecialAdapter productSpecialAdapter;
    private List<ProductBean> productBeans = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_special);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        enterType = intent.getIntExtra(Const.SPECIAL_ENTER_TYPE, 0);
        initView();
    }

    private void initView() {
        ivBack.setImageResource(R.mipmap.back_icon);
        ivBack.setOnClickListener(this);
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        if (enterType == 1) {
            title = getString(R.string.low_price_special);
            llSpecial.setBackgroundResource(R.mipmap.low_special_background);
        } else if (enterType == 5) {
            title = getString(R.string.free_shipping_special);
            llSpecial.setBackgroundResource(R.mipmap.free_shipping_background);
        }else if (enterType == 3) {
            title = getString(R.string.deduct_special);
            llSpecial.setBackgroundResource(R.mipmap.red_packet_deduct_special_background);
        }else if (enterType == 2) {
            title = getString(R.string.high_back_low_cost);
            llSpecial.setBackgroundResource(R.mipmap.high_back_background);
        }else if (enterType == 4) {
            title = getString(R.string.group_buy_special);
            llSpecial.setBackgroundResource(R.mipmap.group_buy_background);
        }
        tvTitle.setText(title);

        ImmersionBar.with(this).titleBar(R.id.view_actionbar)
                .fitsSystemWindows(true)
                .navigationBarColor(android.R.color.black)
                .autoDarkModeEnable(true)
                .autoStatusBarDarkModeEnable(true, 0.2f)
                .init();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvProduct.setLayoutManager(linearLayoutManager);
        productSpecialAdapter = new ProductSpecialAdapter(ProductSpecialActivity.this, productBeans);
        rvProduct.setAdapter(productSpecialAdapter);


        View bannerView = LayoutInflater.from(this).inflate(R.layout.item_special_banner, rvProduct, false);
        ImageView mIvBanner = bannerView.findViewById(R.id.iv_banner);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mIvBanner.getLayoutParams();
        mBannerHeight = DisplayUtils.dip2px(this, 260)
                - ImmersionBar.getStatusBarHeight(this);
        if (enterType==1){
            mIvBanner.setImageResource(R.mipmap.low_special_banner);
        }else if (enterType==5){
            mIvBanner.setImageResource(R.mipmap.free_shipping_banner);
        }else if (enterType==3){
            mIvBanner.setImageResource(R.mipmap.red_packet_deduct_special_banner);
        }else if (enterType==2){
            mIvBanner.setImageResource(R.mipmap.high_back_banner);
//            lp.height = DisplayUtils.dip2px(ProductSpecialActivity.this,280);
//            mBannerHeight = DisplayUtils.dip2px(this, 280)
//                    - ImmersionBar.getStatusBarHeight(this);
        }else if (enterType==4){
            mIvBanner.setImageResource(R.mipmap.groupbuying);
//            lp.height = DisplayUtils.dip2px(ProductSpecialActivity.this,500);
//            mBannerHeight = DisplayUtils.dip2px(this, 500)
//                    - ImmersionBar.getStatusBarHeight(this);
        }
        rvProduct.addHeaderView(bannerView);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        rvProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                Integer barColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / mBannerHeight, getResources().getColor(R.color.white_transparent), Color.WHITE);
                Integer textColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / mBannerHeight, Color.WHITE, Color.BLACK);
                if (totalDy < mBannerHeight) {
                    float alpha = (float) totalDy / mBannerHeight;
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
                    ImmersionBar.with(ProductSpecialActivity.this)
                            .statusBarDarkFont(false)
                            .init();
                } else {
                    viewActionbar.setAlpha(1);
                    ivBack.setImageResource(R.mipmap.return_icon);
                    tvTitle.setTextColor(getResources().getColor(R.color.textColor_actionBar_title));
                    viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    ImmersionBar.with(ProductSpecialActivity.this)
                            .statusBarDarkFont(true)
                            .init();
                }

            }
        });

        loadPage(true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst,false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE * 4);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId())?"":LoginHandler.get().getUserId());
        jsonObject.put("otherType", "6");
        ApiManager.get().getData(Const.GET_PRODUCT_LIST_TAG, null, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        productSpecialAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        productSpecialAdapter.notifyDataSetChanged();
                        rvProduct.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            rvProduct.setVisibility(View.VISIBLE);
                        } else {
                            rvProduct.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(ProductSpecialActivity.this, msg);
            }
        }, null);
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
