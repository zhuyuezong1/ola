package com.kasa.ola.bean.entity;

import java.io.Serializable;

public class YearCardBean implements Serializable {

    private String educationID;
    private String yearCardID;
    private String yearCardName;
    private String yearCardImage;
    private String yearCardDesc;
    private String price;
    private String buyNum;
    private String buyStatus;
    private String effectiveTime;
    private String lessonType;
    private String suppliers;
    private String suppliersID;

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
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

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getEducationID() {
        return educationID;
    }

    public void setEducationID(String educationID) {
        this.educationID = educationID;
    }

    public String getYearCardID() {
        return yearCardID;
    }

    public void setYearCardID(String yearCardID) {
        this.yearCardID = yearCardID;
    }

    public String getYearCardName() {
        return yearCardName;
    }

    public void setYearCardName(String yearCardName) {
        this.yearCardName = yearCardName;
    }

    public String getYearCardImage() {
        return yearCardImage;
    }

    public void setYearCardImage(String yearCardImage) {
        this.yearCardImage = yearCardImage;
    }

    public String getYearCardDesc() {
        return yearCardDesc;
    }

    public void setYearCardDesc(String yearCardDesc) {
        this.yearCardDesc = yearCardDesc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(String buyNum) {
        this.buyNum = buyNum;
    }

    public String getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(String buyStatus) {
        this.buyStatus = buyStatus;
    }
}
