package com.kasa.ola.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.bean.entity.ProductCommentBean;
import com.kasa.ola.bean.entity.ProductInfoBean;
import com.kasa.ola.bean.model.PriceGroupModel;
import com.kasa.ola.bean.model.ProductSkuLabelModel;
import com.kasa.ola.bean.model.ProductSkuModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.MallSkuDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProductCommentAdapter;
import com.kasa.ola.ui.adapter.ProductCommentImageAdapter;
import com.kasa.ola.ui.adapter.ProductDetailsAdapter;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;
import com.kasa.ola.widget.banner.BannerDataBean;
import com.kasa.ola.widget.banner.MultipleTypesAdapter;
import com.kasa.ola.widget.banner.VideoHolder;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.youth.banner.Banner;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectFirstProductDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @BindView(R.id.tv_product_desc)
    TextView tvProductDesc;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_original_price)
    TextView tvOriginalPrice;
    @BindView(R.id.tv_back_value)
    TextView tvBackValue;
    @BindView(R.id.tv_supplier)
    TextView tvSupplier;
    @BindView(R.id.tv_sku)
    TextView tvSku;
    @BindView(R.id.ll_sku)
    LinearLayout llSku;
    @BindView(R.id.tv_check_more)
    TextView tvCheckMore;
    @BindView(R.id.rv_product_details)
    RecyclerView rvProductDetails;
    @BindView(R.id.nsv_products)
    NestedScrollView nsvProducts;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.tv_custom_service)
    TextView tvCustomService;
    @BindView(R.id.tv_cart)
    TextView tvCart;
    @BindView(R.id.tv_cart_product_num)
    TextView tvCartProductNum;
    @BindView(R.id.tv_add_cart)
    TextView tvAddCart;
    @BindView(R.id.tv_buy)
    TextView tvBuy;
    @BindView(R.id.ll_login_bottom)
    LinearLayout llLoginBottom;
    @BindView(R.id.tv_buy_no_login)
    TextView tvBuyNoLogin;
    @BindView(R.id.ll_product)
    LinearLayout llProduct;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_sale_number)
    TextView tvSaleNumber;
    @BindView(R.id.rv_product_comments)
    RecyclerView rvProductComments;
    private ArrayList<BannerDataBean> bannerList = new ArrayList<>();
    StandardGSYVideoPlayer player;
    private int mBannerHeight;
    private List<String> commentImages = new ArrayList<>();
    private ProductCommentImageAdapter productCommentImageAdapter;
    private int dy;
    private List<String> productImages = new ArrayList<>();
    private int status = 0;//0,无操作，1，去购买，2，加入购物车
    private String productID;
    private int num = 0;
    private List<ProductCommentBean> productCommentBeans = new ArrayList<>();
    private ProductInfoBean productInfoBean;
    private CartBean.Product product = new CartBean.Product();
    private ProductCommentAdapter productCommentAdapter;
    private ProductDetailsAdapter productDetailsAdapter;
    private ArrayList<PriceGroupModel> priceGroup = new ArrayList<>();
    private ArrayList<ProductSkuModel> speItem = new ArrayList<>();
    private String[] skuIdList;
    private String[] skuNameList;
    private String skuContent = "";
    //    private ArrayList<UserViewInfo> bannerImages;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        productID = getIntent().getStringExtra(Const.MALL_GOOD_ID_KEY);
        setContentView(R.layout.activity_select_first_product_details);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        loadPage();
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        llSku.setOnClickListener(this);
        tvCustomService.setOnClickListener(this);
        tvCart.setOnClickListener(this);
        tvAddCart.setOnClickListener(this);
        tvBuy.setOnClickListener(this);
        tvBuyNoLogin.setOnClickListener(this);
        tvCheckMore.setOnClickListener(this);
    }

    private void initTitle() {
        ivBack.setImageResource(R.mipmap.back_icon);
        ivBack.setOnClickListener(this);
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        ImmersionBar.with(this).titleBar(R.id.view_actionbar)
                .fitsSystemWindows(true)
                .navigationBarColor(android.R.color.black)
                .autoDarkModeEnable(true)
                .autoStatusBarDarkModeEnable(true, 0.2f)
                .init();
    }

    private void initView() {
        initViewByLogin();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) banner.getLayoutParams();
        int screenWidth = DisplayUtils.getScreenWidth(SelectFirstProductDetailsActivity.this);
        lp.width = screenWidth;
        lp.height = screenWidth;
        mBannerHeight = screenWidth - DisplayUtils.getStatusBarHeight2(SelectFirstProductDetailsActivity.this) - DisplayUtils.dip2px(SelectFirstProductDetailsActivity.this, 45);

        productCommentAdapter = new ProductCommentAdapter(SelectFirstProductDetailsActivity.this, productCommentBeans);
        rvProductComments.setAdapter(productCommentAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SelectFirstProductDetailsActivity.this);
        rvProductDetails.setLayoutManager(linearLayoutManager);

        productDetailsAdapter = new ProductDetailsAdapter(SelectFirstProductDetailsActivity.this, productImages);
        rvProductDetails.setAdapter(productDetailsAdapter);

        nsvProducts.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                dy += scrollY - oldScrollY;
                if (dy < 0) {
                    dy = 0;
                }
                if (dy < mBannerHeight) {
                    float alpha = (float) dy / mBannerHeight;
                    if (dy > 0) {
                        viewActionbar.setAlpha(alpha);
                        ivBack.setImageResource(R.mipmap.return_icon);
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    } else if (dy == 0) {
                        viewActionbar.setAlpha(1);
                        ivBack.setImageResource(R.mipmap.back_icon);
                        viewActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                    }
                    ImmersionBar.with(SelectFirstProductDetailsActivity.this)
                            .statusBarDarkFont(false)
                            .init();
                } else {
                    viewActionbar.setAlpha(1);
                    ivBack.setImageResource(R.mipmap.return_icon);
                    viewActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    ImmersionBar.with(SelectFirstProductDetailsActivity.this)
                            .statusBarDarkFont(true)
                            .init();
                }
            }
        });

        initData();
