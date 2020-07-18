package com.kasa.ola.base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;

import com.bugtags.library.Bugtags;
import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.dialog.CommonProgressDialog;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.utils.APKVersionCodeUtils;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.ClipBoardUtil;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class BaseActivity extends AppCompatActivity {

    protected boolean translucentStatus = true;   //沉浸式状态栏
    protected boolean isSingle = true;      //是否是单例

    private RequestPermissionCallBack mRequestPermissionCallBack;
    private final int mRequestCode = 1024;
    protected final int REQUEST_CODE_SETTING = 1025;

    private Context mContext = BaseActivity.this;


    public interface EventBusListener {

    }


    //版本更新相关
    protected CommonProgressDialog pBar;
    protected boolean goUpdate = false;
    protected AlertDialog dialog;
    protected int defaultColor = R.color.colorPrimaryDark;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pBar = new CommonProgressDialog(this);

        if (translucentStatus) {
            setTranslucentStatus();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStatusBar(defaultColor);
            }
        }

        ActivityUtils.addActivity(this);
        if (isSingle) {
            ActivityUtils.finishOtherSameActivity(this);
        }
        if (this instanceof EventBusListener) {
            EventBus.getDefault().register(this);
        }
//        CommonUtils.checkBlack(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtils.finishOtherActivity(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(true);
    }

    protected void setActionBar(String title, String right,int color) {
        View mActionBar = findViewById(R.id.view_actionbar);
        if (null != mActionBar) {
            if (translucentStatus) {
                mActionBar.setPadding(0, DisplayUtils.getStatusBarHeight2(this), 0, 0);
            }
            TextView titleText = findViewById(R.id.tv_title);
            TextView rightText = findViewById(R.id.tv_right_text);
            ImageView backBtn = findViewById(R.id.iv_back);
            mActionBar.setBackgroundResource(color);
            titleText.setText(title);
            rightText.setText(right);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    protected void setActionBar(String title, String right) {
        View mActionBar = findViewById(R.id.view_actionbar);
        if (null != mActionBar) {
            if (translucentStatus) {
                mActionBar.setPadding(0, DisplayUtils.getStatusBarHeight2(this), 0, 0);
            }
            TextView titleText = findViewById(R.id.tv_title);
            TextView rightText = findViewById(R.id.tv_right_text);
            ImageView backBtn = findViewById(R.id.iv_back);
            titleText.setText(title);
            rightText.setText(right);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
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

    /**
     * 发起权限请求
     *
     * @param context
     * @param permissions
     * @param callback
     */
    public void requestPermissions(final Context context, final String[] permissions,
                                   RequestPermissionCallBack callback) {
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

    /**
     * 权限请求结果回调接口
     */
    public interface RequestPermissionCallBack {
        /**
         * 同意授权
         */
        void granted();

        /**
         * 取消授权
         */
        void denied();

    }

    protected boolean checkUpdate(boolean forceUpdate) {
        if (null == LoginHandler.get().getBaseUrlBean()) {
            return false;
        }
        long anInt = APKVersionCodeUtils.getLong(LoginHandler.get().getBaseUrlBean().getAndroidVersionCode());
        long anInt1 = APKVersionCodeUtils.getLong(APKVersionCodeUtils.getVersionName(this));

        boolean isOnline = anInt > anInt1;
        if (isOnline) {
            goUpdate = false;
            showDialog(forceUpdate);
            return true;
        } else {
            return false;
        }
    }

    private void showDialog(final boolean finish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("版本更新")
                .setMessage("发现新版本")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        goUpdate = true;
                        dialog.dismiss();
                        requestPermissions(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
                            @Override
                            public void granted() {
                                downloadApk();
                            }

                            @Override
                            public void denied() {
                                ToastUtils.showShortToast(BaseActivity.this, "获取权限失败，正常功能受到影响");
                                finish();
                            }

                        });

                    }
                })
                .setNegativeButton("官方下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
//                            Uri content_url = Uri.parse(LoginHandler.get().getBaseUrlBean().getAndroidDownloadUrl());
//                            Intent intent = new Intent(Intent.ACTION_VIEW,content_url);
//                            intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");

//                            Intent intent = new Intent();
//                            intent.setAction("android.intent.action.VIEW");
//                            intent.setData(content_url);
//

//                            Intent intent = new Intent();
//                            intent.setAction("android.intent.action.VIEW");
//                            intent.setData(content_url);


//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            Uri content_uri_browsers = Uri.parse(LoginHandler.get().getBaseUrlBean().getAndroidDownloadUrl());
//                            intent.setData(content_uri_browsers);

//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            Uri content_url = Uri.parse(LoginHandler.get().getBaseUrlBean().getAndroidDownloadUrl());//splitflowurl为分流地址
//                            intent.setData(content_url);
//                            if (!hasPreferredApplication(mContext,intent)){
//                                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
//                            }

                            Intent intent= new Intent(); intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(LoginHandler.get().getBaseUrlBean().getAndroidFirDownloadUrl());
                            intent.setDataAndType(content_url, "text/html");
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);

                            startActivity(intent);
                            dialog.dismiss();
                        } catch (ActivityNotFoundException a) {
                            ToastUtils.showShortToast(BaseActivity.this, getString(R.string.explore_confirm));
                        }
                    }
                });
        if (null == dialog) {
            dialog = builder.create();
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!goUpdate) {
                    if (finish) {
                        finish();
                    }
                }
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(!finish);
    }
//    //判断系统是否设置了默认浏览器
//    public  boolean hasPreferredApplication(Context context, Intent intent) {
//        PackageManager pm = context.getPackageManager();
//        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        return !"android".equals(info.activityInfo.packageName);
//    }
    public void downloadApk() {
        pBar.setCanceledOnTouchOutside(false);
        pBar.setTitle("正在下载");
        pBar.setMessage("正在下载");
        pBar.setIndeterminate(true);
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setCancelable(false);
        final BaseActivity.DownloadTask downloadTask = new DownloadTask(
                BaseActivity.this);
        downloadTask.execute(LoginHandler.get().getBaseUrlBean().getAndroidDownloadUrl());
        pBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    /**
     * 下载应用
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    file = new File(getExternalFilesDir("update").getAbsolutePath(),
                            Const.DOWNLOAD_NAME);

                    if (!file.exists()) {
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }

                } else {
                    ToastUtils.showShortToast(BaseActivity.this, "sd卡未挂载");
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            pBar.setIndeterminate(false);
            pBar.setMax(100);
            pBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pBar.setFinished();
            if (result == null) {
                update();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                downloadApk();
                break;
            }
        }
    }



    private void update() {
        //安装应用
        File apkFile = new File(getExternalFilesDir("update").getAbsolutePath(), Const.DOWNLOAD_NAME);
        if (!apkFile.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(App.getApp(), App.getApp().getPackageName() + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
        }
//        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        startActivity(intent);
        pBar.dismiss();
    }
    public void setStatusBar(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getColor(color));
            if (isLightColor(getColor(color))) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }
    private boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }
}
