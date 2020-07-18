package com.kasa.ola.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ShopDetailBean;
import com.kasa.ola.bean.entity.ShopProductBean;
import com.kasa.ola.bean.entity.ShopProductsBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ShopProductAdapter;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.jsbridge.BridgeWebView;
import com.kasa.ola.widget.xbanner.XBanner;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopDetailActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.iv_shop_logo)
    ImageView ivShopLogo;
    @BindView(R.id.tv_shop_name)
    TextView tvShopName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.shop_tab)
    TabLayout shopTab;
    @BindView(R.id.webview)
    BridgeWebView webview;
    @BindView(R.id.rv_products)
    LoadMoreRecyclerView rvProducts;
    @BindView(R.id.shop_banner)
    XBanner shopBanner;
    private String shopID;
    private int currentPage = 1;
//    private EducationOrganDetailBean educationOrganDetailBean;
//    private ArrayList<String> orderNoList;
    private ShopProductsBean shopProductsBean;
    private List<ShopProductBean> shopProductBeans = new ArrayList<>();
    private ShopProductAdapter shopProductAdapter;
    private ShopDetailBean shopDetailBean;
    private ArrayList<ShopDetailBean.Banner> banners = new ArrayList<>();
    private boolean isFirst = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        shopID = intent.getStringExtra(Const.SHOP_ID);
        initTitle();
        initBanner();
        initView();
    }

    private void initBanner() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) shopBanner.getLayoutParams();
        lp.height = (DisplayUtils.getScreenWidth(this)/*-DisplayUtils.dip2px(ShopDetailActivity.this,10)*2*/)* 360 / 700;
        //以下展示数据部分
        shopBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                if (model instanceof ShopDetailBean.Banner) {
                    final ShopDetailBean.Banner bannerBean = (ShopDetailBean.Banner) model;
                    ImageLoaderUtils.imageLoad(ShopDetailActivity.this, bannerBean.getImageUrl(), (ImageView) view, R.mipmap.shop_detail_banner_default,false);
                }
            }
        });
        shopBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, int position) {
//                if (model instanceof ProductBannerBean) {
//                    ProductBannerBean productBannerBean = (ProductBannerBean) model;
//                    if (!TextUtils.isEmpty(productBannerBean.getProductID())) {
//                        Intent intent = new Intent(getContext(), ProductInfoActivity.class);
//                        intent.putExtra(Const.MALL_GOOD_ID_KEY, productBannerBean.getProductID());
//                        startActivity(intent);
//                    }
//                }
            }
        });
    }

    private void initView() {
        shopTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isFirst){
                    switch (tab.getPosition()) {
                        case 0:
                            webview.setVisibility(View.VISIBLE);
                            rvProducts.setVisibility(View.GONE);
                            break;
                        case 1:
                            webview.setVisibility(View.GONE);
                            rvProducts.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        webSettings.setTextZoom(100);
//        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setBlockNetworkImage(false); // 解决图片不显示
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webview.setWebViewClient(new BaseWebViewClient());
        webview.setFocusable(true);
        webview.setFocusableInTouchMode(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        shopTab.removeAllTabs();
        shopTab.addTab(shopTab.newTab().setText(getString(R.string.shop_detail)));
        shopTab.addTab(shopTab.newTab().setText(getString(R.string.shop_products)));


        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(ShopDetailActivity.this);
        rvProducts.setLayoutManager(linearLayoutManager1);
        shopProductAdapter = new ShopProductAdapter(ShopDetailActivity.this, shopProductBeans);
        shopProductAdapter.setOnShopProductGoBuyListener(new ShopProductAdapter.OnShopProductGoBuyListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ShopDetailActivity.this, ProductInfoActivity.class);
                intent.putExtra(Const.MALL_GOOD_ID_KEY, shopProductBeans.get(position).getProductID());
                startActivity(intent);
//                Intent intent = new Intent(getContext(), EducationCourseActivity.class);
//                startActivity(intent);
            }

            @Override
            public void goBuy(int position) {
                Intent intent = new Intent(ShopDetailActivity.this, ProductInfoActivity.class);
                intent.putExtra(Const.MALL_GOOD_ID_KEY, shopProductBeans.get(position).getProductID());
                startActivity(intent);
            }


        });
        rvProducts.setLoadingListener(this);
        rvProducts.setLoadingMoreEnabled(true);
        rvProducts.setAdapter(shopProductAdapter);


        tvPhoneNumber.setOnClickListener(this);
