package com.kasa.ola.bean.entity;

import java.io.Serializable;

public class CommitOrderProductBean implements Serializable {

    private String productID;
//    private String groupID;
    private String productNum;
    private String groupContent;
    private String shopProductID;

    public String getShopProductID() {
        return shopProductID;
    }

    public void setShopProductID(String shopProductID) {
        this.shopProductID = shopProductID;
    }

    public String getGroupContent() {
        return groupContent;
    }

    public void setGroupContent(String groupContent) {
        this.groupContent = groupContent;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

//    public String getGroupID() {
//        return groupID;
//    }
//
//    public void setGroupID(String groupID) {
//        this.groupID = groupID;
//    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

}
