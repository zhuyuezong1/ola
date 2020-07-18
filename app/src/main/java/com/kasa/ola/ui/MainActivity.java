package com.kasa.ola.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.github.shenyuanqing.zxingsimplify.zxing.Activity.CaptureActivity;
import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CheckOrderInfoBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.DoubleBtnCommonDialog;
import com.kasa.ola.dialog.HomeNoticeDialog;
import com.kasa.ola.dialog.NoticeDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.fragment.BusinessesFragment;
import com.kasa.ola.ui.fragment.CartFragment;
import com.kasa.ola.ui.fragment.HomeFragment;
import com.kasa.ola.ui.fragment.HomeOldFragment;
import com.kasa.ola.ui.fragment.MineFragment;
import com.kasa.ola.ui.fragment.MineNewFragment;
import com.kasa.ola.ui.fragment.MineYImagePickerFragment;
import com.kasa.ola.ui.fragment.NativeFragment;
import com.kasa.ola.ui.fragment.ShopAllowedFragment;
import com.kasa.ola.ui.fragment.ShopFragment;
import com.kasa.ola.ui.fragment.StoreOperationFragment;
import com.kasa.ola.utils.ClipBoardUtil;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.EncryptUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements BaseActivity.EventBusListener, View.OnClickListener {

    @BindView(R.id.vp_container)
    FrameLayout vpContainer;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.ll_mine)
    LinearLayout llMine;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.tv_shop)
    TextView tvShop;
    @BindView(R.id.ll_shop)
    LinearLayout llShop;
    @BindView(R.id.tv_shop_manager)
    TextView tvShopManager;
    @BindView(R.id.ll_shop_manager)
    LinearLayout llShopManager;
    @BindView(R.id.tv_cart)
    TextView tvCart;
    @BindView(R.id.ll_cart)
    LinearLayout llCart;
    //    @BindView(R.id.bottom_bar)
//    TabLayoutView bottomBar;
    private int[] imgs = {R.drawable.nav_home_selector, R.drawable.nav_mall_selector, R.drawable.nav_cart_selector, R.drawable.nav_order_selector, R.drawable.nav_mine_selector}; //新版的导航栏图标
    private static int currentIndex = 0;//tab切换
    private List<Fragment> fragmentList = new ArrayList<>();
    private Fragment currentFragment;
    private boolean isFirst = true;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;
    private boolean isShop = true;
    private boolean haveNoPermission;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double longitude;
    private double latitude;
    private String businessLvl;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        currentIndex = intent.getIntExtra(Const.MAIN_ACTIVITY_TAG, 0);
        initFragments();
        initView();
        initNoticeRight();
        getLocation();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {
        if (haveNoPermission) {
            startLocation();
        } else {
            requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, new BaseActivity.RequestPermissionCallBack() {
                @Override
                public void granted() {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    startLocation();
                }

                @Override
                public void denied() {
                    ToastUtils.showShortToast(MainActivity.this, "获取权限失败，正常功能受到影响");
                }
            });
        }
    }
    private void startLocation() {

        mlocationClient = new AMapLocationClient(MainActivity.this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mLocationOption.setOnceLocationLatest(true);
        mlocationClient.setLocationListener(new AMapLocationListener() {


            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        //获取纬度
                        latitude = amapLocation.getLatitude();
                        //获取经度
                        longitude = amapLocation.getLongitude();
                        amapLocation.getAccuracy();//获取精度信息
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(amapLocation.getTime());
                        df.format(date);//定位时间
                        LoginHandler.get().saveLongitude(longitude+"");
                        LoginHandler.get().saveLatitude(latitude+"");
//                        CoordinateConverter converter  = new CoordinateConverter(MainActivity.this);
//                        // CoordType.GPS 待转换坐标类型
//                        converter.from(CoordinateConverter.CoordType.GPS);
//                        DPoint dPoint = new DPoint(latitude,longitude);
//                        try {
//                            converter.coord(dPoint);
//                            DPoint convert = converter.convert();
//                            LoginHandler.get().saveLongitude(convert.getLongitude()+"");
//                            LoginHandler.get().saveLatitude(convert.getLatitude()+"");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                                ToastUtils.showLongToast(getContext(),amapLocation.getLatitude()+"---"+amapLocation.getLongitude());
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }


                }
            }
        });
        //定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
