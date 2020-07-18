package com.kasa.ola.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by guan on 2018/8/11 0011.
 */
public class APKVersionCodeUtils {
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
    public static int getInt(String str){
        if (str.contains(".")){
            String replace = str.replace(".", "");
            int i = Integer.parseInt(replace);
            return i;
        }
        return 0;
    }
    public static long getLong(String str){
        if (str.contains(".")){
            String replace = str.replace(".", "");
            long i = Long.parseLong(replace);
            return i;
        }
        return 0;
    }
}
