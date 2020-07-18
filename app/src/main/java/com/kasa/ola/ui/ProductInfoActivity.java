package com.kasa.ola.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.bean.entity.PriceGroupBean;
import com.kasa.ola.bean.entity.ProductCommentBean;
import com.kasa.ola.bean.entity.ProductInfoBean;
import com.kasa.ola.bean.entity.SkuBean;
import com.kasa.ola.bean.model.PriceGroupModel;
import com.kasa.ola.bean.model.ProductSkuLabelModel;
import com.kasa.ola.bean.model.ProductSkuModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.MallSkuDialog;
import com.kasa.ola.dialog.SkuDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.CommentAdapter;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;
import com.kasa.ola.widget.jsbridge.BridgeWebView;
import com.kasa.ola.widget.jsbridge.DefaultHandler;
import com.kasa.ola.widget.xbanner.XBanner;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductInfoActivity extends BaseActivity implements View.OnClickListener, BaseActivity.EventBusListener, LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.good_info_banner)
    XBanner goodInfoBanner;
    @BindView(R.id.tv_good_desc)
    TextView tvGoodDesc;
    @BindView(R.id.tv_good_price)
    TextView tvGoodPrice;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.tv_sku)
    TextView tvSku;
    @BindView(R.id.view_sku)
    LinearLayout viewSku;
    @BindView(R.id.good_info_webview)
    BridgeWebView goodInfoWebview;
    //    @BindView(R.id.tv_share)
//    TextView tvShare;
    @BindView(R.id.tv_custom_service)
    TextView tvCustomService;
    @BindView(R.id.tv_buy_now)
    TextView tvBuyNow;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_product_title)
    TextView tvProductTitle;
    @BindView(R.id.tv_add_cart)
    TextView tvAddCart;
    @BindView(R.id.rv_comments)
    LoadMoreRecyclerView rvComments;
    @BindView(R.id.product_info_tab)
    TabLayout productInfoTab;
    @BindView(R.id.tv_supplier)
    TextView tvSupplier;
    @BindView(R.id.tv_sale_number)
    TextView tvSaleNumber;
    @BindView(R.id.tv_good_original_price)
    TextView tvGoodOriginalPrice;
    @BindView(R.id.nsv_product)
    NestedScrollView nsvProduct;
    private int num = 0;
    //    private boolean goBuy = false;
    private int status = 0;//0,无操作，1，去购买，2，加入购物车
    private String productID;
    //    private ProductBean productBean = new ProductBean();
    private ArrayList<String> bannerList = new ArrayList<>();
    private ArrayList<PriceGroupModel> priceGroup = new ArrayList<>();
    private ArrayList<ProductSkuModel> speItem = new ArrayList<>();
    private String[] skuIdList;
    private String[] skuNameList;
    private String skuContent = "";
    private CartBean.Product product = new CartBean.Product();
    private List<SkuBean> skuBeans;
    private List<PriceGroupBean> priceGroups;
    private int currentPage = 1;
    private List<ProductCommentBean> productCommentBeans = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private String lessonType;
    private ProductInfoBean productInfoBean;
    private Timer timer;
    private TimerTask timerTask;
    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        productID = getIntent().getStringExtra(Const.MALL_GOOD_ID_KEY);
        lessonType = getIntent().getStringExtra(Const.EDUCATION_TYPE);
        setContentView(R.layout.activity_product_info);
        ButterKnife.bind(this);
        initView();
        loadPage();
    }

    private void initView() {
        setActionBar(getString(R.string.product_detail), "");
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) goodInfoBanner.getLayoutParams();
        lp.height = DisplayUtils.getScreenWidth(this);
        //以下展示数据部分
        goodInfoBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                if (model instanceof String) {
                    ImageLoaderUtils.imageLoad(ProductInfoActivity.this, (String) model, (ImageView) view);
                }
            }
        });
        if (TextUtils.isEmpty(lessonType)) {//普通商品
            tvAddCart.setEnabled(true);
            tvAddCart.setVisibility(View.VISIBLE);
            viewSku.setEnabled(true);
        } else {
//            if (lessonType.equals("1")){
            tvAddCart.setEnabled(false);
            tvAddCart.setVisibility(View.INVISIBLE);
            viewSku.setEnabled(false);
            num = 1;
            product.setProductNum(num + "");
            tvSku.setText(num + "件");
//            }else if (lessonType.equals("2")){
//                tvAddCart.setEnabled(true);
//                tvAddCart.setVisibility(View.VISIBLE);
//                viewSku.setEnabled(true);
//            }
        }

        /**************商品详情H5***************/
        WebSettings webSettings = goodInfoWebview.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