////设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }
    private void initView() {
        haveNoPermission = checkPermissions(MainActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});

        llHome.setOnClickListener(this);
        llShop.setOnClickListener(this);
        llShopManager.setOnClickListener(this);
        llCart.setOnClickListener(this);
        llMine.setOnClickListener(this);
//        bottomBar.setDataSource(titles, imgs, 0);
//        bottomBar.setTextStyle(13, R.color.COLOR_FF9A9A9A, R.color.COLOR_FF0D91BF);
//        bottomBar.initDatas();
//        bottomBar.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
//            @Override
//            public void onItemClick(int index) {
//                if (index ==2 || index==3 || index==4) {
//                    if (CommonUtils.checkLogin(MainActivity.this)) {
//                       switchFragment(index);
//                    }
//                } else {
//                    switchFragment(index);
//                }
//            }
//
//            @Override
//            public void onVerticalItemClick(int index, View view) {
//
//            }
//        });
        getPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        String s = EncryptUtils.encryptPassword("1001");
//        LogUtil.d("1111111111",s);
//        String s1 = EncryptUtils.decryptPassword(s);
//        LogUtil.d("1111111111",s1);
        this.getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    String paste = ClipBoardUtil.paste();
                    ClipBoardUtil.clear();
                    if (!TextUtils.isEmpty(paste)) {
                        String[] arr = paste.split("¢");
                        if (arr!=null && arr.length==3){
                            String productIDEncode = arr[1];
                            String productID = EncryptUtils.decryptPassword(productIDEncode);
                            if (!TextUtils.isEmpty(productID)){
                                DoubleBtnCommonDialog.Builder builder = new DoubleBtnCommonDialog.Builder(MainActivity.this);
                                builder.setTitle(getString(R.string.home_to_product_dialog_title))
                                        .setMessage(getString(R.string.home_to_product_dialog_content))
                                        .setLeftButton(getString(R.string.cancel))
                                        .setRightButton(getString(R.string.confirm))
                                        .setDialogInterface(new DoubleBtnCommonDialog.DialogInterface() {
                                            @Override
                                            public void leftButtonClick(DoubleBtnCommonDialog dialog) {
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void rightButtonClick(DoubleBtnCommonDialog dialog) {
                                                dialog.dismiss();
                                                Intent homeIntent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                                                homeIntent.putExtra(Const.MALL_GOOD_ID_KEY, productID);
                                                startActivity(homeIntent);
                                            }
                                        }).create().show();
                            }

                        }
//                        Pattern p = Pattern.compile("¢(.*)(?)¢");
//                        Matcher m = p.matcher(paste);
//                        if(m.find()){
//                            MatchResult matchResult = m.toMatchResult();
//                            LogUtil.d("1111111111",matchResult.toString());
//                        }
//                        Intent homeIntent = new Intent(MainActivity.this, ProductDetailsActivity.class);
//                        homeIntent.putExtra(Const.MALL_GOOD_ID_KEY, "");
//                        startActivity(homeIntent);
                    }
                }
            });

        if (!pBar.isShowing()) {
            if (!checkUpdate(true)) {
                if (LoginHandler.get().getBaseUrlBean().getIsHaveNotice() == 1 && isFirst &&
                        LoginHandler.get().getShownNoticeVersion() < LoginHandler.get().getBaseUrlBean().getNoticeVersion() &&
                        !TextUtils.isEmpty(LoginHandler.get().getBaseUrlBean().getNoticeUrl())) {
                    isFirst = false;
                    NoticeDialog noticeDialog = new NoticeDialog(this);
                    noticeDialog.show();
                }
            }
        }
        if (currentIndex > -1) {
            switchFragment(currentIndex);
//            currentIndex = -1;
        }
    }

    private void initFragments() {
        fragmentList.clear();
        businessLvl = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("businessLvl"))?"0":LoginHandler.get().getMyInfo().optString("businessLvl");
        if (businessLvl.equals("1")|| businessLvl.equals("3")){
            tvShopManager.setText(R.string.bottom_shop_manager);
            tvShopManager.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.nav_special_selector),null,null);
        }else if (businessLvl.equals("0")|| businessLvl.equals("2")){
            tvShopManager.setText(R.string.shop_allowed);
            tvShopManager.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.nav_shop_allowed_selector),null,null);
        }else {
            tvShopManager.setText(R.string.shop_allowed);
            tvShopManager.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.nav_shop_allowed_selector),null,null);
        }
        HomeFragment homeFragment = new HomeFragment();
