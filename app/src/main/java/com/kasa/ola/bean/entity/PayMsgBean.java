package com.kasa.ola.bean.entity;

import java.util.ArrayList;

public class PayMsgBean {
    private String totalPrice;//返回示例：
    private ArrayList<String> orderList;

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<String> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<String> orderList) {
        this.orderList = orderList;
    }
}
