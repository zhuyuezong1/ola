package com.kasa.ola.bean.entity;

import java.io.Serializable;
import java.util.List;

public class ShopProductsBean {

    private int totalSize;
    private int totalPage;
    private List<ShopProductBean> productList;

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<ShopProductBean> getProductList() {
        return productList;
    }

    public void setProductList(List<ShopProductBean> productList) {
        this.productList = productList;
    }

}