//        tvOrderOrCancel.setOnClickListener(this);

        loadData();
        loadProductPage(true);
    }

    private void loadProductPage(boolean isFirst) {
        loadProductPage(isFirst, false);
    }

    private void loadProductPage(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        jsonObject.put("suppliersID", shopID);
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE * 4);
        ApiManager.get().getData(Const.GET_SHOP_PRODUCT_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    shopProductsBean = JsonUtils.jsonToObject(jo.toString(), ShopProductsBean.class);
                    if (!isLoadMore) {
                        shopProductBeans.clear();
                        shopProductAdapter.notifyDataSetChanged();
                    }
                    List<ShopProductBean> list = shopProductsBean.getProductList();
                    if (list != null && list.size() > 0) {
                        shopProductBeans.addAll(list);
                        shopProductAdapter.notifyDataSetChanged();
                        rvProducts.loadMoreComplete(currentPage == shopProductsBean.getTotalPage());
                    }
                    if (shopTab.getSelectedTabPosition() == 2 && !isLoadMore) {
                        if (list != null && list.size() > 0) {
                            rvProducts.setVisibility(View.VISIBLE);
                        } else {
                            rvProducts.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(ShopDetailActivity.this, msg);
            }
        }, null);
    }

    private void loadData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("suppliersID", shopID);
        ApiManager.get().getData(Const.GET_SHOP_DETAIL, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    shopDetailBean = JsonUtils.jsonToObject(jo.toString(), ShopDetailBean.class);
//                    ImageLoaderUtils.imageLoadRound(ShopDetailActivity.this, shopDetailBean.getBanner(), shopBanner, R.mipmap.shop_detail_banner_default, false);
                    if (shopDetailBean.getImgArr() != null && shopDetailBean.getImgArr().size() > 0) {
                        banners.clear();
                        banners.addAll(shopDetailBean.getImgArr());
                        shopBanner.setData(banners, null);
                    }
                    ImageLoaderUtils.imageLoad(ShopDetailActivity.this, shopDetailBean.getSuppliersLogo(), ivShopLogo, R.mipmap.shop_icon_default, false);
                    tvShopName.setText(shopDetailBean.getSuppliersName());
                    tvAddress.setText(shopDetailBean.getAddress());
                    tvPhoneNumber.setText(shopDetailBean.getCall());
                    webview.loadUrl(shopDetailBean.getDetailUrl());
//                    if (educationOrganDetailBean.getBuyStatus().equals("0")) {
//                        tvOrderOrCancel.setText(getString(R.string.buy_year_card));
//                        tvOrderOrCancel.setBackgroundResource(R.drawable.bg_rectangle_blue_corner_1);
//                        tvOrderOrCancel.setEnabled(true);
//                    } else if (educationOrganDetailBean.getBuyStatus().equals("1")) {
//                        tvOrderOrCancel.setText(getString(R.string.have_buy));
//                        tvOrderOrCancel.setBackgroundResource(R.drawable.bg_rectangle_grey_corner_1);
//                        tvOrderOrCancel.setEnabled(false);
//                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(ShopDetailActivity.this, msg);
            }
        }, null);
    }

    private void initTitle() {
        setActionBar(getString(R.string.shop_detail), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_order_or_cancel:
//                Intent intent = new Intent(EducationOrganActivity.this, EducationCardBuyActivity.class);
//                YearCardBean yearCardBean = new YearCardBean();
//                yearCardBean.setEducationID(educationOrganDetailBean.getEducationID());
//                yearCardBean.setBuyStatus(educationOrganDetailBean.getBuyStatus());
//                yearCardBean.setPrice(educationOrganDetailBean.getPrice());
//                yearCardBean.setYearCardDesc(educationOrganDetailBean.getYearCardDesc());
//                yearCardBean.setYearCardID(educationOrganDetailBean.getYearCardID());
//                yearCardBean.setYearCardName(educationOrganDetailBean.getYearCardName());
//                intent.putExtra(Const.YEAR_CARD_TAG, (Serializable) yearCardBean);
//                startActivityForResult(intent, Const.YEAR_CARD_PAY_CODE);
//                break;
            case R.id.tv_phone_number:
                if (!TextUtils.isEmpty(shopDetailBean.getCall())) {
                    showTelephony(shopDetailBean.getCall());
                }
                break;
        }
    }

    @Override
    public void onLoadMore() {
        if (shopTab.getSelectedTabPosition() == 1) {
            currentPage++;
            loadProductPage(false, true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadProductPage(true);
        }
    }

    private void showTelephony(final String tel) {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        builder.setMessage(tel)
                .setLeftButton(getString(R.string.cancel))
                .setRightButton(getString(R.string.go_telephony))
                .setDialogInterface(new CommonDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                        requestPermissions(ShopDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                new RequestPermissionCallBack() {
                                    @Override
                                    public void granted() {
                                        try {
                                            Uri uri = Uri.parse("tel:" + tel);
                                            Intent telIntent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(telIntent);
                                        } catch (ActivityNotFoundException a) {
                                            ToastUtils.showShortToast(ShopDetailActivity.this, getString(R.string.tel_confirm));
                                        }
                                    }

                                    @Override
                                    public void denied() {
                                        ToastUtils.showShortToast(ShopDetailActivity.this, "获取权限失败，正常功能受到影响");
                                    }

                                });

                    }
                })
                .create()
                .show();
    }
    private class BaseWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webview.loadUrl(url);
            return true;
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url,
                                           boolean isReload) {

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webview.setVisibility(View.VISIBLE);
            isFirst = false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();  //接受所有证书
            super.onReceivedSslError(view, handler, error);
        }

    }

}
