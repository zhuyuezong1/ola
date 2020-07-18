package com.kasa.ola.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.animation.ArgbEvaluatorCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.ImmersionOwner;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.HomeTopBean;
import com.kasa.ola.bean.entity.HomeTopBean.BannerBean;
import com.kasa.ola.bean.entity.HomeTopBean.ProductBannerBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.DevelopActivity;
import com.kasa.ola.ui.MallActivity;
import com.kasa.ola.ui.MyMessageActivity;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.ui.SearchActivity;
import com.kasa.ola.ui.WebActivity;
import com.kasa.ola.ui.adapter.RecommendProductAdapter;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;
import com.kasa.ola.widget.xbanner.XBanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeOldFragment extends BaseFragment implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener, ImmersionOwner, BaseFragment.EventBusListener {

    @BindView(R.id.rv_hot_sale)
    LoadMoreRecyclerView rvHotSale;
    @BindView(R.id.iv_notice)
    ImageView ivNotice;
    @BindView(R.id.tv_dot)
    TextView tvDot;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_home_title)
    LinearLayout llHomeTitle;
    @BindView(R.id.activity_main)
    FrameLayout activityMain;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    Unbinder unbinder;
    private LayoutInflater inflater;
    private HomeTopBean homeTopBean;
    private int currentPage = 1;
    private List<ProductBean> productBeans = new ArrayList<>();
    private RecommendProductAdapter hotSaleProductAdapter;
    private ArrayList<ProductBannerBean> productBannerBeans = new ArrayList<>();
    private ArrayList<BannerBean> bannerBeans = new ArrayList<>();
    private int bannerHeight = 0;
    private int marginTop = 0;
    private ImageView ivHomeInvitation;
    private XBanner homeBanner;
    private XBanner homeSmallBanner;
    private ImageView ivLimitRob;
    private ImageView ivGroupBuyActivity;
    private ImageView ivFreeShippingProducts;
    private TextView tvGetCouponsCenter;
    private TextView tvDailySign;
    private TextView tvLowCostArea;
    private TextView tvEducationTrain;
    private ImageView ivWalkAroundOla;
    private ImageView ivHighBackLowCost;
    private ImageView ivRedPacketDeduct;
    private LinearLayout llActivity;
    private LinearLayout llFreeShipping;
    private LinearLayout llGroupBuyActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_home_old, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        initEvent();
        loadTopData();
        loadPage(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApiManager.get().getMyInfo(null);
        initMessageDot();
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (isFirst) {
                isFirst = false;
                loadPage(true);
            } else {
                loadPage(false);
            }
        }
    }*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void loadTopData() {
        ApiManager.get().getData(Const.GET_HOMEPAGE_INFO_TAG, null, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    homeTopBean = JsonUtils.jsonToObject(jo.toString(), HomeTopBean.class);
                    if (homeTopBean.getCarouselrList() != null && homeTopBean.getCarouselrList().size() > 0) {
                        productBannerBeans.clear();
                        productBannerBeans.addAll(homeTopBean.getCarouselrList());
                        homeBanner.setData(productBannerBeans, null);
                    }
                    if (homeTopBean.getBannerList() != null && homeTopBean.getBannerList().size() > 0) {
                        bannerBeans.clear();
                        bannerBeans.addAll(homeTopBean.getBannerList());
                        homeSmallBanner.setData(bannerBeans, null);
                    }

                    ImageLoaderUtils.imageLoad(getContext(), homeTopBean.getAds() == null ? null : homeTopBean.getAds().getImageUrl(), ivHomeInvitation, R.mipmap.invitefriendsbanner, false);
//                    if (homeTopBean.getBannerBean()!=null && !TextUtils.isEmpty(homeTopBean.getBannerBean().getImageUrl())) {
//                        ImageLoaderUtils.imageLoad(getContext(), homeTopBean.getBannerBean().getImageUrl(), ivHomeInvitation, R.mipmap.invitefriendsbanner, false);
//                    }

                    if (!TextUtils.isEmpty(homeTopBean.getLimitedUrl())) {
                        ImageLoaderUtils.imageLoad(getContext(), homeTopBean.getLimitedUrl(), ivLimitRob);
                    }
                    if (!TextUtils.isEmpty(homeTopBean.getBuyUrl())) {
                        ImageLoaderUtils.imageLoad(getContext(), homeTopBean.getBuyUrl(), ivGroupBuyActivity);
                    }
                    if (!TextUtils.isEmpty(homeTopBean.getParcelsUrl())) {
                        ImageLoaderUtils.imageLoad(getContext(), homeTopBean.getParcelsUrl(), ivFreeShippingProducts);
                    }

                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, null);
    }

    private void initEvent() {

        ivNotice.setOnClickListener(this);
        llSearch.setOnClickListener(this);

        tvGetCouponsCenter.setOnClickListener(this);
        tvDailySign.setOnClickListener(this);
        tvLowCostArea.setOnClickListener(this);
        tvEducationTrain.setOnClickListener(this);

        ivHomeInvitation.setOnClickListener(this);
        ivWalkAroundOla.setOnClickListener(this);
        llActivity.setOnClickListener(this);
        llFreeShipping.setOnClickListener(this);
        ivRedPacketDeduct.setOnClickListener(this);
        ivHighBackLowCost.setOnClickListener(this);
        llGroupBuyActivity.setOnClickListener(this);

//        tvCheckMore.setOnClickListener(this);
//        ivEducationOrgan.setOnClickListener(this);
//        ivHotCourse.setOnClickListener(this);
    }

    private void getBaseUrl() {
        ApiManager.get().getData(Const.GET_BASE_URL, null, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    LoginHandler.get().saveBaseUrl(jo);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(getContext(), msg);
            }
        }, null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {   // 不在最前端显示 相当于调用了onPause();
            return;
        } else {  // 在最前端显示 相当于调用了onResume();
            ApiManager.get().getMyInfo(null);
            initMessageDot();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initBanner() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) homeBanner.getLayoutParams();
        lp.height = (DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dip2px(getContext(), 10) * 2) * 360 / 700;
        bannerHeight = lp.height;
        marginTop = DisplayUtils.dip2px(getContext(), 45) + DisplayUtils.dip2px(getContext(), 5) + DisplayUtils.getStatusBarHeight2(getActivity());
        lp.topMargin = marginTop;
        //以下展示数据部分
        homeBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                if (model instanceof ProductBannerBean) {
                    final ProductBannerBean productBannerBean = (ProductBannerBean) model;
                    ImageLoaderUtils.imageLoadRound(getContext(), productBannerBean.getImageUrl(), (ImageView) view, 5);
                }
            }
        });
        homeBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, int position) {
                if (model instanceof ProductBannerBean) {
                    ProductBannerBean productBannerBean = (ProductBannerBean) model;
                    if (!TextUtils.isEmpty(productBannerBean.getProductID())) {
                        Intent intent = new Intent(getContext(), ProductInfoActivity.class);
                        intent.putExtra(Const.MALL_GOOD_ID_KEY, productBannerBean.getProductID());
                        startActivity(intent);
                    }
                }
            }
        });

        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) homeSmallBanner.getLayoutParams();
        lp1.height = (DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dip2px(getContext(), 20) * 2) * 260 / 540;
        //以下展示数据部分
        homeSmallBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                if (model instanceof BannerBean) {
                    final BannerBean bannerBean = (BannerBean) model;
                    ImageLoaderUtils.imageLoadRound(getContext(), bannerBean.getImageUrl(), (ImageView) view, 5);
                }
            }
        });
        homeSmallBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, int position) {
                if (model instanceof BannerBean) {
                    BannerBean bannerBean = (BannerBean) model;
                    if (!TextUtils.isEmpty(bannerBean.getWebUrl())) {
                        Intent intent = new Intent(getContext(), WebActivity.class);
                        intent.putExtra(Const.WEB_TITLE, bannerBean.getTitle());
                        intent.putExtra(Const.INTENT_URL, bannerBean.getWebUrl());
                        startActivity(intent);
                    }
                }
            }
        });
        rvHotSale.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                totalDy += dy;
                if (totalDy < 0) {
                    totalDy = 0;
                }
                Integer barColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / bannerHeight, getResources().getColor(R.color.white_transparent), Color.WHITE);
                Integer textColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / bannerHeight, Color.WHITE, Color.BLACK);
                if (totalDy < bannerHeight) {
                    float alpha = (float) totalDy / bannerHeight;
                    if (totalDy > 0) {
                        llHomeTitle.setAlpha(alpha);
                        ivNotice.setImageResource(R.mipmap.blacknews_icon);
                        llHomeTitle.setBackgroundColor(getResources().getColor(R.color.white));
                    } else if (totalDy == 0) {
                        llHomeTitle.setAlpha(1);
                        ivNotice.setImageResource(R.mipmap.news_icon);
                        llHomeTitle.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    ImmersionBar.with(getActivity())
                            .statusBarDarkFont(false)
                            .init();
                } else {
                    llHomeTitle.setAlpha(1);
                    ivNotice.setImageResource(R.mipmap.blacknews_icon);
                    llHomeTitle.setBackgroundColor(getResources().getColor(R.color.white));
                    ImmersionBar.with(getActivity())
                            .statusBarDarkFont(true)
                            .init();
                }
            }
        });
