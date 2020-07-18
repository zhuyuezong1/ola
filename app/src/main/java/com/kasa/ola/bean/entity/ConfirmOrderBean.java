package com.kasa.ola.bean.entity;

import java.util.List;

public class ConfirmOrderBean {

    private String suppliersID;
    private String couponsID;
    private String redPackID;
    private String remark;
    private List<ConfirmProductBean> productList;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSuppliersID() {
        return suppliersID;
    }

    public void setSuppliersID(String suppliersID) {
        this.suppliersID = suppliersID;
    }

    public String getCouponsID() {
        return couponsID;
    }

    public void setCouponsID(String couponsID) {
        this.couponsID = couponsID;
    }

    public String getRedPackID() {
        return redPackID;
    }

    public void setRedPackID(String redPackID) {
        this.redPackID = redPackID;
    }

    public List<ConfirmProductBean> getProductList() {
        return productList;
    }

    public void setProductList(List<ConfirmProductBean> productList) {
        this.productList = productList;
    }

    public static class ConfirmProductBean {
        private String productID;
        private String groupContent;
        private String productNum;
        private String shopProductID;

        public String getShopProductID() {
            return shopProductID;
        }

        public void setShopProductID(String shopProductID) {
            this.shopProductID = shopProductID;
        }

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public String getGroupContent() {
            return groupContent;
        }

        public void setGroupContent(String groupContent) {
            this.groupContent = groupContent;
        }

        public String getProductNum() {
            return productNum;
        }

        public void setProductNum(String productNum) {
            this.productNum = productNum;
        }
    }
}
