package com.kasa.ola.bean.entity;

public class CourseBean {
    private String lessonName;
    private String lessonID;
    private String educationLogo;
    private String describe;
    private String buyStatus;
    private String effectiveTime;

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
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

    public String getEducationLogo() {
        return educationLogo;
    }

    public void setEducationLogo(String educationLogo) {
        this.educationLogo = educationLogo;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(String buyStatus) {
        this.buyStatus = buyStatus;
    }
}
