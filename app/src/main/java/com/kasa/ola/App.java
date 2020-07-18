package com.kasa.ola;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.TestImageLoader;
import com.previewlibrary.ZoomMediaLoader;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.managers.setting.GlobalSetting;
import com.tencent.bugly.webank.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import static com.tencent.bugly.webank.Bugly.applicationContext;

public class App extends MultiDexApplication {

    private static App app;
    private static HashMap<String, String> globaList;
    private boolean isOpenVibrate = true;
    private boolean isOpenSound = true;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        if (BuildConfig.APPLICATION_ID.equals(getAppName(android.os.Process.myPid()))) {
            init();
        }
        ZoomMediaLoader.getInstance().init(new TestImageLoader());
        initX5WebView();
//        GDTADManager.getInstance().initWith(applicationContext, "1110480252");
        // 打开Logcat输出，上线时，一定要关闭
        StatConfig.setDebugEnable(false);
        // 注册activity生命周期，统计时长
        StatService.registerActivityLifecycleCallbacks(app);
        config(this);
    }
    /**
     * 开启x5内核
     */
    private void initX5WebView() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //X5内核初始化完成，true使用x5内核，false使用系统内核
                LogUtil.e("APP","x5内核使用：" + b);
            }
        };
        //x5内核初始化
        QbSdk.initX5Environment(this, cb);
    }
    public static App getApp() {
        return app;
    }

    private void init() {
        globaList = new HashMap<>();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        //极光推送
        JPushInterface.init(this);
        JPushInterface.setLatestNotificationNumber(this, 1);
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.mipmap.icon_logo;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);

//        JPushInterface.init(this);     		// 初始化 JPush

//        //极光推送
//        JPushInterface.init(this);
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
//        builder.statusBarDrawable = R.mipmap.logo_launcher;
//        builder.notificationDefaults = Notification.DEFAULT_SOUND
//                /*| Notification.DEFAULT_VIBRATE
//                | Notification.DEFAULT_LIGHTS*/;  // 设置为铃声、震动、呼吸灯闪烁都要
//        JPushInterface.setPushNotificationBuilder(1, builder);
//        JPushInterface.setLatestNotificationNumber(this, 1);
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
//        builder.statusBarDrawable = R.mipmap.logo_launcher;
//        builder.notificationFlags = /*Notification.FLAG_AUTO_CANCEL
//                | */Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
//        builder.notificationDefaults = Notification.DEFAULT_SOUND;
////                | Notification.DEFAULT_VIBRATE
////                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
//        JPushInterface.setPushNotificationBuilder(1, builder);

//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
//        builder.statusBarDrawable = R.mipmap.logo_launcher;//设置推送的图标
//        if (isOpenVibrate && !isOpenSound) {//只有振动
//            builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
//        } else if (isOpenSound && !isOpenVibrate) {//只有声音
//            builder.notificationDefaults = Notification.DEFAULT_SOUND;
//        } else if (isOpenSound && isOpenVibrate) {//两个都有
//            builder.notificationDefaults = Notification.DEFAULT_ALL;
//        } else {//只有呼吸灯
//            builder.notificationDefaults = Notification.DEFAULT_LIGHTS;
//        }
//        JPushInterface.setDefaultPushNotificationBuilder(builder);

        //在这里初始化  Bugtags
//        Bugtags.start("f2f05d103a0a64168fee182b5ac1084c", this, Bugtags.BTGInvocationEventBubble);
    }

    private String getAppName(int pID) {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    return info.processName;
                }
            } catch (Exception e) {

            }
        }
        return null;
    }

    public void putString(String key, String value) {
        globaList.put(key, value);
    }

    public void removeString(String key) {
        globaList.remove(key);
    }

    public String getGlobalString(String key) {
        return globaList.get(key);
    }

    void config(Context context) {
        try {
//            CrashReport.initCrashReport(this, Constants.BuglyAppID, true);

            // 通过调用此方法初始化 SDK。如果需要在多个进程拉取广告，每个进程都需要初始化 SDK。
            GDTADManager.getInstance().initWith(context, Const.AD_APP_ID);

            GlobalSetting.setChannel(1);
            GlobalSetting.setEnableMediationTool(true);
            String packageName = context.getPackageName();
            //Get all activity classes in the AndroidManifest.xml
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
            if (packageInfo.activities != null) {
                for (ActivityInfo activity : packageInfo.activities) {
                    Bundle metaData = activity.metaData;
                    if (metaData != null && metaData.containsKey("id")
                            && metaData.containsKey("content") && metaData.containsKey("action")) {
                        Log.e("gdt", activity.name);
                        try {
                            Class.forName(activity.name);
                        } catch (ClassNotFoundException e) {
                            continue;
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
