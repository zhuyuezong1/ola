package com.kasa.ola.bean.entity;

import java.io.Serializable;

public class CheckOrderInfoBean implements Serializable {

    private String orderStatus;
    private String failTitle;
    private String failMessage;
    private String name;
    private String mobile;
    private String productName;
    private String price;
    private String productNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFailTitle() {
        return failTitle;
    }

    public void setFailTitle(String failTitle) {
        this.failTitle = failTitle;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }
}
