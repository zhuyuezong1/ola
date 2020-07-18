package com.kasa.ola.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.dialog.PermissionDialog;
import com.kasa.ola.dialog.PrivateDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SplashADActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.app_logo)
    ImageView appLogo;
    @BindView(R.id.splash_holder)
    ImageView splashHolder;
    @BindView(R.id.splash_container)
    FrameLayout splashContainer;
    @BindView(R.id.skip_view)
    TextView skipView;
    @BindView(R.id.preload_view)
    TextView preloadView;
    @BindView(R.id.splash_main)
    RelativeLayout splashMain;
    @BindView(R.id.splash_load_ad_close)
    Button splashLoadAdClose;
    @BindView(R.id.splash_load_ad_refresh)
    Button splashLoadAdRefresh;
    @BindView(R.id.splash_load_ad_display)
    Button splashLoadAdDisplay;
    @BindView(R.id.splash_load_ad_status)
    TextView splashLoadAdStatus;
    @BindView(R.id.splash_load_ad_only)
    LinearLayout splashLoadAdOnly;
    private Handler mHandler;
    private ArrayList<String> list = new ArrayList<>();
    private BaseActivity.RequestPermissionCallBack mRequestPermissionCallBack;
    private final int mRequestCode = 1024;
    protected final int REQUEST_CODE_SETTING = 1025;
    /**
     * 记录拉取广告的时间
     */
    private long fetchSplashADTime = 0;
    private SplashAD splashAD;
    private boolean loadAdOnly = false;
    private boolean showingAd = false;
    public boolean canJump = false;
    private boolean needStartDemoList = true;
    private static final String SKIP_TEXT = "点击跳过 %d";
    /**
     * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
     * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
     * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值（单位ms）
     **/
    private int minSplashTimeWhenNoAD = 2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);
        // androidManifest.xml指定本activity最先启动
        // 因此，MTA的初始化工作需要在本onCreate中进行
        // 在startStatService之前调用StatConfig配置类接口，使得MTA配置及时生
