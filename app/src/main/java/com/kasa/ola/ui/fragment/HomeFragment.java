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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
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
import com.kasa.ola.ui.BusinessAndProductSearchActivity;
import com.kasa.ola.ui.CityServiceActivity;
import com.kasa.ola.ui.FindActivity;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.ui.MyMessageActivity;
import com.kasa.ola.ui.ProductClassifyActivity;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.ui.QualityShopActivity;
import com.kasa.ola.ui.SelectFirstProductActivity;
import com.kasa.ola.ui.StoreDetailsActivity;
import com.kasa.ola.ui.WebActivity;
import com.kasa.ola.ui.adapter.HomeProductAreaAdapter;
import com.kasa.ola.ui.adapter.ProductVerticalAdapter;
import com.kasa.ola.ui.adapter.ShopListAdapter;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.EncryptUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;
import com.kasa.ola.widget.xbanner.XBanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener/*, ImmersionOwner*/, BaseFragment.EventBusListener {

    @BindView(R.id.rv_recommend)
    LoadMoreRecyclerView rvRecommend;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    Unbinder unbinder;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.iv_notice)
    ImageView ivNotice;
    @BindView(R.id.tv_dot)
    TextView tvDot;
    @BindView(R.id.ll_home_title)
    LinearLayout llHomeTitle;
    @BindView(R.id.view_shadow)
    View viewShadow;
    private LayoutInflater inflater;
    private HomeTopBean homeTopBean;
    private int currentPage = 1;
    private List<ProductBean> productBeans = new ArrayList<>();
    private List<ProductBean> limitProductBeans = new ArrayList<>();
    private List<ProductBean> farmProductBeans = new ArrayList<>();
    private List<ProductBean> highBackProductBeans = new ArrayList<>();
    private List<ProductBean> freeShippingProductBeans = new ArrayList<>();
    private List<HomeTopBean.HomeQualityBean> qualityShopBeans = new ArrayList<>();
    //    private RecommendProductAdapter hotSaleProductAdapter;
    private ArrayList<ProductBannerBean> productBannerBeans = new ArrayList<>();
    private ArrayList<BannerBean> bannerBeans = new ArrayList<>();
    private int bannerHeight = 0;
    private int marginTop = 0;
    private XBanner homeBanner;
    private ImageView ivHomeInvitation;
    private TextView tvDailySign;
    private TextView tvFind;
    private TextView tvSelect;
    private TextView tvCity;
    private TextView tvClassify;
    private TextView tvCheckMore;
    private ProductVerticalAdapter productVerticalAdapter;
    private RecyclerView rvLimit;
    private RecyclerView rvFarm;
    private RecyclerView rvHighBack;
    private RecyclerView rvFree;
    private RecyclerView rv_quality_shop;
    private int defaultColor = R.color.COLOR_FF1677FF;
    private LinearLayout llLimit;
    private LinearLayout llFarm;
    private LinearLayout llHighBack;
    private LinearLayout llFreeShipping;
    private ShopListAdapter shopListAdapter;
    private HomeProductAreaAdapter limitProductAdapter;
    private HomeProductAreaAdapter farmProductAdapter;
    private HomeProductAreaAdapter highBackProductAdapter;
    private HomeProductAreaAdapter freeShippingProductAdapter;
    private String text;
    //    private RecyclerView rvGoodShop;

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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTitle();
        initView();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        ImmersionBar.destroy(HomeFragment.this);
    }

    private void loadTopData() {
        ApiManager.get().getData(Const.GET_HOMEPAGE_INFO_TAG, null, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    homeTopBean = JsonUtils.jsonToObject(jo.toString(), HomeTopBean.class);
                    if (homeTopBean!=null){
                        if (homeTopBean.getCarouselrList() != null && homeTopBean.getCarouselrList().size() > 0) {
                            productBannerBeans.clear();
                            productBannerBeans.addAll(homeTopBean.getCarouselrList());
                            homeBanner.setData(productBannerBeans, null);
                        }
                        ImageLoaderUtils.imageLoad(getContext(), homeTopBean.getAds() == null ? null : homeTopBean.getAds().getImageUrl(), ivHomeInvitation, R.mipmap.invitefriendsbanner, false);
                        if (homeTopBean.getSelectList()!=null&& homeTopBean.getSelectList().size()>0){
                            qualityShopBeans.clear();
                            qualityShopBeans.addAll(homeTopBean.getSelectList());
                            shopListAdapter.notifyDataSetChanged();
                        }
                        if (homeTopBean.getLimitedList()!=null && homeTopBean.getLimitedList().size()>0){
                            limitProductBeans.clear();
                            limitProductBeans.addAll(homeTopBean.getLimitedList());
                            limitProductAdapter.notifyDataSetChanged();
                        }
                        if (homeTopBean.getAgriculturalList()!=null && homeTopBean.getAgriculturalList().size()>0){
                            farmProductBeans.clear();
                            farmProductBeans.addAll(homeTopBean.getAgriculturalList());
                            farmProductAdapter.notifyDataSetChanged();
                        }
                        if (homeTopBean.getHighReturnList()!=null && homeTopBean.getHighReturnList().size()>0){
                            highBackProductBeans.clear();
                            highBackProductBeans.addAll(homeTopBean.getHighReturnList());
                            highBackProductAdapter.notifyDataSetChanged();
                        }
                        if (homeTopBean.getParcelsList()!=null && homeTopBean.getParcelsList().size()>0){
                            freeShippingProductBeans.clear();
                            freeShippingProductBeans.addAll(homeTopBean.getParcelsList());
                            freeShippingProductAdapter.notifyDataSetChanged();
                        }
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
        ivNotice.setOnClickListener(this);
        tvLocation.setOnClickListener(this);

        tvCheckMore.setOnClickListener(this);

        tvDailySign.setOnClickListener(this);
        tvFind.setOnClickListener(this);
        tvSelect.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvClassify.setOnClickListener(this);

        llLimit.setOnClickListener(this);
        llFarm.setOnClickListener(this);
        llHighBack.setOnClickListener(this);
        llFreeShipping.setOnClickListener(this);

        CommonUtils.cancelRecyclerViewTouchEvent(rvLimit,llLimit);
        CommonUtils.cancelRecyclerViewTouchEvent(rvFarm,llFarm);
        CommonUtils.cancelRecyclerViewTouchEvent(rvHighBack,llHighBack);
        CommonUtils.cancelRecyclerViewTouchEvent(rvFree,llFreeShipping);
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
            ImmersionBar.with(getActivity()).statusBarDarkFont(false).init();
            ApiManager.get().getMyInfo(null);
            initMessageDot();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initBanner() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) homeBanner.getLayoutParams();
        lp.height = (DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dip2px(getContext(), 10) * 2) * 130 / 351;
        bannerHeight = lp.height;
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
                        productVerticalAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        productVerticalAdapter.notifyDataSetChanged();
                        rvRecommend.loadMoreComplete(currentPage == jo.optInt("totalPage"));
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
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            setStatusBar(defaultColor);
//        }
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRecommend.setLayoutManager(linearLayoutManager);
        rvRecommend.setLoadingListener(this);
        rvRecommend.setLoadingMoreEnabled(true);
        productVerticalAdapter = new ProductVerticalAdapter(getContext(), productBeans);
        rvRecommend.setAdapter(productVerticalAdapter);
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
        View headView = inflater.from(getContext()).inflate(R.layout.view_home_head, rvRecommend, false);
        homeBanner = headView.findViewById(R.id.home_banner);
        tvDailySign = headView.findViewById(R.id.tv_daily_sign);
        tvFind = headView.findViewById(R.id.tv_find);
        tvSelect = headView.findViewById(R.id.tv_select);
        tvCity = headView.findViewById(R.id.tv_city);
        tvClassify = headView.findViewById(R.id.tv_classify);

        llLimit = headView.findViewById(R.id.ll_limit);
        llFarm = headView.findViewById(R.id.ll_farm);
        llHighBack = headView.findViewById(R.id.ll_high_back);
        llFreeShipping = headView.findViewById(R.id.ll_free_shipping);

        rvLimit = headView.findViewById(R.id.rv_limit);
        rvFarm = headView.findViewById(R.id.rv_farm);
        rvHighBack = headView.findViewById(R.id.rv_high_back);
        rvFree = headView.findViewById(R.id.rv_free);
        tvCheckMore = headView.findViewById(R.id.tv_check_more);
        ivHomeInvitation = headView.findViewById(R.id.iv_home_invitation);
        rv_quality_shop = headView.findViewById(R.id.rv_quality_shop);
//        rvGoodShop = headView.findViewById(R.id.rv_good_shop);
        ViewGroup.LayoutParams layoutParams = ivHomeInvitation.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dip2px(getContext(), 12) * 2;
        layoutParams.width = width;
        layoutParams.height = width / 4;
        GridLayoutManager limitLayoutManager = new GridLayoutManager(getContext(), 2);
        GridLayoutManager farmLayoutManager = new GridLayoutManager(getContext(), 2);
        GridLayoutManager highBackLayoutManager = new GridLayoutManager(getContext(), 2);
        GridLayoutManager freeLayoutManager = new GridLayoutManager(getContext(), 2);
        rvLimit.setLayoutManager(limitLayoutManager);
        rvFarm.setLayoutManager(farmLayoutManager);
        rvHighBack.setLayoutManager(highBackLayoutManager);
        rvFree.setLayoutManager(freeLayoutManager);
        limitProductAdapter = new HomeProductAreaAdapter(getContext(), limitProductBeans);
        farmProductAdapter = new HomeProductAreaAdapter(getContext(), farmProductBeans);
        highBackProductAdapter = new HomeProductAreaAdapter(getContext(), highBackProductBeans);
        freeShippingProductAdapter = new HomeProductAreaAdapter(getContext(), freeShippingProductBeans);
        rvLimit.setAdapter(limitProductAdapter);
        rvFarm.setAdapter(farmProductAdapter);
        rvHighBack.setAdapter(highBackProductAdapter);
        rvFree.setAdapter(freeShippingProductAdapter);
        shopListAdapter = new ShopListAdapter(getContext(), qualityShopBeans,0);
        shopListAdapter.setOnCheckMoreListener(new ShopListAdapter.OnCheckMoreListener() {
            @Override
            public void checkMore(int position) {
                HomeTopBean.HomeQualityBean homeQualityBean = qualityShopBeans.get(position);
                Intent intent = new Intent(getContext(), StoreDetailsActivity.class);
                intent.putExtra(Const.SHOP_ID,homeQualityBean.getSuppliersID());
                startActivity(intent);
            }
        });
        rv_quality_shop.setAdapter(shopListAdapter);

        CommonUtils.setScroll(rvLimit);
        CommonUtils.setScroll(rvFarm);
        CommonUtils.setScroll(rvHighBack);
        CommonUtils.setScroll(rvFree);
        CommonUtils.setScroll(rv_quality_shop);

        rvRecommend.addHeaderView(headView);


//        rvGoodShop.setLayoutManager(gridLayoutManager);
//        ShopRecommendAdapter shopRecommendAdapter = new ShopRecommendAdapter(getContext(), null);
//        rvGoodShop.setAdapter(shopRecommendAdapter);

//        initTitle();
        initEvent();
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
            case R.id.tv_location:
//                Intent intent2 = new Intent(getContext(), WebActivity.class);
//                intent2.putExtra(Const.WEB_TITLE, "测试");
//                intent2.putExtra(Const.INTENT_URL, "http://wx.oulachina.com/app/./index.php?i=2&c=entry&eid=74");
//                startActivity(intent2);
                //测试
//                Intent intent1 = new Intent(getContext(), PaySuccessResultActivity.class);
//                startActivity(intent1);
//                Intent intent2 = new Intent(getContext(), StoreDetailsActivity.class);
//                startActivity(intent2);
//                Intent intent3 = new Intent(getContext(), ProductDetailsActivity.class);
//                startActivity(intent3);
//                Intent intent1 = new Intent(getContext(), TestAlbumActivity.class);
//                startActivity(intent1);
                break;
            case R.id.ll_search:
                Intent searchIntent = new Intent(getContext(), BusinessAndProductSearchActivity.class);
                searchIntent.putExtra(Const.SEARCH_TYPE, 0);
                startActivity(searchIntent);
                //测试加密
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    try {
//                        DESKeySpec keySpec = new DESKeySpec(
//                                Const.PRODUCT_ID_ENC_SECRET.getBytes("UTF-8"));
//                        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//                        SecretKey key = keyFactory.generateSecret(keySpec);
//
//                        Cipher cipher = Cipher.getInstance("DES");
//                        cipher.init(Cipher.ENCRYPT_MODE, key);
//                        byte[] bytes = cipher.doFinal("1016".getBytes("UTF-8"));
//                        LogUtil.d("11111",bytes.toString());
//                        String base64encodedString = Base64.getEncoder().encodeToString(bytes);
//                        LogUtil.d("11111",base64encodedString);
//                        // 解码
//                        byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString.getBytes());
//                        LogUtil.d("11111",base64decodedBytes.toString());
//                        Cipher cipher1 = Cipher.getInstance("DES");
//                        cipher1.init(Cipher.DECRYPT_MODE, key);
//                        byte[] bytes1 = cipher1.doFinal(base64decodedBytes);
//                        LogUtil.d("11111",new String(bytes1));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                break;
            case R.id.iv_notice:
                Intent messageIntent = new Intent(getContext(), MyMessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.iv_home_invitation:
                if (homeTopBean != null && homeTopBean.getAds() != null && !TextUtils.isEmpty(homeTopBean.getAds().getWebUrl())) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(homeTopBean.getAds().getWebUrl());
                    intent.setDataAndType(content_url, "text/html");
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    startActivity(intent);
                }
                break;
            case R.id.tv_daily_sign:
                if (CommonUtils.checkLogin(getContext())) {
                    Intent signIntent = new Intent(getContext(), WebActivity.class);
                    signIntent.putExtra(Const.WEB_TITLE, getString(R.string.sign));
                    signIntent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getSignUrl() + "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken());
                    startActivity(signIntent);
                }
                break;
            case R.id.tv_find:
                if (CommonUtils.checkLogin(getContext())){
                    Intent findIntent = new Intent(getContext(), FindActivity.class);
                    startActivity(findIntent);
                }
                break;
            case R.id.tv_city:
                if (CommonUtils.checkLogin(getContext())){
                    Intent findIntent = new Intent(getContext(), CityServiceActivity.class);
                    startActivity(findIntent);
                }
                break;
            case R.id.tv_classify:
                Intent productClassifyIntent = new Intent(getContext(), ProductClassifyActivity.class);
                startActivity(productClassifyIntent);
                break;
            case R.id.tv_check_more:
                Intent qualityIntent = new Intent(getContext(), QualityShopActivity.class);
                startActivity(qualityIntent);
                break;
            case R.id.tv_select:
                Intent intent = new Intent(getContext(), SelectFirstProductActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_limit:
                startWebActivity(getContext().getString(R.string.limit_activity),
                        LoginHandler.get().getBaseUrlBean().getLimitedUrl(),
                        LoginHandler.get().getUserId(),
                        LoginHandler.get().getToken()
                );
                break;
            case R.id.ll_farm:
                startWebActivity(getContext().getString(R.string.special_product),
                        LoginHandler.get().getBaseUrlBean().getSpecialUrl(),
                        LoginHandler.get().getUserId(),
                        LoginHandler.get().getToken()
                );
                break;
            case R.id.ll_high_back:
                startWebActivity(getContext().getString(R.string.high_back_low_cost),
                        LoginHandler.get().getBaseUrlBean().getGaofanUrl(),
                        LoginHandler.get().getUserId(),
                        LoginHandler.get().getToken()
                );
                break;
            case R.id.ll_free_shipping:
                startWebActivity(getContext().getString(R.string.free_shipping_special),
                        LoginHandler.get().getBaseUrlBean().getParcelsUrl(),
                        LoginHandler.get().getUserId(),
                        LoginHandler.get().getToken()
                        );
                break;
        }
    }

    private void startWebActivity(String title,String url,String userID,String token){
        Intent intent = new Intent(getContext(), WebActivity.class);
        if (!TextUtils.isEmpty(title)){
            intent.putExtra(Const.WEB_TITLE, title);
        }
        if (!TextUtils.isEmpty(userID) && !TextUtils.isEmpty(token)) {
            intent.putExtra(Const.INTENT_URL, url + "?userID=" + userID + "&token=" + token);
        } else {
            intent.putExtra(Const.INTENT_URL, url);
        }
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

//    @Override
//    public void onLazyBeforeView() {
//
//    }
//
//    @Override
//    public void onLazyAfterView() {
//
//    }
//
//    @Override
//    public void onVisible() {
//
//    }
//
//    @Override
//    public void onInvisible() {
//
//    }
//
//    @Override
//    public void initImmersionBar() {
//        ImmersionBar.with(this).titleBar(R.id.ll_home_title)
//                .fitsSystemWindows(true)
//                .navigationBarColor(android.R.color.black)
//                .autoDarkModeEnable(true)
//                .autoStatusBarDarkModeEnable(true, 0.2f)
//                .init();
//    }
//
//    @Override
//    public boolean immersionBarEnabled() {
//        return true;
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.RefreshMyInfo refreshMyInfo) {
        initMessageDot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.MainBottonClickCurrent mainBottonClickCurrent) {
        rvRecommend.scrollToPosition(0);
    }
}
