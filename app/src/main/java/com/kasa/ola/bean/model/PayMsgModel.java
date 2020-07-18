package com.kasa.ola.bean.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by guan on 2019/3/7 0007.
 */
public class PayMsgModel implements Serializable {

    public String totalPrice;//返回示例：
//    public String orderList;//返回示例：
    public ArrayList<String> orderList = new ArrayList<>();
}