//        goodInfoWebview.setVerticalScrollBarEnabled(false);
//        goodInfoWebview.setVerticalScrollbarOverlay(false);
//        goodInfoWebview.setHorizontalScrollBarEnabled(false);
//        goodInfoWebview.setHorizontalScrollbarOverlay(false);


//        goodInfoWebview.addJavascriptInterface(this, "MyApp");
//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                goodInfoWebview.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
//            }
//        };


//        webSettings.setSupportZoom(true);
////        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
////        webSettings.setTextZoom(100);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setBlockNetworkImage(false); // 解决图片不显示
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.LOAD_NORMAL);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            BridgeWebView.setWebContentsDebuggingEnabled(true);
        }
        goodInfoWebview.setDefaultHandler(new DefaultHandler());
//        goodInfoWebview.setWebChromeClient(new BaseWebChromeClient());
        goodInfoWebview.setWebViewClient(new BaseWebViewClient());
//        goodInfoWebview.setFocusable(true);
//        goodInfoWebview.setFocusableInTouchMode(true);
//        goodInfoWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        goodInfoWebview.setWebViewClient(new BaseWebViewClient());
//        goodInfoWebview.setWebChromeClient(new BaseWebChromeClient());

        viewSku.setOnClickListener(this);
        /**************底部操作按钮***************/
        tvCustomService.setOnClickListener(this);  //客服
        tvBuyNow.setOnClickListener(this);
//        tvShare.setOnClickListener(this);
        tvAddCart.setOnClickListener(this);

        /**************loadingView***************/
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                loadPage();
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setLoadingListener(this);
        rvComments.setLoadingMoreEnabled(true);
        commentAdapter = new CommentAdapter(ProductInfoActivity.this, productCommentBeans);
        rvComments.setNestedScrollingEnabled(false);
        rvComments.setFocusableInTouchMode(false);
        rvComments.requestFocus();
        rvComments.setAdapter(commentAdapter);
        productInfoTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isFirst){
                    switch (tab.getPosition()) {
                        case 0:
                            goodInfoWebview.setVisibility(View.VISIBLE);
                            rvComments.setVisibility(View.GONE);
                            break;
                        case 1:
                            goodInfoWebview.setVisibility(View.GONE);
                            rvComments.setVisibility(View.VISIBLE);
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
        productInfoTab.removeAllTabs();
        productInfoTab.addTab(productInfoTab.newTab().setText(getString(R.string.detail)));
        productInfoTab.addTab(productInfoTab.newTab().setText(getString(R.string.comments)));

        loadComment(true);
    }

    private void loadComment(boolean isFirst) {
        loadComment(isFirst, false);
    }

    private void loadComment(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productID", productID);
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE * 4);
        ApiManager.get().getData(Const.GET_PRODUCT_COMMENTS_LIST_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("commentsList");
                    if (!isLoadMore) {
                        productCommentBeans.clear();
                        commentAdapter.notifyDataSetChanged();
                    }
                    List<ProductCommentBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductCommentBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productCommentBeans.addAll(list);
                        commentAdapter.notifyDataSetChanged();
                        rvComments.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore && productInfoTab.getSelectedTabPosition() == 1) {
                        if (list != null && list.size() > 0) {
                            rvComments.setVisibility(View.VISIBLE);
                        } else {
                            rvComments.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(ProductInfoActivity.this, msg);
            }
        }, null);

    }

    private void loadPage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productID", productID);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.GET_PRODUCT_DETAIL_TAG, jsonObject,
                new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
                            //测试
                            productInfoBean = JsonUtils.jsonToObject(jo.toString(), ProductInfoBean.class);
//                            jo.optString("productID");
//                            shopID = jo.optString("shopID");
                            product.setProductID(jo.optString("productID"));
