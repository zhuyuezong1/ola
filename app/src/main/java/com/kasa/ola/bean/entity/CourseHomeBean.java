package com.kasa.ola.bean.entity;

import java.util.List;

public class CourseHomeBean {

    private int totalSize;
    private int totalPage;
    private String lessonBanner;
    private String educationID;
    private List<CourseBean> lessonList;
    private List<HomeTopBean.BannerBean> bannerList;

    public List<HomeTopBean.BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<HomeTopBean.BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    public String getEducationID() {
        return educationID;
    }

    public void setEducationID(String educationID) {
        this.educationID = educationID;
    }

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

    public String getLessonBanner() {
        return lessonBanner;
    }

    public void setLessonBanner(String lessonBanner) {
        this.lessonBanner = lessonBanner;
    }

    public List<CourseBean> getLessonList() {
        return lessonList;
    }

    public void setLessonList(List<CourseBean> lessonList) {
        this.lessonList = lessonList;
    }

    public static class CourseBean{
        private String lessonName;
        private String lessonID;
        private String lessonLogo;
        private String lessonDesc;
        private String schoolName;
        private String schoolID;
        private String buyStatus;
//        private String lessonType;

//        public String getLessonType() {
//            return lessonType;
//        }
//
//        public void setLessonType(String lessonType) {
//            this.lessonType = lessonType;
//        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSchoolID() {
            return schoolID;
        }

        public void setSchoolID(String schoolID) {
            this.schoolID = schoolID;
        }

        public String getBuyStatus() {
            return buyStatus;
        }

        public void setBuyStatus(String buyStatus) {
            this.buyStatus = buyStatus;
        }

        public String getLessonName() {
            return lessonName;
        }

        public void setLessonName(String lessonName) {
            this.lessonName = lessonName;
        }

        public String getLessonID() {
            return lessonID;
        }

        public void setLessonID(String lessonID) {
            this.lessonID = lessonID;
        }

        public String getLessonLogo() {
            return lessonLogo;
        }

        public void setLessonLogo(String lessonLogo) {
            this.lessonLogo = lessonLogo;
        }

        public String getLessonDesc() {
            return lessonDesc;
        }

        public void setLessonDesc(String lessonDesc) {
            this.lessonDesc = lessonDesc;
        }
    }

}
