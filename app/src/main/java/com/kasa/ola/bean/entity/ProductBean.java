package com.kasa.ola.bean.entity;

import java.io.Serializable;

public class ProductBean implements Serializable {

    private String productID;
    private String describe;
    private String price;
    private String imageUrl;
    private String productName;
    private String specialPrice;
    private String rebates;
    private String sales;
    private String unit;



    private String productUrl;
    private String status;
    private String suppliers;
    private String suppliersID;
    private String spe;
    private String productNum;
    //    private String groupID;
    private String groupContent;


    public String getSpe() {
        return spe;
    }

    public void setSpe(String spe) {
        this.spe = spe;
    }

    public String getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(String specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getRebates() {
        return rebates;
    }

    public void setRebates(String rebates) {
        this.rebates = rebates;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

//    public String getGroupID() {
//        return groupID;
//    }
//
//    public void setGroupID(String groupID) {
//        this.groupID = groupID;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
