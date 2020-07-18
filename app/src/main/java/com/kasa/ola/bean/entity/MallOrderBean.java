package com.kasa.ola.bean.entity;

import java.io.Serializable;
import java.util.List;

public class MallOrderBean {

    private String suppliers;
    private String suppliersID;
    private String orderNo;
    private int orderStatus;
    private int totalNum;
    private String totalPrice;
    private TakeAddressBean takeAddress;
    private String codeImageUrl;

    private List<ProductOrder> productList;

    public String getCodeImageUrl() {
        return codeImageUrl;
    }

    public void setCodeImageUrl(String codeImageUrl) {
        this.codeImageUrl = codeImageUrl;
    }

    public TakeAddressBean getTakeAddress() {
        return takeAddress;
    }

    public void setTakeAddress(TakeAddressBean takeAddress) {
        this.takeAddress = takeAddress;
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers;
    }

    public String getSuppliersID() {
        return suppliersID;
    }

    public void setSuppliersID(String suppliersID) {
        this.suppliersID = suppliersID;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<ProductOrder> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductOrder> productList) {
        this.productList = productList;
    }

    public static class ProductOrder implements Serializable {

        private String productID;
        private String imageUrl;
        private String productName;
        private String productNum;
        private String spe;
        private String price;
        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductNum() {
            return productNum;
        }

        public void setProductNum(String productNum) {
            this.productNum = productNum;
        }

        public String getSpe() {
            return spe;
        }

        public void setSpe(String spe) {
            this.spe = spe;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

    }
}
