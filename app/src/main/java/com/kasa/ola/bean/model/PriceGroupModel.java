package com.kasa.ola.bean.model;


import com.kasa.ola.json.JSONObject;


public class PriceGroupModel {

    public String groupContent;
//    public String groupID;
    public String totalPrice;
    public int inventory;

    public PriceGroupModel(JSONObject jo) {
        groupContent = jo.optString("groupContent");
//        groupID = jo.optString("groupID");
        totalPrice = jo.optString("totalPrice");
        inventory = jo.optInt("inventory");
    }
}
