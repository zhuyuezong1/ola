package com.kasa.ola.bean.entity;

import java.util.List;

public class MallTabClassBean {

    private String adsImg;
    private List<MallClassBean> classList;

    public String getAdsImg() {
        return adsImg;
    }

    public void setAdsImg(String adsImg) {
        this.adsImg = adsImg;
    }

    public List<MallClassBean> getClassList() {
        return classList;
    }

    public void setClassList(List<MallClassBean> classList) {
        this.classList = classList;
    }
}
