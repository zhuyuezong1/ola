package com.kasa.ola.bean.entity;

import java.util.List;

public class EducationHomeBean {

    private int totalSize;
    private int totalPage;
    private String yearCardBanner;
    private String educationID;
    private List<YearCardBean> yearCardList;

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getYearCardBanner() {
        return yearCardBanner;
    }

    public void setYearCardBanner(String yearCardBanner) {
        this.yearCardBanner = yearCardBanner;
    }

    public String getEducationID() {
        return educationID;
    }

    public void setEducationID(String educationID) {
        this.educationID = educationID;
    }

    public List<YearCardBean> getYearCardList() {
        return yearCardList;
    }

    public void setYearCardList(List<YearCardBean> yearCardList) {
        this.yearCardList = yearCardList;
    }
}
