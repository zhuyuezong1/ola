package com.kasa.ola.bean;

import android.text.TextUtils;

import com.kasa.ola.json.JSONObject;


/**
 * Created by guan on 2018/6/6 0006.
 */
public class BaseResponseModel {

    public int resultCode;
    public String resultCodeDetail;
    public Object data;

    public BaseResponseModel(String response) {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jo = new JSONObject(response);
            resultCode = jo.optInt("resultCode");
            resultCodeDetail = jo.optString("resultCodeDetail");
            data = jo.opt("data");
        }
    }
}
