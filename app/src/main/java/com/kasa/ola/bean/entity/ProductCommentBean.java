package com.kasa.ola.bean.entity;

import java.util.List;

public class ProductCommentBean {
    private String mobile;
    private String time;
    private String spe;
    private String comment;
    private List<String> imgArr;

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

    public String getSpe() {
        return spe;
    }

    public void setSpe(String spe) {
        this.spe = spe;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImgArr() {
        return imgArr;
    }

    public void setImgArr(List<String> imgArr) {
        this.imgArr = imgArr;
    }
}
