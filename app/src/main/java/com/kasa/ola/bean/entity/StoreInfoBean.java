package com.kasa.ola.bean.entity;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class StoreInfoBean implements Serializable {

    private String suppliersID;
    private String shopName;
    private String corporate;
    private String mobile;
    private String name;
    private String province;
    private String city;
    private String area;
    private String addressDetail;
    private String isTake;
    private String license;
    private String licenseUrl;
    private List<String> shopImages;
    private List<String> shopImageUrls;
    private String businessHours;
    private String longitude;
    private String latitude;

    public String getSuppliersID() {
        return suppliersID;
    }

    public void setSuppliersID(String suppliersID) {
        this.suppliersID = suppliersID;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCorporate() {
        return corporate;
    }

    public void setCorporate(String corporate) {
        this.corporate = corporate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIsTake() {
        return isTake;
    }

    public void setIsTake(String isTake) {
        this.isTake = isTake;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public List<String> getShopImages() {
        return shopImages;
    }

    public void setShopImages(List<String> shopImages) {
        this.shopImages = shopImages;
    }

    public List<String> getShopImageUrls() {
        return shopImageUrls;
    }

    public void setShopImageUrls(List<String> shopImageUrls) {
        this.shopImageUrls = shopImageUrls;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }
}
