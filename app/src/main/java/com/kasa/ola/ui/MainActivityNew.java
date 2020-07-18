package com.kasa.ola.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.shenyuanqing.zxingsimplify.zxing.Activity.CaptureActivity;
import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.HomeNoticeDialog;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.fragment.HomeFragment;
import com.kasa.ola.ui.fragment.MineFragment;
import com.kasa.ola.ui.fragment.NativeFragment;
import com.kasa.ola.ui.fragment.ShopFragment;
import com.kasa.ola.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityNew extends BaseActivity implements BaseActivity.EventBusListener, View.OnClickListener {
    @BindView(R.id.vp_container)
    FrameLayout vpContainer;
    @BindView(R.id.radio_home)
    RadioButton radioHome;
    @BindView(R.id.radio_shop)
    RadioButton radioShop;
    @BindView(R.id.radio_shop_manager)
    RadioButton radioShopManager;
    @BindView(R.id.radio_cart)
    RadioButton radioCart;
    @BindView(R.id.radio_mine)
    RadioButton radioMine;
    @BindView(R.id.bottom_radio)
    RadioGroup bottomRadio;
    private static int currentIndex = 0;//tab切换
    private List<Fragment> fragmentList = new ArrayList<>();
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private boolean isFirst = true;
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragments();
        initView();
        initEvent();
        initNoticeRight();
    }

    private void initEvent() {
        bottomRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_home:
                        switchFragment(0);
                        currentIndex = 0;
                        break;
                    case R.id.radio_shop:
                        switchFragment(1);
                        currentIndex = 1;
                        break;
                    case R.id.radio_shop_manager:
//                        switchFragment(1);
                        break;
                    case R.id.radio_cart:
                        switchFragment(2);
                        currentIndex = 2;
                        break;
                    case R.id.radio_mine:
                        switchFragment(3);
                        currentIndex = 3;
                        break;
                }
            }
        });
        radioHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex==0) {
                    EventBus.getDefault().post(new EventCenter.MainBottonClickCurrent(0));
                } else {

                }
            }
        });
    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        switchFragment(0);
        getPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!pBar.isShowing()) {
            if (!checkUpdate(true)) {
                if (LoginHandler.get().getBaseUrlBean().getIsHaveNotice() == 1 && isFirst &&
                        LoginHandler.get().getShownNoticeVersion() < LoginHandler.get().getBaseUrlBean().getNoticeVersion() &&
                        !TextUtils.isEmpty(LoginHandler.get().getBaseUrlBean().getNoticeUrl())) {
                    isFirst = false;
                    HomeNoticeDialog homeNoticeDialog = new HomeNoticeDialog(this);
                    homeNoticeDialog.show();
                }
            }
        }
        if (currentIndex > -1) {
            switchFragment(currentIndex);
//            currentIndex = -1;
        }
    }

    private void initFragments() {
        HomeFragment homeFragment = new HomeFragment();
        ShopFragment shopFragment = new ShopFragment();
        NativeFragment nativeFragment = new NativeFragment();
        MineFragment mineFragment = new MineFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(shopFragment);
        fragmentList.add(nativeFragment);
        fragmentList.add(mineFragment);
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
    }



    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            requestPermissions(MainActivityNew.this, new String[]{
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
        currentIndex = homeSwitch.getIndex();
    }

    @Override
    public void onClick(View v) {
//        bottomClick(v);
    }

//    private void bottomClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_home:
//                if (currentIndex == 0) {
//                    EventBus.getDefault().post(new EventCenter.MainBottonClickCurrent(0));
//                } else {
//                    switchFragment(0);
//                    currentIndex = 0;
//                }
//                break;
//            case R.id.ll_mall:
//                switchFragment(1);
//                currentIndex = 1;
//                break;
//            case R.id.ll_activity:
////                if (CommonUtils.checkLogin(MainActivity.this)) {
//                switchFragment(2);
//                currentIndex = 2;
////                }
//                break;
//            case R.id.ll_mine:
//                if (CommonUtils.checkLogin(MainActivity.this)) {
//                    switchFragment(3);
//                    currentIndex = 3;
//                }
//                break;
//            case R.id.iv_guide:
//                //缩放动画
//                ScaleAnimation scaleAnim = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                scaleAnim.setDuration(1000);//动画持续时间
//                scaleAnim.setFillAfter(false);
//                ivGuide.startAnimation(scaleAnim);
//                //测试
//                //下滑pop
////                SlideFromTopCommissionToBalancePop slideFromTopCommissionToBalancePop = new SlideFromTopCommissionToBalancePop(MainActivity.this, new OnConfirmListener() {
////                    @Override
////                    public void confirm(String text) {
////                        ToastUtils.showLongToast(MainActivity.this,text);
////                    }
////                });
////                slideFromTopCommissionToBalancePop.showPopupWindow();
//                //map
////                Intent intent = new Intent(MainActivity.this, AmapActivity.class);
////                startActivity(intent);
//                break;
//        }
//    }
//
//    private void resetTextView() {
//        tvHome.setTextColor(getResources().getColor(R.color.COLOR_FF9A9A9A));
//        tvMall.setTextColor(getResources().getColor(R.color.COLOR_FF9A9A9A));
//        tvActivity.setTextColor(getResources().getColor(R.color.COLOR_FF9A9A9A));
//        tvMine.setTextColor(getResources().getColor(R.color.COLOR_FF9A9A9A));
//        tvHome.setSelected(false);
//        tvMall.setSelected(false);
//        tvActivity.setSelected(false);
//        tvMine.setSelected(false);
//    }

    public void scanCode() {
        requestPermissions(this, new String[]{Manifest.permission.CAMERA}, new RequestPermissionCallBack() {
            @Override
            public void granted() {
                Intent intent = new Intent(MainActivityNew.this, CaptureActivity.class);
                startActivityForResult(intent, Const.SCAN_CODE);
            }

            @Override
            public void denied() {
                ToastUtils.showShortToast(MainActivityNew.this, "获取权限失败，正常功能受到影响");
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                ToastUtils.showLongToast(MainActivityNew.this, getString(R.string.exit_app_tips));
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
            CommonDialog.Builder builder = new CommonDialog.Builder(MainActivityNew.this);
            builder.setMessage(MainActivityNew.this.getString(R.string.go_to_open_app_notice))
                    .setLeftButton(MainActivityNew.this.getString(R.string.cancel_pay))
                    .setRightButton(MainActivityNew.this.getString(R.string.confirm_pay))
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
}
