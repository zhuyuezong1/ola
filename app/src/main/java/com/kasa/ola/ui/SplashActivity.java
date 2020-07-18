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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.PermissionDialog;
import com.kasa.ola.dialog.OrdinaryDialog;
import com.kasa.ola.dialog.PrivateDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    private Handler mHandler;
    private ArrayList<String> list = new ArrayList<>();
    private BaseActivity.RequestPermissionCallBack mRequestPermissionCallBack;
    private final int mRequestCode = 1024;
    protected final int REQUEST_CODE_SETTING = 1025;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        setTranslucentStatus();
        try {
            // 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
            StatService.startStatService(this, Const.APP_KEY,
                    com.tencent.stat.common.StatConstants.VERSION);
            Log.d("MTA","MTA初始化成功");
        } catch (MtaSDkException e) {
            // MTA初始化失败
            Log.d("MTA","MTA初始化失败"+e);
        }
        mHandler = new Handler();

        //目的：应用退到后台，再次进入应用重启App问题，特别是华为
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
        initView();
    }

    private void initView() {

        ivSplash.setVisibility(View.VISIBLE);
        final boolean haveNoPermission = checkPermissions(SplashActivity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE});

        if (LoginHandler.get().getShowPrivateRule()){
//            OrdinaryDialog.Builder builder = new OrdinaryDialog.Builder(SplashActivity.this);
//            String message = getString(R.string.private_content_first)+"<font color='#1E90FF'>"+getString(R.string.private_content_second)+"</font>"
//                    +getString(R.string.private_content_third)+"<font color='#1E90FF'>"+getString(R.string.private_content_fourth)+"</font>"+","+"<br/>"
//                    +getString(R.string.private_content_fifth)+"<br/>"+getString(R.string.private_content_fifth_1)+"<br/>"+getString(R.string.private_content_sixth)
//                    +"<br/>"+getString(R.string.private_content_seventh)+"<font color='#1E90FF'>"+getString(R.string.private_content_eighth)+"</font>"
//                    +getString(R.string.private_content_ninth)
//                    ;
//            builder.setMessage(message)
//                    .setTitle(getString(R.string.private_title))
//                    .setLeftButton(getString(R.string.cancel))
//                    .setRightButton(getString(R.string.agree))
//                    .setDialogInterface(new OrdinaryDialog.DialogInterface() {
//                        @Override
//                        public void disagree(OrdinaryDialog dialog) {
//                            dialog.dismiss();
//                            finish();
//                        }
//
//                        @Override
//                        public void agree(OrdinaryDialog dialog) {
//                            dialog.dismiss();
//                            LoginHandler.get().saveShowPrivateRule(false);
//                            showPermission(haveNoPermission);
//                        }
//                    })
//                    .create()
//                    .show();
            PrivateDialog.Builder builder = new PrivateDialog.Builder(SplashActivity.this);

            builder .setTitle(getString(R.string.private_title))
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
        }else {
            showPermission(haveNoPermission);
        }
    }

    private void showPermission(boolean haveNoPermission) {
        if (haveNoPermission){
            commonEnter();
            getBaseUrl();
//            ApiManager.get().getMyInfo(null);
        }else {
            PermissionDialog.Builder builder = new PermissionDialog.Builder(SplashActivity.this);
            String permissionContent = getString(R.string.permission_content_first)+"<font color='#1E90FF'>"+getString(R.string.permission_content_second)+"</font>"
                    +getString(R.string.permission_content_third)+"<font color='#1E90FF'>"+getString(R.string.permission_content_fourth)+"</font>"
                    +getString(R.string.permission_content_fifth)+"<br/>"+getString(R.string.permission_content_sixth)+"<br/>"
                    +getString(R.string.permission_content_seventh)+"<br/>"+getString(R.string.permission_content_eighth)+"<br/>"
                    +getString(R.string.permission_content_ninth)+"<br/>"+getString(R.string.permission_content_tenth)+"<br/>"
                    +getString(R.string.permission_content_eleventh)
                    ;
            builder.setMessage(permissionContent)
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

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            requestPermissions(SplashActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE}, new BaseActivity.RequestPermissionCallBack() {
                @Override
                public void granted() {
                    commonEnter();
                    getBaseUrl();
//                    ApiManager.get().getMyInfo(null);
                }

                @Override
                public void denied() {

                }

            });
        }
    }
    private void commonEnter(){
        ImageLoaderUtils.imageLoad(SplashActivity.this, R.drawable.splash, ivSplash);
        splash();
//        ApiManager.get().getData(Const.GUIDE_PAGE, null, new BusinessBackListener() {
//            @Override
//            public void onBusinessSuccess(BaseResponseModel responseModel) {
//                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
//                    JSONObject jo = (JSONObject) responseModel.data;
//                    JSONArray imageArr = jo.optJSONArray("imageArr");
//                    if (imageArr!=null && imageArr.length()>0){
//                        list.clear();
//                        for (int i=0;i<imageArr.length();i++){
//                            String imageUrl = imageArr.optString(i);
//                            list.add(imageUrl);
//                        }
//                    }
//                    splash();
//                }
//
//            }
//
//            @Override
//            public void onBusinessError(int code, String msg) {
//                ToastUtils.showLongToast(SplashActivity.this, msg);
//            }
//        }, null);
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

    private void splash() {

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
                ToastUtils.showShortToast(SplashActivity.this, msg);
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

}