//        slLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            private int totalDy = 0;
//
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                totalDy += scrollY - oldScrollY;
//                if (totalDy < 0) {
//                    totalDy = 0;
//                }
//                Integer barColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / bannerHeight, getResources().getColor(R.color.white_transparent), Color.WHITE);
//                Integer textColor = ArgbEvaluatorCompat.getInstance().evaluate(totalDy / bannerHeight, Color.WHITE, Color.BLACK);
//                if (totalDy < bannerHeight) {
//                    float alpha = (float) totalDy / bannerHeight;
//                    if (totalDy > 0) {
//                        llHomeTitle.setAlpha(alpha);
//                        ivNotice.setImageResource(R.mipmap.blacknews_icon);
//                        llHomeTitle.setBackgroundColor(getResources().getColor(R.color.white));
//                    } else if (totalDy == 0) {
//                        llHomeTitle.setAlpha(1);
//                        ivNotice.setImageResource(R.mipmap.news_icon);
//                        llHomeTitle.setBackgroundColor(getResources().getColor(R.color.transparent));
//                    }
//                    ImmersionBar.with(getActivity())
//                            .statusBarDarkFont(false)
//                            .init();
//                } else {
//                    llHomeTitle.setAlpha(1);
//                    ivNotice.setImageResource(R.mipmap.blacknews_icon);
//                    llHomeTitle.setBackgroundColor(getResources().getColor(R.color.white));
//                    ImmersionBar.with(getActivity())
//                            .statusBarDarkFont(true)
//                            .init();
//                }
//            }
//        });
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
                refreshLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        hotSaleProductAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        hotSaleProductAdapter.notifyDataSetChanged();
                        rvHotSale.loadMoreComplete(currentPage == jo.optInt("totalPage"));
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
                refreshLayout.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, isFirst ? loadingView : null);
    }

    private void initTitle() {
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
//        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        llHomeTitle.setPadding(0, DisplayUtils.getStatusBarHeight2(getActivity()), 0, 0);
//        initMessageDot();
    }

    private void initMessageDot() {
        if (LoginHandler.get().checkLogined()) {
            String noReadMessage = LoginHandler.get().getMyInfo().optString("noReadMessage");
            if (noReadMessage.equals("0")) {
                tvDot.setVisibility(View.GONE);
            } else {
                tvDot.setVisibility(View.VISIBLE);
            }
        } else {
            tvDot.setVisibility(View.GONE);
        }
        tvDot.setTextColor(Color.WHITE);
        tvDot.setBackgroundColor(Color.BLUE);
        tvDot.setGravity(Gravity.CENTER);
        tvDot.setIncludeFontPadding(false);
        tvDot.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.psts_dot_txt_size));
        tvDot.setSingleLine();
        Drawable dot_drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.apsts_tips, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvDot.setBackground(dot_drawable);
        } else {
            tvDot.setBackgroundDrawable(dot_drawable);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
//        rvHotSale.addItemDecoration(new GridItemSpance(DisplayUtils.dip2px(getContext(),14),DisplayUtils.dip2px(getContext(),0),2));
        rvHotSale.setLayoutManager(linearLayoutManager);
        rvHotSale.setLoadingListener(this);
        rvHotSale.setLoadingMoreEnabled(true);
        hotSaleProductAdapter = new RecommendProductAdapter(getContext(), productBeans);
        rvHotSale.setAdapter(hotSaleProductAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ApiManager.get().getMyInfo(null);
                loadTopData();
                if (LoginHandler.get().getBaseUrlBean() == null) {
                    getBaseUrl();
                }
                currentPage = 1;
                loadPage(true);
            }
        });
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                ApiManager.get().getMyInfo(null);
                loadTopData();
                if (LoginHandler.get().getBaseUrlBean() == null) {
                    getBaseUrl();
                }
                currentPage = 1;
                loadPage(true);
            }
        });
        View headView = inflater.from(getContext()).inflate(R.layout.view_home_head_old, rvHotSale, false);
        ivHomeInvitation = headView.findViewById(R.id.iv_home_invitation);
        homeBanner = headView.findViewById(R.id.home_banner);
        homeSmallBanner = headView.findViewById(R.id.home_small_banner);
        ivLimitRob = headView.findViewById(R.id.iv_limit_rob);
        ivGroupBuyActivity = headView.findViewById(R.id.iv_group_buy_activity);
        ivFreeShippingProducts = headView.findViewById(R.id.iv_free_shipping_products);
        tvGetCouponsCenter = headView.findViewById(R.id.tv_get_coupons_center);
        tvDailySign = headView.findViewById(R.id.tv_daily_sign);
        tvLowCostArea = headView.findViewById(R.id.tv_low_cost_area);
        tvEducationTrain = headView.findViewById(R.id.tv_education_train);
        ivWalkAroundOla = headView.findViewById(R.id.iv_walk_around_ola);
        ivHighBackLowCost = headView.findViewById(R.id.iv_high_back_low_cost);
        ivRedPacketDeduct = headView.findViewById(R.id.iv_red_packet_deduct);
        llActivity = headView.findViewById(R.id.ll_activity);
        llFreeShipping = headView.findViewById(R.id.ll_free_shipping);
        llGroupBuyActivity = headView.findViewById(R.id.ll_group_buy_activity);
        ViewGroup.LayoutParams layoutParams = ivHomeInvitation.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dip2px(getContext(), 14) * 2;
        layoutParams.width = width;
        layoutParams.height = width / 4;
        rvHotSale.addHeaderView(headView);
        initTitle();
        initBanner();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(searchIntent);

                break;
            case R.id.iv_notice:
                if (CommonUtils.checkLogin(getContext())) {
                    Intent messageIntent = new Intent(getContext(), MyMessageActivity.class);
                    startActivity(messageIntent);
                }
                break;
            case R.id.iv_home_invitation:
