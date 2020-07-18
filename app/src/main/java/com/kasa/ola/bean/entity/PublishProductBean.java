package com.kasa.ola.bean.entity;

import java.util.List;

public class PublishProductBean {

    private String productID;
    private String comment;
    private String spe;
    private List<String> imageArr;

    public String getSpe() {
        return spe;
    }

    public void setSpe(String spe) {
        this.spe = spe;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImageArr() {
        return imageArr;
    }

    public void setImageArr(List<String> imageArr) {
        this.imageArr = imageArr;
    }
}