//                            product.setDescribe(jo.optString("describe"));
                            product.setPrice(jo.optString("price"));
                            product.setSpecialPrice(jo.optString("specialPrice"));
                            product.setSuppliers(jo.optString("suppliers"));
                            product.setSuppliersID(jo.optString("suppliersID"));
                            product.setStatus(jo.optString("status"));
//                            product.setProductUrl(jo.optString("productUrl"));
////                            productBean.setGroupID(skuId);
                            initData(jo);
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(ProductInfoActivity.this, msg);
                    }
                }, loadingView);
    }

    /**
     * 显示数据
     */
    private void initData(JSONObject jo) {
        /**************banner***************/
        JSONArray bannerJa = jo.optJSONArray("imgArr");
        bannerList.clear();
        for (int i = 0; i < bannerJa.length(); i++) {
            if (i == 0) {
                product.setImageUrl(bannerJa.optJSONObject(i).optString("imageUrl"));
            }
            bannerList.add(bannerJa.optJSONObject(i).optString("imageUrl"));
        }
        goodInfoBanner.setData(bannerList, null);

        /**************商品标题、售价、选择规格栏***************/
        tvProductTitle.setText(jo.optString("productName"));
        tvGoodDesc.setText(jo.optString("describe"));
        tvGoodPrice.setText(getString(R.string.price, jo.optString("specialPrice")));
        tvGoodOriginalPrice.setText(getString(R.string.price, jo.optString("price")));
        tvGoodOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvSaleNumber.setText(getString(R.string.hot_sale_number, jo.optString("sales")));

        tvSupplier.setText(jo.optString("suppliers"));


        String rebates = jo.optString("rebates");
        String buyText;
        if (TextUtils.isEmpty(rebates)) {
            buyText = "立即购买";
        } else {
            buyText = "立即购买" + "<br/>" + "<font color='white'>" + "<small><small><small>" + "预计可返" + rebates + "元" + "</small></small></small>" + "</font>";
        }
        tvBuyNow.setText(Html.fromHtml(buyText));

        JSONArray priceGroupJa = jo.optJSONArray("priceGroup");
        JSONArray speItemJa = jo.optJSONArray("specifications");

        if (null != priceGroupJa && null != speItemJa) {
//            priceGroups = new Gson().fromJson(priceGroupJa.toString(), new TypeToken<List<PriceGroupBean>>() {
//            }.getType());

//            skuBeans = new Gson().fromJson(speItemJa.toString(), new TypeToken<List<SkuBean>>() {
//            }.getType());
            priceGroup.clear();
            speItem.clear();
            for (int i = 0; i < priceGroupJa.length(); i++) {
                priceGroup.add(new PriceGroupModel(priceGroupJa.optJSONObject(i)));
            }
            for (int i = 0; i < speItemJa.length(); i++) {
                ProductSkuModel productSkuModel = new ProductSkuModel(speItemJa.optJSONObject(i));
                productSkuModel.pos = i;
                speItem.add(productSkuModel);
            }
            skuIdList = new String[speItem.size()];
            skuNameList = new String[speItem.size()];
        }
        /**************商品详情H5***************/
        goodInfoWebview.loadUrl(jo.optString("productUrl"));
//        goodInfoWebview.loadUrl("http://www.oulachina.com/index.php/goodsInfo/id/1017.html");
        //test
//        goodInfoWebview.loadUrl("https://www.oulachina.com/h5/qd/index.html");
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadComment(false, true);
    }

    private class BaseWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            goodInfoWebview.loadUrl(url);
//            int w = View.MeasureSpec.makeMeasureSpec(0,
//                    View.MeasureSpec.UNSPECIFIED);
//            int h = View.MeasureSpec.makeMeasureSpec(0,
//                    View.MeasureSpec.UNSPECIFIED);
//            //重新测量
//            goodInfoWebview.measure(w, h);
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
//            goodInfoWebview.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
//            timer = new Timer();
//            timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    goodInfoWebview.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
//                }
//            };
//            timer.schedule(timerTask,0,1000);
//            goodInfoWebview.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");

            goodInfoWebview.setVisibility(View.VISIBLE);
            isFirst = false;
//            timer = new Timer();
//            timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    goodInfoWebview.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
//                }
//            };
//            timer.schedule(timerTask,0,1000);
//            goodInfoWebview.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();  //接受所有证书
            super.onReceivedSslError(view, handler, error);
        }

    }