//                if (CommonUtils.checkLogin(getContext())){
//                    CommonUtils.showSimpleBottomSheetGrid(getContext(), LoginHandler.get().getBaseUrlBean().getShareUrl()+"?userID="+LoginHandler.get().getUserId(),0);
//                }
                if (homeTopBean != null && homeTopBean.getAds() != null && !TextUtils.isEmpty(homeTopBean.getAds().getWebUrl())) {
//                    Intent inviteIntent = new Intent(getContext(), WebActivity.class);
//                    inviteIntent.putExtra(Const.INTENT_URL, homeTopBean.getBannerBean().getWebUrl());
//                    inviteIntent.putExtra(Const.WEB_TITLE, homeTopBean.getBannerBean().getTitle());
//                    startActivity(inviteIntent);
//                    Intent intent= new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri content_url = Uri.parse(homeTopBean.getAds().getWebUrl());
//                    intent.setData(content_url);
//                    startActivity(intent);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(homeTopBean.getAds().getWebUrl());
                    intent.setDataAndType(content_url, "text/html");
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    startActivity(intent);
                }

                break;
            case R.id.tv_get_coupons_center:
                Intent infoIntent = new Intent(getContext(), WebActivity.class);
                infoIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getInfoUrl());
                infoIntent.putExtra(Const.WEB_TITLE, getString(R.string.product_intro));
                startActivity(infoIntent);