//        MallFragment mallFragment = new MallFragment();
//        OrganFragment organFragment = new OrganFragment();
//        CartFragment cartFragment = new CartFragment();
//        OrderFragment orderFragment = new OrderFragment();
//        EducationFragment educationFragment = new EducationFragment();
//        SameCityFragment sameCityFragment = new SameCityFragment();
//        ShopFragment shopFragment = new ShopFragment();
        //暂时
        BusinessesFragment businessesFragment = new BusinessesFragment();
        ShopAllowedFragment shopAllowedFragment = new ShopAllowedFragment();
        StoreOperationFragment storeOperationFragment = new StoreOperationFragment();
//        HomeOldFragment homeOldFragment = new HomeOldFragment();
        MineNewFragment mineNewFragment = new MineNewFragment();
        MineYImagePickerFragment mineYImagePickerFragment = new MineYImagePickerFragment();
        CartFragment cartFragment = new CartFragment();
//        MineFragment mineFragment = new MineFragment();
        fragmentList.add(homeFragment);
//        fragmentList.add(shopFragment);
        fragmentList.add(businessesFragment);
        if (businessLvl.equals("1") || businessLvl.equals("3")){
            fragmentList.add(storeOperationFragment);
        }else if (businessLvl.equals("0")|| businessLvl.equals("2")){
            fragmentList.add(shopAllowedFragment);
        }else {
            fragmentList.add(shopAllowedFragment);
        }
//        fragmentList.add(homeOldFragment);
        fragmentList.add(cartFragment);
//        fragmentList.add(mineFragment);
        fragmentList.add(mineYImagePickerFragment);
    }

    public void switchFragment(int whichFragment) {
        Fragment fragment = fragmentList.get(whichFragment);
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (currentFragment != null) {
                    transaction.hide(currentFragment).show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (currentFragment != null) {
                    transaction.hide(currentFragment).add(R.id.vp_container, fragment);
                } else {
                    transaction.add(R.id.vp_container, fragment);
                }
            }
            currentFragment = fragment;
            transaction.commit();
        }
