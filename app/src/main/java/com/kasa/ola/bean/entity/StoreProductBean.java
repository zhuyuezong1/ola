package com.kasa.ola.bean.entity;

import java.util.List;

public class StoreProductBean {

    private String productID;
    private String productName;
    private String describe;
    private String specialPrice;
    private String price;
    private String inventory;
    private String isAuto;
    private List<String> imgArr;
    private List<String> detailImgArr;
    private List<String> imgUrlArr;
    private List<String> detailImgUrlArr;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(String specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public String getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(String isAuto) {
        this.isAuto = isAuto;
    }

    public List<String> getImgArr() {
        return imgArr;
    }

    public void setImgArr(List<String> imgArr) {
        this.imgArr = imgArr;
    }

    public List<String> getDetailImgArr() {
        return detailImgArr;
    }

    public void setDetailImgArr(List<String> detailImgArr) {
        this.detailImgArr = detailImgArr;
    }

    public List<String> getImgUrlArr() {
        return imgUrlArr;
    }

    public void setImgUrlArr(List<String> imgUrlArr) {
        this.imgUrlArr = imgUrlArr;
    }

    public List<String> getDetailImgUrlArr() {
        return detailImgUrlArr;
    }

    public void setDetailImgUrlArr(List<String> detailImgUrlArr) {
        this.detailImgUrlArr = detailImgUrlArr;
    }
}