//                Intent intent2 = new Intent(getContext(), DevelopActivity.class);
//                startActivity(intent2);
                break;
            case R.id.tv_daily_sign:
                if (CommonUtils.checkLogin(getContext())) {
                    Intent signIntent = new Intent(getContext(), WebActivity.class);
                    signIntent.putExtra(Const.WEB_TITLE, getString(R.string.sign));
                    signIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getSignUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
                    startActivity(signIntent);
                }
//                Intent dailySignIntent = new Intent(getContext(), WebActivity.class);
//                startActivity(dailySignIntent);
                break;
            case R.id.tv_education_train:
                Intent intent = new Intent(getContext(), MallActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_activity:
                Intent activityIntent = new Intent(getContext(), WebActivity.class);
                activityIntent.putExtra(Const.WEB_TITLE, getContext().getString(R.string.limit_activity));
                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
                    activityIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getLimitedUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
                } else {
                    activityIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getLimitedUrl());
                }
                startActivity(activityIntent);
//                Intent mallIntent = new Intent(getContext(), MallActivity.class);
//                startActivity(mallIntent);
//                Intent educationHomeIntent = new Intent(getContext(), EducationProductsActivity.class);
//                startActivity(educationHomeIntent);
                break;
            case R.id.iv_walk_around_ola:
//                Intent walkAroundOlaintent = new Intent(getContext(), DevelopActivity.class);
//                startActivity(walkAroundOlaintent);
                Intent redPacketintent = new Intent(getContext(), WebActivity.class);
                redPacketintent.putExtra(Const.WEB_TITLE, "年终大回馈");
                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
                    redPacketintent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getRedPackUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken() + "&app=android");
                } else {
                    redPacketintent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getRedPackUrl() + "?app=android");
                }

                startActivity(redPacketintent);
                break;
            case R.id.tv_low_cost_area:
//                Intent lowPriceIntent = new Intent(getContext(), WebActivity.class);
//                lowPriceIntent.putExtra(Const.WEB_TITLE, getContext().getString(R.string.low_price_special));
//                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
//                    lowPriceIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getNineUrl() + "?userID=" + LoginHandler.get().getUserId()+"&token="+LoginHandler.get().getToken());
//                }else {
//                    lowPriceIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getNineUrl());
//                }
//
//                startActivity(lowPriceIntent);
//                Intent traditionalChineseIntent = new Intent(getContext(), TraditionalChineseActivity.class);
//                startActivity(traditionalChineseIntent);
//                Intent activityIntroIntent = new Intent(getContext(), WebActivity.class);
//                activityIntroIntent.putExtra(Const.WEB_TITLE, getContext().getString(R.string.activity_intro));
//                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
//                    activityIntroIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getActivityUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
//                }else {
//                    activityIntroIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getActivityUrl());
//                }
//                startActivity(activityIntroIntent);
                Intent intent1 = new Intent(getContext(), DevelopActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_free_shipping:
//                Intent freeShippingIntent = new Intent(getContext(), ProductSpecialActivity.class);
//                freeShippingIntent.putExtra(Const.SPECIAL_ENTER_TYPE, 5);
//                startActivity(freeShippingIntent);
                Intent freeShippingIntent = new Intent(getContext(), WebActivity.class);
                freeShippingIntent.putExtra(Const.WEB_TITLE, getContext().getString(R.string.free_shipping_special));
                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
                    freeShippingIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getParcelsUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
                } else {
                    freeShippingIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getParcelsUrl());
                }
                startActivity(freeShippingIntent);
                break;
            case R.id.iv_red_packet_deduct:
//                Intent redPacketDeductIntent = new Intent(getContext(), ProductSpecialActivity.class);
//                redPacketDeductIntent.putExtra(Const.SPECIAL_ENTER_TYPE, 3);
//                startActivity(redPacketDeductIntent);
                Intent redPacketDeductIntent = new Intent(getContext(), WebActivity.class);
                redPacketDeductIntent.putExtra(Const.WEB_TITLE, getContext().getString(R.string.deduct_special));
                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
                    redPacketDeductIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getDiscountUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
                } else {
                    redPacketDeductIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getDiscountUrl());
                }
                startActivity(redPacketDeductIntent);
                break;
            case R.id.iv_high_back_low_cost:
