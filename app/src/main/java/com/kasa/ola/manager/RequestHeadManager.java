package com.kasa.ola.manager;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.kasa.ola.App;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.utils.DisplayUtils;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by guan on 2018/7/3 0003.
 */
public class RequestHeadManager {

    private JSONObject headJo;
    private Application app = App.getApp();
    private boolean initialized = false;

    private static RequestHeadManager instance = null;

    public static RequestHeadManager get() {
        if (null == instance) {
            synchronized (RequestHeadManager.class) {
                if (null == instance) {
                    instance = new RequestHeadManager();
                }
            }
        }
        return instance;
    }

    private String getResolution() {
        return DisplayUtils.getScreenWidth(app) + "*" + DisplayUtils.getScreenHeight(app);
    }

    /**
     * 返回当前程序版本名
     */
    private String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {

        }
        return versionName;
    }

    private String getAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public JSONObject getHead() {
        if (null == headJo) {
            headJo = new JSONObject();
        }
//        if (!initialized) {
            headJo.put("resolution", getResolution());
            headJo.put("devSystemVersion", "Android " + getAndroidVersion());
            headJo.put("ClientVersion", getAppVersionName(app));
            headJo.put("is_mobile_terminal", "mobile");
            headJo.put("tokenID", TextUtils.isEmpty(LoginHandler.get().getToken())?"":LoginHandler.get().getToken());
//            initialized = true;
//        }
//        headJo.put("userID", LoginHandler.get().getUserId());
//        headJo.put("registrationId", JPushInterface.getRegistrationID(app));
        return headJo;
    }

}
