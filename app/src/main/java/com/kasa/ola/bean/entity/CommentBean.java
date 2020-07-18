package com.kasa.ola.bean.entity;

import java.util.List;

public class CommentBean {

    private String mobile;
    private String time;
    private String comment;
    private String spe;
    private String productImage;
    private String productName;
    private String productPrice;
    private List<String> imgArr;

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getSpe() {
        return spe;
    }

    public void setSpe(String spe) {
        this.spe = spe;
    }

    public List<String> getImgArr() {
        return imgArr;
    }

    public void setImgArr(List<String> imgArr) {
        this.imgArr = imgArr;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
