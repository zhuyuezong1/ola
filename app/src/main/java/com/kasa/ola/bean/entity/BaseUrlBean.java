package com.kasa.ola.bean.entity;

public class BaseUrlBean {
    private String aboutUrl = "";
    private String privacyEnsureUrl = "";
    private String userProUrl = "";
    private String downloadUrl = "";
    private String androidDownloadUrl = "";
    private String androidFirDownloadUrl = "";
    private String serviceMobile = "";
    private String androidVersionCode = "1.0";
    private int isOnline = 1;  //0：未上线  1：已上线
    private String noticeUrl;
    private String noticeImg;
    private int isHaveNotice = 0;//是否有公告  “0”无公告  “1”有公告
    private int noticeVersion = 1;//公告版本号  从“1”开始自增
    private String distributionUrl;
    private String refundUrl;
    private String infoUrl;
    private String gaofanUrl;
    private String discountUrl;
    private String buyUrl;
    private String parcelsUrl;
    private String voucherUrl;
    private String limitedUrl;
    private String redPackUrl;
    private String signUrl;
    private String shareUrl;
    private String specialUrl;
    private String activityUrl;
    private String isVipSwitch;//0 关，1 开
    public String getAndroidFirDownloadUrl() {
        return androidFirDownloadUrl;
    }

    public void setAndroidFirDownloadUrl(String androidFirDownloadUrl) {
        this.androidFirDownloadUrl = androidFirDownloadUrl;
    }

    public String getActivityUrl() {
        return activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

    public String getSpecialUrl() {
        return specialUrl;
    }

    public void setSpecialUrl(String specialUrl) {
        this.specialUrl = specialUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getRedPackUrl() {
        return redPackUrl;
    }

    public void setRedPackUrl(String redPackUrl) {
        this.redPackUrl = redPackUrl;
    }

    public String getGaofanUrl() {
        return gaofanUrl;
    }

    public void setGaofanUrl(String gaofanUrl) {
        this.gaofanUrl = gaofanUrl;
    }

    public String getDiscountUrl() {
        return discountUrl;
    }

    public void setDiscountUrl(String discountUrl) {
        this.discountUrl = discountUrl;
    }

    public String getBuyUrl() {
        return buyUrl;
    }

    public void setBuyUrl(String buyUrl) {
        this.buyUrl = buyUrl;
    }

    public String getParcelsUrl() {
        return parcelsUrl;
    }

    public void setParcelsUrl(String parcelsUrl) {
        this.parcelsUrl = parcelsUrl;
    }

    public String getVoucherUrl() {
        return voucherUrl;
    }

    public void setVoucherUrl(String voucherUrl) {
        this.voucherUrl = voucherUrl;
    }

    public String getLimitedUrl() {
        return limitedUrl;
    }

    public void setLimitedUrl(String limitedUrl) {
        this.limitedUrl = limitedUrl;
    }

    public String getIsVipSwitch() {
        return isVipSwitch;
    }

    public void setIsVipSwitch(String isVipSwitch) {
        this.isVipSwitch = isVipSwitch;
    }

    public String getSignUrl() {
        return signUrl;
    }

    public void setSignUrl(String signUrl) {
        this.signUrl = signUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getDistributionUrl() {
        return distributionUrl;
    }

    public void setDistributionUrl(String distributionUrl) {
        this.distributionUrl = distributionUrl;
    }

    public String getRefundUrl() {
        return refundUrl;
    }

    public void setRefundUrl(String refundUrl) {
        this.refundUrl = refundUrl;
    }

    public String getAboutUrl() {
        return aboutUrl;
    }

    public void setAboutUrl(String aboutUrl) {
        this.aboutUrl = aboutUrl;
    }

    public String getPrivacyEnsureUrl() {
        return privacyEnsureUrl;
    }

    public void setPrivacyEnsureUrl(String privacyEnsureUrl) {
        this.privacyEnsureUrl = privacyEnsureUrl;
    }

    public String getUserProUrl() {
        return userProUrl;
    }

    public void setUserProUrl(String userProUrl) {
        this.userProUrl = userProUrl;
    }


    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getAndroidDownloadUrl() {
        return androidDownloadUrl;
    }

    public void setAndroidDownloadUrl(String androidDownloadUrl) {
        this.androidDownloadUrl = androidDownloadUrl;
    }

    public String getServiceMobile() {
        return serviceMobile;
    }

    public void setServiceMobile(String serviceMobile) {
        this.serviceMobile = serviceMobile;
    }

    public String getAndroidVersionCode() {
        return androidVersionCode;
    }

    public void setAndroidVersionCode(String androidVersionCode) {
        this.androidVersionCode = androidVersionCode;
    }


    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }

    public String getNoticeImg() {
        return noticeImg;
    }

    public void setNoticeImg(String noticeImg) {
        this.noticeImg = noticeImg;
    }



    public int getIsHaveNotice() {
        return isHaveNotice;
    }

    public void setIsHaveNotice(int isHaveNotice) {
        this.isHaveNotice = isHaveNotice;
    }

    public int getNoticeVersion() {
        return noticeVersion;
    }

    public void setNoticeVersion(int noticeVersion) {
        this.noticeVersion = noticeVersion;
    }


}