//                Intent highBackIntent = new Intent(getContext(), ProductSpecialActivity.class);
//                highBackIntent.putExtra(Const.SPECIAL_ENTER_TYPE, 2);
//                startActivity(highBackIntent);
                Intent highBackIntent = new Intent(getContext(), WebActivity.class);
                highBackIntent.putExtra(Const.WEB_TITLE, getContext().getString(R.string.high_back_low_cost));
                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
                    highBackIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getGaofanUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
                } else {
                    highBackIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getGaofanUrl());
                }
                startActivity(highBackIntent);
                break;
            case R.id.ll_group_buy_activity:
//                Intent groupBuyIntent = new Intent(getContext(), ProductSpecialActivity.class);
//                groupBuyIntent.putExtra(Const.SPECIAL_ENTER_TYPE, 4);
//                startActivity(groupBuyIntent);
                Intent groupBuyIntent = new Intent(getContext(), WebActivity.class);
                groupBuyIntent.putExtra(Const.WEB_TITLE, getContext().getString(R.string.group_buy_activity));
                if (!TextUtils.isEmpty(LoginHandler.get().getUserId()) && !TextUtils.isEmpty(LoginHandler.get().getToken())) {
                    groupBuyIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getBuyUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
                } else {
                    groupBuyIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getBuyUrl());
                }
                startActivity(groupBuyIntent);
                break;
//            case R.id.tv_check_more:
//                if (CommonUtils.checkLogin(getContext())){
//                    ((MainActivity) getActivity()).switchFragment(2);
//                }
//                break;
//            case R.id.iv_education_organ:
////                Intent organIntent = new Intent(getContext(), EducationTrainSpecialSubjectActivity.class);
////                startActivity(organIntent);
//                if (CommonUtils.checkLogin(getContext())){
//                    ((MainActivity)getActivity()).switchFragment(1);
//                }
//                break;
//            case R.id.iv_hot_course:
//                if (CommonUtils.checkLogin(getContext())){
//                    Intent courseIntent = new Intent(getContext(), EducationCourseActivity.class);
//                    startActivity(courseIntent);
//                }
//                break;
        }
