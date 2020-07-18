package com.kasa.ola.bean.entity;

public class ShopInfoBean {
    private String suppliersID;
    private String suppliersLogo;
    private String suppliersName;
    private String suppliersDesc;
    private String address;
    private String distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSuppliersID() {
        return suppliersID;
    }

    public void setSuppliersID(String suppliersID) {
        this.suppliersID = suppliersID;
    }

    public String getSuppliersLogo() {
        return suppliersLogo;
    }

    public void setSuppliersLogo(String suppliersLogo) {
        this.suppliersLogo = suppliersLogo;
    }

    public String getSuppliersName() {
        return suppliersName;
    }

    public void setSuppliersName(String suppliersName) {
        this.suppliersName = suppliersName;
    }

    public String getSuppliersDesc() {
        return suppliersDesc;
    }

    public void setSuppliersDesc(String suppliersDesc) {
        this.suppliersDesc = suppliersDesc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
