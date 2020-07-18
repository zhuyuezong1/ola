package com.kasa.ola.bean.entity;

import java.io.Serializable;

public class SelfMentionPointBean implements Serializable {
    private String takeID;
    private String name;
    private String mobile;
    private String province;
    private String city;
    private String area;
    private String addressDetail;

    public String getTakeID() {
        return takeID;
    }

    public void setTakeID(String takeID) {
        this.takeID = takeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