//    private class BaseWebChromeClient extends WebChromeClient {
//        @Override
//        public void onProgressChanged(com.tencent.smtt.sdk.WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
//            if (newProgress == 100) {
//                goodInfoWebview.setVisibility(View.VISIBLE);
//            } else {
//                goodInfoWebview.setVisibility(View.GONE);
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_sku:
                status = 0;
                showMallSkuDialog();
                break;
            case R.id.tv_share:
                CommonUtils.showShareBottomSheetGrid(ProductInfoActivity.this, Const.PRODUCT_SHARE_URL + "id/" + product.getProductID(), 0);
//                showSkuDialog();
                break;
            case R.id.tv_custom_service:
//                showSkuDialog();
                showTelephony(LoginHandler.get().getBaseUrlBean().getServiceMobile());
                break;
            case R.id.tv_add_cart:
                status = 2;
                if (CommonUtils.checkLogin(this) /*&& CommonUtils.checkCertification(this)*/) {
                    if (num == 0) {
                        showMallSkuDialog();
                    } else {
                        addToCart();
                    }
                }
                break;
            case R.id.tv_buy_now:
//                Intent intent = new Intent(ProductInfoActivity.this, ConfirmOrderActivity.class);
//                startActivity(intent);
                status = 1;
                if (CommonUtils.checkLogin(this)/* && CommonUtils.checkCertification(this)*/) {
                    if (num == 0) {
                        showMallSkuDialog();
                    } else {
                        goToBuy();
                    }
                }
                break;
        }
    }

    private void showSkuDialog() {
        SkuDialog skuDialog = new SkuDialog(ProductInfoActivity.this, productInfoBean);
        skuDialog.show();
    }


    public class LabelItem {
        private String speTitle;
        private String speID;
        private String speName;
        private String speImgUrl;
        private boolean isSelected = false;
        private boolean hasStock;

        public String getSpeTitle() {
            return speTitle;
        }

        public void setSpeTitle(String speTitle) {
            this.speTitle = speTitle;
        }

        public String getSpeID() {
            return speID;
        }

        public void setSpeID(String speID) {
            this.speID = speID;
        }

        public String getSpeName() {
            return speName;
        }

        public void setSpeName(String speName) {
            this.speName = speName;
        }

        public String getSpeImgUrl() {
            return speImgUrl;
        }

        public void setSpeImgUrl(String speImgUrl) {
            this.speImgUrl = speImgUrl;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public boolean isHasStock() {
            return hasStock;
        }

        public void setHasStock(boolean hasStock) {
            this.hasStock = hasStock;
        }
    }

    private void showMallSkuDialog() {
        if (!TextUtils.isEmpty(skuContent)) {
            for (int i = 0; i < priceGroup.size(); i++) {   //  寻找选中的规格
                if (TextUtils.equals(priceGroup.get(i).groupContent, skuContent)) {
                    String[] idList = priceGroup.get(i).groupContent.split("_");
                    /*********将选中的id和name重置为最近一次点击确定选择的*********/
                    for (int k = 0; k < speItem.size(); k++) {
                        ArrayList<ProductSkuLabelModel> labelList = speItem.get(k).labelList;
                        for (int j = 0; j < labelList.size(); j++) {
                            if (Arrays.asList(idList).contains(labelList.get(j).speID)) {
                                skuIdList[k] = labelList.get(j).speID;
                                skuNameList[k] = labelList.get(j).specName;
                                labelList.get(j).selected = 1;
                                speItem.get(k).selectItem = labelList.get(j);
                            } else {
                                labelList.get(j).selected = 0;
                            }
                        }
                    }
                    /*************************************************************/
                    break;
                }
            }
        } else {
            for (int i = 0; i < speItem.size(); i++) {
                skuIdList[i] = "";
                skuNameList[i] = "";
                speItem.get(i).selectItem = null;
                ArrayList<ProductSkuLabelModel> labelList = speItem.get(i).labelList;
                for (int j = 0; j < labelList.size(); j++) {
                    labelList.get(j).selected = 0;
                }
            }
        }
        MallSkuDialog mallSkuDialog = new MallSkuDialog(this, speItem, priceGroup, skuIdList, skuNameList, num == 0 ? 1 : num, product.getSpecialPrice(), product.getImageUrl());
        mallSkuDialog.setOnConfirmListener(new MallSkuDialog.OnConfirmListener() {
            @Override
            public void confirm(String groupContent, String skuGroup, String skuFullGroup, String price, int number) {
                /*skuId = groupID;*/
                skuContent = groupContent;
                num = number;
                if (TextUtils.isEmpty(skuGroup)) {
                    tvSku.setText(number + "件");
                } else {
                    tvSku.setText(number + "件，" + skuGroup);
                }
                tvSelect.setText(getString(R.string.selected));
                if (/*groupID.length() != 0 && */speItem.size() != 0) {
                    product.setPrice(price);
                    product.setSpe(skuFullGroup);
                    product.setGroupContent(skuContent);
                    SpannableStringBuilder sp = new SpannableStringBuilder(getString(R.string.price, price));
                    sp.setSpan(new AbsoluteSizeSpan(DisplayUtils.sp2px(ProductInfoActivity.this, 12)), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    tvGoodPrice.setText(sp);
                }
                product.setProductNum(number + "");
                if (status == 0) {

                } else if (status == 1) {
                    status = 0;
                    goToBuy();
                } else if (status == 2) {
                    addToCart();
                }
            }
        });
        mallSkuDialog.setMaxNum(999);
        mallSkuDialog.show();
    }

    private void addToCart() {
        if (CommonUtils.checkLogin(this) /*&& CommonUtils.checkCertification(this)*/) {
            JSONObject jo = new JSONObject();
            jo.put("userID", LoginHandler.get().getUserId());
            jo.put("suppliersID", product.getSuppliersID());
            jo.put("productID", productID);
            jo.put("groupContent", product.getGroupContent());
            jo.put("productNum", num);
            ApiManager.get().getData(Const.ADD_SHOP_CART_PRODUCT_TAG, jo,
                    new BusinessBackListener() {
                        @Override
                        public void onBusinessSuccess(BaseResponseModel responseModel) {
                            ToastUtils.showLongToast(ProductInfoActivity.this, responseModel.resultCodeDetail);
                        }

                        @Override
                        public void onBusinessError(int code, String msg) {
                            ToastUtils.showContinuationToast(ProductInfoActivity.this, msg);
//                            Toast.makeText(ProductInfoActivity.this,msg,Toast.LENGTH_LONG).show();
                        }
                    }, new LoadingDialog.Builder(this).setMessage(getString(R.string.submitting_tips)).create());
        }
    }

    private void goToBuy() {
        if (CommonUtils.checkLogin(this) /*&& CommonUtils.checkCertification(this)*/) {
            Intent intent = new Intent(this, ConfirmOrderActivity.class);
            ArrayList<CartBean.ShoppingCartBean> objects = new ArrayList<>();
            CartBean.ShoppingCartBean shopProduct = new CartBean.ShoppingCartBean();
            ArrayList<CartBean.Product> selectCartProductModels = new ArrayList<>();
            selectCartProductModels.add(product);
            shopProduct.setSuppliers(product.getSuppliers());
            shopProduct.setSuppliersID(product.getSuppliersID());
            shopProduct.setProductList(selectCartProductModels);
            objects.add(shopProduct);
            intent.putExtra(Const.PRODUCT_LIST_KEY, objects);
            intent.putExtra(Const.COMMIT_ORDER_ENTER_TYPE, 0);
            startActivity(intent);
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
                        requestPermissions(ProductInfoActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                new RequestPermissionCallBack() {
                                    @Override
                                    public void granted() {
                                        try {
                                            Uri uri = Uri.parse("tel:" + tel);
                                            Intent telIntent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(telIntent);
                                        } catch (ActivityNotFoundException a) {
                                            ToastUtils.showShortToast(ProductInfoActivity.this, getString(R.string.tel_confirm));
                                        }
                                    }

                                    @Override
                                    public void denied() {
                                        ToastUtils.showShortToast(ProductInfoActivity.this, "获取权限失败，正常功能受到影响");
                                    }

                                });

                    }
                })
                .create()
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.LoginNotice loginNotice) {
        loadPage();
    }

    @JavascriptInterface
    public void resize(final float height) {
        goodInfoWebview.post(new Runnable() {
            @Override
            public void run() {
                goodInfoWebview.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }
}
