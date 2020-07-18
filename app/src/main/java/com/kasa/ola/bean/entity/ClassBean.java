package com.kasa.ola.bean.entity;

public class ClassBean {

    private String classID;
    private String className;
    private String classLogo;
    private String classTime;
    private String classStatus;
    private String reservationNum;
    private String remainingNum;

    public String getReservationNum() {
        return reservationNum;
    }

    public void setReservationNum(String reservationNum) {
        this.reservationNum = reservationNum;
    }

    public String getRemainingNum() {
        return remainingNum;
    }

    public void setRemainingNum(String remainingNum) {
        this.remainingNum = remainingNum;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassLogo() {
        return classLogo;
    }

    public void setClassLogo(String classLogo) {
        this.classLogo = classLogo;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(String classStatus) {
        this.classStatus = classStatus;
    }
}
