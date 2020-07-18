package com.kasa.ola.bean.model;



import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;

import java.util.ArrayList;

public class ProductSkuModel {

    public String speTitle;
    public int pos;
    public ProductSkuLabelModel selectItem;
    public ArrayList<ProductSkuLabelModel> labelList = new ArrayList<>();

    public ProductSkuModel(JSONObject jo) {
        speTitle = jo.optString("speTitle");
        JSONArray ja = jo.optJSONArray("speItem");
        if (null != ja) {
            for (int i = 0; i < ja.length(); i++) {
                ProductSkuLabelModel productSkuLabelModel = new ProductSkuLabelModel(ja.optJSONObject(i));
                labelList.add(productSkuLabelModel);
            }
        }
    }
}