//        switch (v.getId()) {
//            case R.id.iv_search:
//                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
//                startActivity(searchIntent);
//                break;
//            case R.id.iv_notice:
////                CommonDialog.Builder builder = new CommonDialog.Builder(getContext());
////                builder.setMessage(this.getString(R.string.is_join_qq_group))
////                        .setLeftButton(this.getString(R.string.cancel_pay))
////                        .setRightButton(this.getString(R.string.confirm_pay))
////                        .setDialogInterface(new CommonDialog.DialogInterface() {
////
////                            @Override
////                            public void leftButtonClick(CommonDialog dialog) {
////                                dialog.dismiss();
////                            }
////
////                            @Override
////                            public void rightButtonClick(CommonDialog dialog) {
////                                if (CommonUtils.isQQClientAvailable(getContext())){
////                                    joinQQGroup("KvbOHpPRnqlIcV-PAiChtMZ-cFWIlhtR");
////                                }else {
////                                    ToastUtils.showLongToast(getContext(),"未安装手机qq");
////                                }
////                                dialog.dismiss();
////                                finish();
////                            }
////                        })
////                        .create()
////                        .show();
//                if (CommonUtils.checkLogin(getContext())) {
//                    tvDot.setVisibility(View.GONE);
//                    Intent messageIntent = new Intent(getContext(), MessageActivity.class);
//                    startActivity(messageIntent);
//                }
//                break;
//            case R.id.iv_home_invitation:
////                Intent invitationIntent = new Intent(getContext(), WebActivity.class);
////                invitationIntent.putExtra(Const.WEB_TITLE, getString(R.string.invite));
////                invitationIntent.putExtra(Const.INTENT_URL, Const.SHARE_URL);
////                startActivity(invitationIntent);
//                if (CommonUtils.checkLogin(getContext())){
//                    CommonUtils.showSimpleBottomSheetGrid(getContext(), Const.SHARE_URL+"?userID="+LoginHandler.get().getUserId());
//                }
//                break;
//            case R.id.iv_vipzhuanqu:
//                ((MainActivity) getActivity()).switchFragment(1);
//                break;
//            case R.id.tv_mall_classify:
//                Intent mallClassifyIntent = new Intent(getContext(), MallClassifyActivity.class);
//                startActivity(mallClassifyIntent);
//                break;
//            case R.id.tv_hot_information:
//                if (!TextUtils.isEmpty(LoginHandler.get().getBaseUrlBean().getInfoUrl())) {
//                    Intent hotInformation = new Intent(getContext(), WebActivity.class);
//                    hotInformation.putExtra(Const.WEB_TITLE, getString(R.string.information));
//                    hotInformation.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getInfoUrl());
//                    startActivity(hotInformation);
//                }
//                break;
//            case R.id.tv_everyday_task:
//                if (!TextUtils.isEmpty(LoginHandler.get().getBaseUrlBean().getSignUrl())) {
//                    if (CommonUtils.checkLogin(getContext())) {
//                        Intent signIntent = new Intent(getContext(), WebActivity.class);
//                        signIntent.putExtra(Const.WEB_TITLE, getString(R.string.sign));
//                        signIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getSignUrl() + "?userID=" + LoginHandler.get().getUserId());
//                        startActivity(signIntent);
//                    }
//                }
//                break;
//            case R.id.tv_hot_products:
//                Intent intent2 = new Intent(getContext(), ProductListActivity.class);
//                intent2.putExtra(Const.PRODUCT_TYPE_KEY, 3);
//                startActivity(intent2);
//                break;
//            case R.id.tv_everyday_new_products:
//                Intent intent3 = new Intent(getContext(), ProductListActivity.class);
//                intent3.putExtra(Const.PRODUCT_TYPE_KEY, 4);
//                startActivity(intent3);
//                break;
//            case R.id.tv_must_buy_quality_goods:
//                Intent intent4 = new Intent(getContext(), ProductListActivity.class);
//                intent4.putExtra(Const.PRODUCT_TYPE_KEY, 5);
//                startActivity(intent4);
//                break;
//            case R.id.tv_surprise_brand:
////                Intent intent5 = new Intent(getContext(), ProductListActivity.class);
////                intent5.putExtra(Const.PRODUCT_TYPE_KEY, 6);
////                startActivity(intent5);
//                if (CommonUtils.checkLogin(getContext()) && CommonUtils.checkCertification(getContext())) {
//                    Intent intent = new Intent(getContext(), EducationActivity.class);
//                    startActivity(intent);
//                }
//                break;
//            case R.id.tv_vip_more:
////                Intent intent6 = new Intent(getContext(), ProductListActivity.class);
////                intent6.putExtra(Const.PRODUCT_TYPE_KEY,1);
////                startActivity(intent6);
//                ((MainActivity) getActivity()).switchFragment(1);
//                break;
//            case R.id.tv_select_quality_goods_more:
//                Intent intent7 = new Intent(getContext(), ProductListActivity.class);
//                intent7.putExtra(Const.PRODUCT_TYPE_KEY, 7);
//                startActivity(intent7);
//                break;
//            case R.id.tv_today_special_sale_more:
//                Intent intent8 = new Intent(getContext(), ProductListActivity.class);
//                intent8.putExtra(Const.PRODUCT_TYPE_KEY, 8);
//                startActivity(intent8);
//                break;
//            case R.id.tv_promote_sales_more:
//                Intent intent9 = new Intent(getContext(), ProductListActivity.class);
//                intent9.putExtra(Const.PRODUCT_TYPE_KEY, 2);
//                startActivity(intent9);
//                break;
//            case R.id.iv_promote_sales:
//
//                break;
//        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

    @Override
    public void onLazyBeforeView() {

    }

    @Override
    public void onLazyAfterView() {

    }

    @Override
    public void onVisible() {

    }

    @Override
    public void onInvisible() {

    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBar(R.id.ll_home_title)
                .fitsSystemWindows(true)
                .navigationBarColor(android.R.color.black)
                .autoDarkModeEnable(true)
                .autoStatusBarDarkModeEnable(true, 0.2f)
                .init();
    }

    @Override
    public boolean immersionBarEnabled() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.RefreshMyInfo refreshMyInfo) {
        initMessageDot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.MainBottonClickCurrent mainBottonClickCurrent) {
        rvHotSale.scrollTo(0, 0);
    }
}
