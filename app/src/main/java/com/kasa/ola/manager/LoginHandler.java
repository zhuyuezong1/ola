package com.kasa.ola.manager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.kasa.ola.App;
import com.kasa.ola.bean.entity.BaseUrlBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.utils.JsonUtils;


/**
 * Created by guan on 2018/7/2 0002.
 */
public class LoginHandler {

    private static final String SHARED_PREF_NAME = "shared.preferences";
    private static final String PREF_KEY_MY_INFO = "PREF_KEY_MY_INFO";
    private static final String PREF_KEY_TOKEN = "PREF_KEY_TOKEN";
    private static final String NOTICE_SHOWN_VERSION = "NOTICE_SHOWN_VERSION";
    private static final String PRIVATE_RULE = "PRIVATE_RULE";
    private static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    private static final String USER_ID = "user_id";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private Application app = App.getApp();
    private JSONObject myInfo;
    private BaseUrlBean baseUrlBean = new BaseUrlBean();

    private static LoginHandler instance = null;

    public static LoginHandler get() {
        if (null == instance) {
            synchronized (LoginHandler.class) {
                if (null == instance) {
                    instance = new LoginHandler();
                }
            }
        }
        return instance;
    }

    private SharedPreferences getSharedPreferences() {
        return app.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    private String getUserPreferences() {
        return getSharedPreferences().getString(PREF_KEY_MY_INFO, null);
    }

    public void saveAccountNumber(String account) {
        JSONArray ja;
        if (!TextUtils.isEmpty(getAccountNumber())) {
            ja = new JSONArray(getAccountNumber());
        } else {
            ja = new JSONArray();
        }
        JSONObject jo = new JSONObject();
        jo.put("userID", getUserId());
        jo.put("account", account);
        ja.put(jo);
        getSharedPreferences().edit().putString(ACCOUNT_NUMBER, ja.toString()).commit();
    }

    public String getAccountNumber() {
        return getSharedPreferences().getString(ACCOUNT_NUMBER, null);
    }

    public String getUserId() {
        String uid = getSharedPreferences().getString(USER_ID, null);
        return getSharedPreferences().getString(USER_ID, null);
    }
    public String getToken() {
        return getSharedPreferences().getString(PREF_KEY_TOKEN, null);
    }
    public boolean checkLogined() {
        return getUserId() != null;
    }

    public JSONObject getMyInfo() {
        initMyInfo();
        return myInfo;
    }

    private void initMyInfo() {
        String cache = getUserPreferences();
        if (!TextUtils.isEmpty(cache)) {
            myInfo = new JSONObject(cache);
        } else {
            myInfo = new JSONObject();
        }
    }

    private void saveMyInfo() {
        getSharedPreferences().edit().putString(PREF_KEY_MY_INFO, myInfo.toString()).commit();
    }
    public void saveToken(String tokenID) {
        getSharedPreferences().edit().putString(PREF_KEY_TOKEN, tokenID).commit();
    }
    public void saveMyInfo(JSONObject infoJo) {
        save(PREF_KEY_MY_INFO, infoJo.toString());
    }

    public void saveShownNoticeVersion() {
        if (baseUrlBean!=null){
            getSharedPreferences().edit().putInt(NOTICE_SHOWN_VERSION, baseUrlBean.getNoticeVersion()).commit();
        }
    }

    public int getShownNoticeVersion() {
        return getSharedPreferences().getInt(NOTICE_SHOWN_VERSION, 0);
    }
    public void saveShowPrivateRule(boolean isShow) {
        getSharedPreferences().edit().putBoolean(PRIVATE_RULE, isShow).commit();
    }

    public boolean getShowPrivateRule() {
        return getSharedPreferences().getBoolean(PRIVATE_RULE,true);
    }
    public void updateMyInfoCache(String key, String value) {
        if (checkLogined()) {
            getMyInfo().put(key, value);
            saveMyInfo();
        }
    }

    public void successLogin(String uId, JSONObject infoJo) {
        save(USER_ID, uId);
        save(PREF_KEY_MY_INFO, infoJo.toString());
    }

    public void logout() {
        removeEntry(USER_ID);
        removeEntry(PREF_KEY_MY_INFO);
        removeEntry(PREF_KEY_TOKEN);
    }

    private void save(String key, String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    private void removeEntry(String key) {
        getSharedPreferences().edit().remove(key).commit();
    }

    public void saveBaseUrl(JSONObject urlJo) {
        String jsonData =  urlJo.toString();
        baseUrlBean = JsonUtils.jsonToObject(jsonData,BaseUrlBean.class);
    }

    public BaseUrlBean getBaseUrlBean() {
        if (baseUrlBean!=null){
            return baseUrlBean;
        }else{
            return new BaseUrlBean();
        }
    }
    public double getLongitude() {
        return Double.parseDouble(getSharedPreferences().getString(LONGITUDE,"0"));
    }
    public void saveLongitude(String longitude) {
        getSharedPreferences().edit().putString(LONGITUDE, longitude).commit();
    }

    public double getLatitude() {
        return Double.parseDouble(getSharedPreferences().getString(LATITUDE,"0"));
    }
    public void saveLatitude(String latitude) {
        getSharedPreferences().edit().putString(LATITUDE, latitude).commit();
    }
}