//        String appkey = "Aqc1110480252";
        // 初始化并启动MTA
        try {
            // 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
            StatService.startStatService(this, Const.APP_KEY,
                    com.tencent.stat.common.StatConstants.VERSION);
            Log.d("MTA","MTA初始化成功");
        } catch (MtaSDkException e) {
            // MTA初始化失败
            Log.d("MTA","MTA初始化失败"+e);
        }
        ButterKnife.bind(this);
        setTranslucentStatus();
        mHandler = new Handler();

        skipView.setVisibility(View.VISIBLE);
        appLogo.setVisibility(View.VISIBLE);

        splashLoadAdClose.setOnClickListener(this);
        splashLoadAdDisplay.setOnClickListener(this);
        splashLoadAdRefresh.setOnClickListener(this);

        if(loadAdOnly){
            splashLoadAdOnly.setVisibility(View.VISIBLE);
            splashLoadAdStatus.setText(R.string.splash_loading);
            splashLoadAdDisplay.setEnabled(false);
        }

        //目的：应用退到后台，再次进入应用重启App问题，特别是华为
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            checkAndRequestPermission();
//        } else {
//            // 如果是Android6.0以下的机器，建议在manifest中配置相关权限，这里可以直接调用SDK
//            fetchSplashAD(this, container, skipView, getPosId(), this, 0);
//        }
        initView();
    }

    private void initView() {

        final boolean haveNoPermission = checkPermissions(SplashADActivity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE});

        if (LoginHandler.get().getShowPrivateRule()) {
            PrivateDialog.Builder builder = new PrivateDialog.Builder(SplashADActivity.this);

            builder.setTitle(getString(R.string.private_title))
                    .setLeftButton(getString(R.string.cancel))
                    .setRightButton(getString(R.string.agree))
                    .setDialogInterface(new PrivateDialog.DialogInterface() {
                        @Override
                        public void disagree(PrivateDialog dialog) {
                            dialog.dismiss();
                            finish();
                        }

                        @Override
                        public void agree(PrivateDialog dialog) {
                            dialog.dismiss();
                            LoginHandler.get().saveShowPrivateRule(false);
                            showPermission(haveNoPermission);
                        }
                    })
                    .create()
                    .show();
        } else {
            showPermission(haveNoPermission);
        }
    }

    private void showPermission(boolean haveNoPermission) {
        if (haveNoPermission) {
//            commonEnter();
            getAd();
            getBaseUrl();
            ApiManager.get().getMyInfo(null);
        } else {
            PermissionDialog.Builder builder = new PermissionDialog.Builder(SplashADActivity.this);
            String clickText = getString(R.string.permission_content_first) + "<font color='#1E90FF'>" + getString(R.string.permission_content_second) + "</font>"
                    + getString(R.string.permission_content_third) + "<font color='#1E90FF'>" + getString(R.string.permission_content_fourth) + "</font>"
                    + getString(R.string.permission_content_fifth) ;
            String contentText = getString(R.string.permission_content_sixth) + "<br/>"
                    + getString(R.string.permission_content_seventh) + "<br/>" + getString(R.string.permission_content_eighth) + "<br/>"
                    + getString(R.string.permission_content_ninth) + "<br/>" + getString(R.string.permission_content_tenth) + "<br/>"
                    + getString(R.string.permission_content_eleventh);
            builder.setTitle(getString(R.string.remind))
                    .setClickText(clickText)
                    .setMessage(contentText)
                    .setDialogInterface(new PermissionDialog.DialogInterface() {
                        @Override
                        public void close(PermissionDialog dialog) {

                        }

                        @Override
                        public void open(PermissionDialog dialog) {
                            dialog.dismiss();
                            getPermission();
                        }
                    })
                    .create()
                    .show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            requestPermissions(SplashADActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE}, new BaseActivity.RequestPermissionCallBack() {
                @Override
                public void granted() {
                    getAd();
//                    commonEnter();
                    getBaseUrl();
                    ApiManager.get().getMyInfo(null);
                }

                @Override
                public void denied() {

                }

            });
        }
    }

    private void getAd() {
        fetchSplashAD(SplashADActivity.this, splashContainer, skipView, getPosId(), new SplashADListener() {
            @Override
            public void onADDismissed() {
                next();
            }

            @Override
            public void onNoAD(AdError error) {
                String str = String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", error.getErrorCode(),
                        error.getErrorMsg());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLongToast(SplashADActivity.this,str);
                    }
                });
                if(loadAdOnly && !showingAd) {
                    splashLoadAdStatus.setText(str);
                    return;//只拉取广告时，不终止activity
                }    /**
                 * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
                 * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
                 * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值
                 **/
                long alreadyDelayMills = System.currentTimeMillis() - fetchSplashADTime;//从拉广告开始到onNoAD已经消耗了多少时间
                long shouldDelayMills = alreadyDelayMills > minSplashTimeWhenNoAD ? 0 : minSplashTimeWhenNoAD
                        - alreadyDelayMills;//为防止加载广告失败后立刻跳离开屏可能造成的视觉上类似于"闪退"的情况，根据设置的minSplashTimeWhenNoAD
                // 计算出还需要延时多久
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (needStartDemoList) {
                            SplashADActivity.this.startActivity(new Intent(SplashADActivity.this, MainActivity.class));
                        }
                        SplashADActivity.this.finish();
                    }
                }, shouldDelayMills);
            }

            @Override
            public void onADPresent() {

            }

            @Override
            public void onADClicked() {

            }

            @Override
            public void onADTick(long millisUntilFinished) {
                if (skipView != null) {
                    skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
                }
            }

            @Override
            public void onADExposure() {

            }

            @Override
            public void onADLoaded(long expireTimestamp) {
                if(loadAdOnly) {
                    splashLoadAdDisplay.setEnabled(true);
                    long timeIntervalSec = (expireTimestamp- SystemClock.elapsedRealtime())/1000;
                    long min = timeIntervalSec/60;
                    long second = timeIntervalSec-(min*60);
                    splashLoadAdStatus.setText("加载成功,广告将在:"+min+"分"+second+"秒后过期，请在此之前展示(showAd)");
                }
            }
        }, 3000);
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            if (needStartDemoList) {
                this.startActivity(new Intent(this, MainActivity.class));
            }
            this.finish();
        } else {
            canJump = true;
        }
    }
    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param posId         广告位ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String posId, SplashADListener adListener, int fetchDelay) {
        fetchSplashADTime = System.currentTimeMillis();
        splashAD = new SplashAD(activity, skipContainer, posId, adListener, fetchDelay);
        if (loadAdOnly) {
            splashAD.fetchAdOnly();
        } else {
            splashAD.fetchAndShowIn(adContainer);
        }
    }
    private String getPosId() {
        String posId = getIntent().getStringExtra("pos_id");
        return TextUtils.isEmpty(posId) ? Const.SPLASH_POS_ID : posId;
    }
    private void commonEnter() {
//        splash();
    }

    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
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
                ToastUtils.showShortToast(SplashADActivity.this, msg);
            }
        }, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 发起权限请求
     *
     * @param context
     * @param permissions
     * @param callback
     */
    public void requestPermissions(final Context context, final String[] permissions,
                                   BaseActivity.RequestPermissionCallBack callback) {
        this.mRequestPermissionCallBack = callback;
        StringBuilder permissionNames = new StringBuilder();
        for (String s : permissions) {
            permissionNames = permissionNames.append(s + "\r\n");
        }
        //如果所有权限都已授权，则直接返回授权成功,只要有一项未授权，则发起权限请求
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isAllGranted = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    new AlertDialog.Builder(context)
                            .setMessage("您好，需要如下权限：\r\n" +
                                    permissionNames +
                                    " 请允许，否则将影响部分功能的正常使用。")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                }

                break;
            }
        }
        if (isAllGranted) {
            if (null != mRequestPermissionCallBack) {
                mRequestPermissionCallBack.granted();
            }
        }
    }

    public boolean checkPermissions(final Context context, final String[] permissions) {
        StringBuilder permissionNames = new StringBuilder();
        for (String s : permissions) {
            permissionNames = permissionNames.append(s + "\r\n");
        }
        //如果所有权限都已授权，则直接返回授权成功,只要有一项未授权，则发起权限请求
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        StringBuilder permissionName = new StringBuilder();
        for (String s : permissions) {
            permissionName = permissionName.append(s + "\r\n");
        }
        switch (requestCode) {
            case mRequestCode: {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        hasAllGranted = false;
                        //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                        // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            new AlertDialog.Builder(this)
                                    .setMessage("获取相关权限失败:\r\n" +
                                            permissionName +
                                            "将导致部分功能无法正常使用，需要到设置页面手动授权")
                                    .setCancelable(false)
                                    .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, REQUEST_CODE_SETTING);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (null != mRequestPermissionCallBack) {
                                                mRequestPermissionCallBack.denied();
                                            }
                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    if (null != mRequestPermissionCallBack) {
                                        mRequestPermissionCallBack.denied();
                                    }
                                }
                            }).show();

                        } else {
                            //用户拒绝权限请求，但未选中“不再提示”选项
                            if (null != mRequestPermissionCallBack) {
                                mRequestPermissionCallBack.denied();
                            }
                        }
                        break;
                    }
                }
                if (hasAllGranted) {
                    if (null != mRequestPermissionCallBack) {
                        mRequestPermissionCallBack.granted();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.splash_load_ad_close:
                finish();
                break;
            case R.id.splash_load_ad_display:
                splashLoadAdOnly.setVisibility(View.GONE);
                showingAd = true;
                splashAD.showAd(splashContainer);
                break;
            case R.id.splash_load_ad_refresh:
                showingAd = false;
                splashAD.fetchAdOnly();
                splashLoadAdStatus.setText(R.string.splash_loading);
                splashLoadAdDisplay.setEnabled(false);
                break;
        }
    }

}
