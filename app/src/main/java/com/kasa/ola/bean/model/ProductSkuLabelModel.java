package com.kasa.ola.bean.model;


import com.kasa.ola.json.JSONObject;

/**
 * Created by guan on 2018/9/4 0004.
 */
public class ProductSkuLabelModel {

    public String specName;
    public String speID;
    public String speImgUrl;
    public int selected = 0;
    public boolean isClickable = true;

    public ProductSkuLabelModel(JSONObject jo){
        specName = jo.optString("speName");
        speID = jo.optString("speID");
        speImgUrl = jo.optString("speImgUrl");
    }
}