//        bottomBar.setSelectStyle(whichFragment);
        setSelected(whichFragment);
    }

    private void setSelected(int whichFragment) {
        resetTextView();
        switch (whichFragment) {
            case 0:
                tvHome.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                tvHome.setSelected(true);
                break;
            case 1:
                tvShop.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                tvShop.setSelected(true);
                break;
            case 2:
                tvShopManager.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                tvShopManager.setSelected(true);
                break;
            case 3:
                tvCart.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                tvCart.setSelected(true);
                break;
            case 4:
                tvMine.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                tvMine.setSelected(true);
                break;
        }
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE}, new RequestPermissionCallBack() {
                @Override
                public void granted() {

                }

                @Override
                public void denied() {

                }

            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.HomeSwitch homeSwitch) {
        EventBus.getDefault().post(new EventCenter.MainBottonClickCurrent(0));
        currentIndex = homeSwitch.getIndex();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.LoginNotice loginNotice) {
       initFragments();
    }
    @Override
    public void onClick(View v) {
        bottomClick(v);
    }

    private void bottomClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home:
                if (currentIndex == 0) {
                    EventBus.getDefault().post(new EventCenter.MainBottonClickCurrent(0));
                } else {
                    switchFragment(0);
                    currentIndex = 0;
                }
                break;
            case R.id.ll_shop:
                switchFragment(1);
                currentIndex = 1;
                break;
            case R.id.ll_shop_manager:
                if (CommonUtils.checkLogin(MainActivity.this)) {
                    switchFragment(2);
                    currentIndex = 2;
                }
                break;
            case R.id.ll_cart:
                if (CommonUtils.checkLogin(MainActivity.this)) {
                    switchFragment(3);
                    currentIndex = 3;
                }
                break;
            case R.id.ll_mine:
                if (CommonUtils.checkLogin(MainActivity.this)) {
                    switchFragment(4);
                    currentIndex = 4;
                }
                break;

        }
    }

    private void resetTextView() {
        tvHome.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
        tvShop.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
        tvShopManager.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
        tvCart.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
        tvMine.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
        tvHome.setSelected(false);
        tvShop.setSelected(false);
        tvShopManager.setSelected(false);
        tvCart.setSelected(false);
        tvMine.setSelected(false);
    }

    public void scanCode() {
        requestPermissions(this, new String[]{Manifest.permission.CAMERA}, new RequestPermissionCallBack() {
            @Override
            public void granted() {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, Const.SCAN_CODE);
            }

            @Override
            public void denied() {
                ToastUtils.showShortToast(MainActivity.this, "获取权限失败，正常功能受到影响");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.SCAN_CODE) {
//                Intent intent = new Intent(MainActivity.this, VerificationActivity.class);
//                startActivityForResult(intent,Const.CONTINUE_VERIFICATION);
                if (data != null) {
                    final String orderNo = data.getStringExtra("barCode");
                    if (!TextUtils.isEmpty(orderNo)) {
                        if (orderNo.contains("_")) {
                            String[] s = orderNo.split("_");
                            if (s.length > 1) {
                                if (s[0].equals("ola")) {
                                    getCheckOrderInfo(s[1]);
                                } else {
                                    ToastUtils.showLongToast(MainActivity.this, getString(R.string.scan_qr_code_error_tip));
                                }
                            }
                        }

//                        CommonDialog.Builder builder = new CommonDialog.Builder(MainActivity.this);
//                        builder.setMessage(MainActivity.this.getString(R.string.is_confirm_write_off_order))
//                                .setLeftButton(getString(R.string.cancel))
//                                .setRightButton(getString(R.string.confirm))
//                                .setDialogInterface(new CommonDialog.DialogInterface() {
//
//                                    @Override
//                                    public void leftButtonClick(CommonDialog dialog) {
//                                        dialog.dismiss();
//                                    }
//
//                                    @Override
//                                    public void rightButtonClick(CommonDialog dialog) {
//                                        if (orderNo.contains("_")) {
//                                            String[] s = orderNo.split("_");
//                                            if (s.length > 1) {
//                                                if (s[0].equals("ola")) {
//                                                    writeOffOrder(s[1]);
//                                                } else {
//                                                    ToastUtils.showLongToast(MainActivity.this, getString(R.string.scan_qr_code_error_tip));
//                                                }
//                                            }
//                                        }
//
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .create()
//                                .show();

                    } else {
                        ToastUtils.showLongToast(MainActivity.this, getString(R.string.scan_right_code_tip));
                    }
                }
            }else if (requestCode == Const.CONTINUE_VERIFICATION){
                scanCode();
            }
        }
    }
    private void getCheckOrderInfo(String orderNo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("orderNo", orderNo);
        ApiManager.get().getData(Const.GET_CHECK_ORDER_INFO, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data!=null && responseModel.data instanceof JSONObject){
                    JSONObject jo = (JSONObject) responseModel.data;
                    CheckOrderInfoBean checkOrderInfoBean = JsonUtils.jsonToObject(jo.toString(), CheckOrderInfoBean.class);
                    if (checkOrderInfoBean.getOrderStatus().equals("0")){
                        Intent intent = new Intent(MainActivity.this, VerificationActivity.class);
                        intent.putExtra(Const.CHECK_ORDER_INFO,checkOrderInfoBean);
                        intent.putExtra(Const.CHECK_ORDER_NO,orderNo);
                        startActivityForResult(intent,Const.CONTINUE_VERIFICATION);
                    }else if (checkOrderInfoBean.getOrderStatus().equals("1")){
                        Intent scanFailIntent = new Intent(MainActivity.this, ScanFailActivity.class);
                        scanFailIntent.putExtra(Const.CHECK_ORDER_INFO,checkOrderInfoBean);
                        startActivity(scanFailIntent);
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(MainActivity.this,msg);
            }
        },null);
    }
