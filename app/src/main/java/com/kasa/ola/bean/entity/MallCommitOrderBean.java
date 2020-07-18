package com.kasa.ola.bean.entity;

import java.util.List;

public class MallCommitOrderBean {
    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public List<CommitOrderProductBean> getProductList() {
        return productList;
    }

    public void setProductList(List<CommitOrderProductBean> productList) {
        this.productList = productList;
    }

    private String shopID;
    private List<CommitOrderProductBean> productList;

}
