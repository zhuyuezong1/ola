package com.kasa.ola.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ShopProductBean;
import com.kasa.ola.bean.entity.StoreDetailBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.pop.ShowTextPopup;
import com.kasa.ola.ui.adapter.ShopProductVerticalAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.MapUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.xbanner.XBanner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDetailsActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener {
    @BindView(R.id.rv_products)
    LoadMoreRecyclerView rvProducts;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.ll_store)
    LinearLayout llStore;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    private int dy;
    private int mBannerHeight;
    private ShopProductVerticalAdapter productVerticalAdapter;
    private List<ShopProductBean> productBeans = new ArrayList<>();
    private int currentPage = 1;
    private LinearLayout ll_business_time;
    private ImageView iv_nav;
    private ImageView iv_phone;
    private String shopID;
    private StoreDetailBean storeDetailBean;
    private ArrayList<StoreDetailBean.Banner> banners = new ArrayList<>();
    private XBanner storeBanner;
    private TextView tv_store_name;
    private TextView tv_address;
    private TextView tv_time;
    private SelectImagePop selectMapPop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        shopID = intent.getStringExtra(Const.SHOP_ID);
        initView();
        getStoreDetailsData();
        loadPage(true);
    }

    private void getStoreDetailsData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("suppliersID", shopID);
        ApiManager.get().getData(Const.GET_SHOP_DETAIL, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    storeDetailBean = JsonUtils.jsonToObject(jo.toString(), StoreDetailBean.class);
//                    ImageLoaderUtils.imageLoadRound(ShopDetailActivity.this, shopDetailBean.getBanner(), shopBanner, R.mipmap.shop_detail_banner_default, false);
                    if (storeDetailBean.getImgArr() != null && storeDetailBean.getImgArr().size() > 0) {
                        banners.clear();
                        banners.addAll(storeDetailBean.getImgArr());
                        storeBanner.setData(banners, null);
                    }
                    tv_store_name.setText(storeDetailBean.getSuppliersName());
                    tv_address.setText(storeDetailBean.getAddress());
                    tv_time.setText(storeDetailBean.getBusinessHours());
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(StoreDetailsActivity.this, msg);
            }
        }, null);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        ll_business_time.setOnClickListener(this);
        iv_nav.setOnClickListener(this);
        iv_phone.setOnClickListener(this);
    }

    private void initView() {
        ivBack.setImageResource(R.mipmap.icon_product_details_back);
        ivBack.setOnClickListener(this);
        ivShare.setVisibility(View.GONE);
        ivShare.setImageResource(R.mipmap.icon_share);
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        ImmersionBar.with(this).titleBar(R.id.view_actionbar)
                .fitsSystemWindows(true)
                .navigationBarColor(android.R.color.black)
                .autoDarkModeEnable(true)
                .autoStatusBarDarkModeEnable(true, 0.2f)
                .init();
        productVerticalAdapter = new ShopProductVerticalAdapter(StoreDetailsActivity.this, productBeans);
        rvProducts.setLoadingListener(this);
        rvProducts.setLoadingMoreEnabled(true);
        rvProducts.setAdapter(productVerticalAdapter);

        View headView = LayoutInflater.from(StoreDetailsActivity.this).inflate(R.layout.view_store_details_head, rvProducts, false);
        storeBanner = headView.findViewById(R.id.store_banner);
        tv_store_name = headView.findViewById(R.id.tv_store_name);
        tv_time = headView.findViewById(R.id.tv_time);
        tv_address = headView.findViewById(R.id.tv_address);
        ll_business_time = headView.findViewById(R.id.ll_business_time);
        iv_nav = headView.findViewById(R.id.iv_nav);
        iv_phone = headView.findViewById(R.id.iv_phone);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) storeBanner.getLayoutParams();
        int screenWidth = DisplayUtils.getScreenWidth(StoreDetailsActivity.this);
        lp.width = screenWidth;
        lp.height = screenWidth;

        storeBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                if (model instanceof StoreDetailBean.Banner) {
                    ImageView imageView = (ImageView) view;
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ImageLoaderUtils.imageLoad(StoreDetailsActivity.this, ((StoreDetailBean.Banner) model).getImageUrl(), imageView);
                }
            }
        });

        mBannerHeight = screenWidth - DisplayUtils.getStatusBarHeight2(StoreDetailsActivity.this) - DisplayUtils.dip2px(StoreDetailsActivity.this, 45);
        rvProducts.addHeaderView(headView);
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                if (totalDy < mBannerHeight) {
                    float alpha = (float) totalDy / mBannerHeight;
                    if (totalDy > 0) {
                        viewActionbar.setAlpha(alpha);
                        ivBack.setImageResource(R.mipmap.return_icon);
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                        ivShare.setImageResource(R.mipmap.icon_share_normal);
                    } else if (totalDy == 0) {
                        viewActionbar.setAlpha(1);
                        ivBack.setImageResource(R.mipmap.icon_product_details_back);
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                        ivShare.setImageResource(R.mipmap.icon_share);
                    }
                    ImmersionBar.with(StoreDetailsActivity.this)
                            .statusBarDarkFont(false)
                            .init();
                } else {
                    viewActionbar.setAlpha(1);
                    ivBack.setImageResource(R.mipmap.return_icon);
                    tvTitle.setTextColor(getResources().getColor(R.color.textColor_actionBar_title));
                    viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    ivShare.setImageResource(R.mipmap.icon_share_normal);
                    ImmersionBar.with(StoreDetailsActivity.this)
                            .statusBarDarkFont(true)
                            .init();
                }
            }
        });
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        initEvent();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_business_time:
                if (storeDetailBean != null) {
                    showBusinessPop(storeDetailBean.getBusinessHours());
                }
                break;
            case R.id.iv_nav:
                List<TextBean> bottomList = getBottomList();
                showSelectMapPop(bottomList);
                break;
            case R.id.iv_phone:
                showTelephony(LoginHandler.get().getBaseUrlBean().getServiceMobile());
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showSelectMapPop(List<TextBean> bottomList) {
        selectMapPop = new SelectImagePop(StoreDetailsActivity.this, bottomList, new OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                String longitude = storeDetailBean.getLongitude();
                String latitude = storeDetailBean.getLatitude();
                switch (position) {
                    case 0:
                        boolean baiduAvilible = MapUtils.isAvilible(StoreDetailsActivity.this, "com.baidu.BaiduMap");
                        if (baiduAvilible) {
                            MapUtils.toBaidu(StoreDetailsActivity.this, longitude, latitude);
                        } else {
                            ToastUtils.showLongToast(StoreDetailsActivity.this, "未安装百度地图");
                        }
                        break;
                    case 1:
                        boolean gaodeAvilible = MapUtils.isAvilible(StoreDetailsActivity.this, "com.autonavi.minimap");
                        if (gaodeAvilible) {
                            MapUtils.toGaodeNavi(StoreDetailsActivity.this, longitude, latitude);
                        } else {
                            ToastUtils.showLongToast(StoreDetailsActivity.this, "未安装高德地图");
                        }
                        break;
                    case 2:
                        boolean tencentAvilible = MapUtils.isAvilible(StoreDetailsActivity.this, "com.tencent.map");
                        if (tencentAvilible) {
                            MapUtils.toTencent(StoreDetailsActivity.this, longitude, latitude);
                        } else {
                            ToastUtils.showLongToast(StoreDetailsActivity.this, "未安装高德地图");
                        }
                        break;
                    case 3:
                        selectMapPop.dismiss();
                        break;
                }
            }
        });
        selectMapPop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        selectMapPop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        selectMapPop.showAtLocation(iv_nav, Gravity.BOTTOM, 0, 0);
        selectMapPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                CommonUtils.backgroundAlpha(1, StoreDetailsActivity.this);
            }
        });
        CommonUtils.backgroundAlpha(0.3f, StoreDetailsActivity.this);
    }

    private List<TextBean> getBottomList() {
        List<TextBean> textBeans = new ArrayList<>();
        TextBean textBean1 = new TextBean();
        textBean1.setContent("百度地图");
        textBean1.setColor(R.color.black);
        TextBean textBean2 = new TextBean();
        textBean2.setContent("高德地图");
        textBean2.setColor(R.color.black);
        TextBean textBean3 = new TextBean();
        textBean3.setContent("腾讯地图");
        textBean3.setColor(R.color.black);
        TextBean textBean4 = new TextBean();
        textBean4.setContent("取消");
        textBean4.setColor(R.color.black);
        textBeans.add(textBean1);
        textBeans.add(textBean2);
        textBeans.add(textBean3);
        textBeans.add(textBean4);
        return textBeans;
    }

    private void showBusinessPop(String s) {
        ShowTextPopup showTextPopup = new ShowTextPopup(StoreDetailsActivity.this);
        showTextPopup.setTitle(getString(R.string.business_hours));
        showTextPopup.setContent(getString(R.string.business_hours_title) + s);
        showTextPopup.showPopupWindow();
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE / 2);
        jsonObject.put("suppliersID", shopID);
        ApiManager.get().getData(Const.GET_SHOP_PRODUCT_LIST, jsonObject, new BusinessBackListener() {

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
                    List<ShopProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ShopProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        productVerticalAdapter.notifyDataSetChanged();
                        rvProducts.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(StoreDetailsActivity.this, msg);
            }
        }, null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
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
                        requestPermissions(StoreDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                new RequestPermissionCallBack() {
                                    @Override
                                    public void granted() {
                                        try {
                                            Uri uri = Uri.parse("tel:" + tel);
                                            Intent telIntent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(telIntent);
                                        } catch (ActivityNotFoundException a) {
                                            ToastUtils.showShortToast(StoreDetailsActivity.this, getString(R.string.tel_confirm));
                                        }
                                    }

                                    @Override
                                    public void denied() {
                                        ToastUtils.showShortToast(StoreDetailsActivity.this, "获取权限失败，正常功能受到影响");
                                    }

                                });

                    }
                })
                .create()
                .show();
    }
}