//    private void writeOffOrder(String orderNo) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userID", LoginHandler.get().getUserId());
//        jsonObject.put("orderNo", orderNo);
//        ApiManager.get().getData(Const.VERIFICATION_ORDER, jsonObject, new BusinessBackListener() {
//            @Override
//            public void onBusinessSuccess(BaseResponseModel responseModel) {
//                ToastUtils.showLongToast(MainActivity.this, getString(R.string.write_off_success_tips));
//            }
//
//            @Override
//            public void onBusinessError(int code, String msg) {
//                ToastUtils.showLongToast(MainActivity.this, msg);
//            }
//        }, null);
//    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.RefreshMyInfo refreshMyInfo) {
        businessLvl = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("businessLvl"))?"0":LoginHandler.get().getMyInfo().optString("businessLvl");
        if (businessLvl.equals("1")|| businessLvl.equals("3")){
            tvShopManager.setText(R.string.bottom_shop_manager);
            tvShopManager.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.nav_special_selector),null,null);
        }else if (businessLvl.equals("0")|| businessLvl.equals("2")){
            tvShopManager.setText(R.string.shop_allowed);
            tvShopManager.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.nav_shop_allowed_selector),null,null);
        }else {
            tvShopManager.setText(R.string.shop_allowed);
            tvShopManager.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.nav_shop_allowed_selector),null,null);
        }
        ShopAllowedFragment shopAllowedFragment = new ShopAllowedFragment();
        StoreOperationFragment storeOperationFragment = new StoreOperationFragment();
        if (businessLvl.equals("1") || businessLvl.equals("3")){
            fragmentList.set(2,storeOperationFragment);
        }else if (businessLvl.equals("0")|| businessLvl.equals("2")){
            fragmentList.add(shopAllowedFragment);
        }else {
            fragmentList.add(shopAllowedFragment);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                ToastUtils.showLongToast(MainActivity.this, getString(R.string.exit_app_tips));
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initNoticeRight() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(App.getApp());
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isOpened) {
            CommonDialog.Builder builder = new CommonDialog.Builder(MainActivity.this);
            builder.setMessage(MainActivity.this.getString(R.string.go_to_open_app_notice))
                    .setLeftButton(MainActivity.this.getString(R.string.cancel_pay))
                    .setRightButton(MainActivity.this.getString(R.string.confirm_pay))
                    .setDialogInterface(new CommonDialog.DialogInterface() {

                        @Override
                        public void leftButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void rightButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", App.getApp().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();

        } else {
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // 停止定位
        if (null != mlocationClient) {
            mlocationClient.stopLocation();
        }
    }
    @Override
    public void onDestroy() {
        destroyLocation();
        super.onDestroy();
    }

    /**
     * 销毁定位
     */
    private void destroyLocation() {
        if (null != mlocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mlocationClient.onDestroy();
            mlocationClient = null;
        }
    }
}