//        bannerImages = new ArrayList<>();
//        if (bannerList != null && bannerList.size() > 0) {
//            for (int i = 0; i < bannerList.size(); i++) {
//                UserViewInfo userViewInfo = new UserViewInfo(bannerList.get(i).imageUrl);
//                bannerImages.add(userViewInfo);
//            }
//        }
        banner.addBannerLifecycleObserver(this)
                .setAdapter(new MultipleTypesAdapter(this, bannerList))
                .setIndicator(new CircleIndicator(this))
                .setIndicatorSelectedColorRes(R.color.COLOR_FF1677FF)
                .setIndicatorNormalColorRes(R.color.white)
                .setIndicatorWidth(DisplayUtils.dip2px(this, 8), DisplayUtils.dip2px(this, 8))
                .setIndicatorHeight(DisplayUtils.dip2px(this, 8))
                .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
                .setIndicatorRadius(DisplayUtils.dip2px(this, 4))
                .addOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position != 0) {
                            GSYVideoManager.onPause();
                        } else {
                            GSYVideoManager.onResume();
                        }
                        if (player == null) {
                            RecyclerView.ViewHolder viewHolder = banner.getAdapter().getViewHolder();
                            if (viewHolder instanceof VideoHolder) {
                                VideoHolder holder = (VideoHolder) viewHolder;
                                player = holder.player;
                                if (position != 0) {
                                    player.onVideoPause();
                                } else {
                                }

                            }
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                }).setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
//                GPreviewBuilder.from(SelectFirstProductDetailsActivity.this)
//                        .setData(bannerImages)
//                        .setCurrentIndex(position)
//                        .setType(GPreviewBuilder.IndicatorType.Dot)
//                        .start();//启动
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null)
            player.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null)
            player.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        //释放所有
        if (player != null)
            player.setVideoAllCallBack(null);
        super.onBackPressed();
    }

    private void initData() {
        bannerList.add(new BannerDataBean("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4", "第一个放视频", 2));
        bannerList.add(new BannerDataBean("http://www.oulachina.com/public/upload/goods/202005/b5a772e5e1b65962e90ad1826a415d9a.jpg", "测试图片", 1));
        bannerList.add(new BannerDataBean("http://www.oulachina.com/public/upload/goods/202005/b5a772e5e1b65962e90ad1826a415d9a.jpg", "测试图片", 1));
        bannerList.add(new BannerDataBean("http://www.oulachina.com/public/upload/goods/202005/b5a772e5e1b65962e90ad1826a415d9a.jpg", "测试图片", 1));
        bannerList.add(new BannerDataBean("http://www.oulachina.com/public/upload/goods/202005/b5a772e5e1b65962e90ad1826a415d9a.jpg", "测试图片", 1));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_sku:
                status = 0;
                showMallSkuDialog();
                break;
            case R.id.tv_custom_service:
                showTelephony(LoginHandler.get().getBaseUrlBean().getServiceMobile());
                break;
            case R.id.tv_cart:
                if (CommonUtils.checkLogin(this)) {
                    Intent intent = new Intent(SelectFirstProductDetailsActivity.this, CartActivity.class);
                    startActivity(intent);
                }
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
            case R.id.tv_buy:
                status = 1;
                if (num == 0) {
                    showMallSkuDialog();
                } else {
                    goToBuy();
                }
                break;
            case R.id.tv_buy_no_login:
                CommonUtils.checkLogin(SelectFirstProductDetailsActivity.this);
                break;
            case R.id.tv_check_more:
                Intent productCommentIntent = new Intent(SelectFirstProductDetailsActivity.this, ProductCommentListActivity.class);
                productCommentIntent.putExtra(Const.MALL_GOOD_ID_KEY, productID);
                startActivity(productCommentIntent);
                break;
            case R.id.tv_right_text:
                CommonUtils.showShareBottomSheetGrid(SelectFirstProductDetailsActivity.this, productInfoBean.getShareText());
                break;
        }
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
                        ToastUtils.showShortToast(SelectFirstProductDetailsActivity.this, msg);
                    }
                }, loadingView);
    }

    /**
     * 显示数据
     */
    private void initData(JSONObject jo) {
        /**************banner***************/
//        JSONArray bannerJa = jo.optJSONArray("imgArr");
//        bannerList.clear();
//        for (int i = 0; i < bannerJa.length(); i++) {
//            if (i == 0) {
//                product.setImageUrl(bannerJa.optJSONObject(i).optString("imageUrl"));
//            }
//            bannerList.add(bannerJa.optJSONObject(i).optString("imageUrl"));
//        }
//        productBanner.setData(bannerList, null);
//        bannerImages = new ArrayList<>();
//        if (bannerList != null && bannerList.size() > 0) {
//            for (int i = 0; i < bannerList.size(); i++) {
//                UserViewInfo userViewInfo = new UserViewInfo(bannerList.get(i));
//                bannerImages.add(userViewInfo);
//            }
//        }

        /**************商品标题、售价、选择规格栏***************/
        tvProductName.setText(jo.optString("productName"));
        tvProductDesc.setText(jo.optString("describe"));
        tvPrice.setText(getString(R.string.price, jo.optString("specialPrice")));
        tvOriginalPrice.setText(getString(R.string.price, jo.optString("price")));
        tvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvSaleNumber.setText(getString(R.string.product_details_sale_num, jo.optString("sales")));
        tvSupplier.setText(jo.optString("suppliers"));
        if (productInfoBean.getComments() != null && productInfoBean.getComments().size() > 0) {
            productCommentBeans.clear();
            productCommentBeans.addAll(productInfoBean.getComments());
            productCommentAdapter.notifyDataSetChanged();
        }
        if (productInfoBean.getDetailImgArr() != null && productInfoBean.getDetailImgArr().size() > 0) {
            productImages.clear();
            productImages.addAll(productInfoBean.getDetailImgArr());
            productDetailsAdapter.notifyDataSetChanged();
        }
        if (!TextUtils.isEmpty(productInfoBean.getCartItemCount())) {
            tvCartProductNum.setText(productInfoBean.getCartItemCount());
        }

        String rebates = jo.optString("rebates");
        tvBackValue.setText(getString(R.string.back_value, rebates));

        JSONArray priceGroupJa = jo.optJSONArray("priceGroup");
        JSONArray speItemJa = jo.optJSONArray("specifications");

        if (null != priceGroupJa && null != speItemJa) {
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
                if (/*groupID.length() != 0 && */speItem.size() != 0) {
                    product.setPrice(price);
                    product.setSpe(skuFullGroup);
                    product.setGroupContent(skuContent);
                    SpannableStringBuilder sp = new SpannableStringBuilder(getString(R.string.price, price));
//                    sp.setSpan(new AbsoluteSizeSpan(DisplayUtils.sp2px(ProductDetailsActivity.this, 12)), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    tvPrice.setText(sp);
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
                            ToastUtils.showLongToast(SelectFirstProductDetailsActivity.this, responseModel.resultCodeDetail);
                        }

                        @Override
                        public void onBusinessError(int code, String msg) {
                            ToastUtils.showContinuationToast(SelectFirstProductDetailsActivity.this, msg);
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
                        requestPermissions(SelectFirstProductDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                new RequestPermissionCallBack() {
                                    @Override
                                    public void granted() {
                                        try {
                                            Uri uri = Uri.parse("tel:" + tel);
                                            Intent telIntent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(telIntent);
                                        } catch (ActivityNotFoundException a) {
                                            ToastUtils.showShortToast(SelectFirstProductDetailsActivity.this, getString(R.string.tel_confirm));
                                        }
                                    }

                                    @Override
                                    public void denied() {
                                        ToastUtils.showShortToast(SelectFirstProductDetailsActivity.this, "获取权限失败，正常功能受到影响");
                                    }

                                });

                    }
                })
                .create()
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.LoginNotice loginNotice) {
        llLoginBottom.setVisibility(View.VISIBLE);
        tvBuyNoLogin.setVisibility(View.GONE);
        tvBackValue.setVisibility(View.VISIBLE);
        loadPage();
    }

    private void initViewByLogin() {
        if (LoginHandler.get().checkLogined()) {
            llLoginBottom.setVisibility(View.VISIBLE);
            tvBuyNoLogin.setVisibility(View.GONE);
            tvBackValue.setVisibility(View.VISIBLE);
        } else {
            llLoginBottom.setVisibility(View.GONE);
            tvBuyNoLogin.setVisibility(View.VISIBLE);
            tvBackValue.setVisibility(View.GONE);
        }
    }
}
