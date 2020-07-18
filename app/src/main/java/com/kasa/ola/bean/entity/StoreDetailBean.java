package com.kasa.ola.bean.entity;

import java.util.List;

public class StoreDetailBean {

//    private String banner;
    private List<Banner> imgArr;
    private String suppliersID;
    private String suppliersLogo;
    private String suppliersName;
    private String address;
    private String call;
    private String detailUrl;
    private String businessHours;
    private String longitude;
    private String latitude;

    public List<Banner> getImgArr() {
        return imgArr;
    }

    public void setImgArr(List<Banner> imgArr) {
        this.imgArr = imgArr;
    }

    public class Banner{
        private String imageUrl;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }


    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}
