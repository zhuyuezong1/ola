package com.kasa.ola.bean.entity;

import java.util.List;

public class HomeTopBean {

//    private String invitedUrl;
    private String limitedUrl;
    private String buyUrl;
    private String parcelsUrl;
    private BannerBean ads;
    private List<ProductBannerBean> carouselrList;
    private List<BannerBean> bannerList;
    private List<ProductBean> limitedList;
    private List<ProductBean> agriculturalList;
    private List<ProductBean> highReturnList;
    private List<ProductBean> parcelsList;
    private List<HomeQualityBean> selectList;

    public class HomeQualityBean{
        private String suppliersID;
        private String suppliersName;
        private String address;
        private String bgColor;

        public String getBgColor() {
            return bgColor;
        }

        public void setBgColor(String bgColor) {
            this.bgColor = bgColor;
        }

        private List<ProductBean> productList;
        public String getSuppliersID() {
            return suppliersID;
        }

        public void setSuppliersID(String suppliersID) {
            this.suppliersID = suppliersID;
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

        public List<ProductBean> getProductList() {
            return productList;
        }

        public void setProductList(List<ProductBean> productList) {
            this.productList = productList;
        }
    }

    public List<HomeQualityBean> getSelectList() {
        return selectList;
    }

    public void setSelectList(List<HomeQualityBean> selectList) {
        this.selectList = selectList;
    }

    public List<ProductBean> getLimitedList() {
        return limitedList;
    }

    public void setLimitedList(List<ProductBean> limitedList) {
        this.limitedList = limitedList;
    }

    public List<ProductBean> getAgriculturalList() {
        return agriculturalList;
    }

    public void setAgriculturalList(List<ProductBean> agriculturalList) {
        this.agriculturalList = agriculturalList;
    }

    public List<ProductBean> getHighReturnList() {
        return highReturnList;
    }

    public void setHighReturnList(List<ProductBean> highReturnList) {
        this.highReturnList = highReturnList;
    }

    public List<ProductBean> getParcelsList() {
        return parcelsList;
    }

    public void setParcelsList(List<ProductBean> parcelsList) {
        this.parcelsList = parcelsList;
    }

    public BannerBean getAds() {
        return ads;
    }
    public void setAds(BannerBean ads) {
        this.ads = ads;
    }



    public String getLimitedUrl() {
        return limitedUrl;
    }

    public void setLimitedUrl(String limitedUrl) {
        this.limitedUrl = limitedUrl;
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

    public List<ProductBannerBean> getCarouselrList() {
        return carouselrList;
    }

    public void setCarouselrList(List<ProductBannerBean> carouselrList) {
        this.carouselrList = carouselrList;
    }

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    public class HomeWebProductBean{
        private String productID;
        private String imageUrl;
        private String specialPrice;
        private String rebates;

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getSpecialPrice() {
            return specialPrice;
        }

        public void setSpecialPrice(String specialPrice) {
            this.specialPrice = specialPrice;
        }

        public String getRebates() {
            return rebates;
        }

        public void setRebates(String rebates) {
            this.rebates = rebates;
        }
    }

    public class BannerBean{
        private String imageUrl;
        private String webUrl;
        private String title;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public class ProductBannerBean {
        private String imageUrl;
        private String productID;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getProductID() {
            return productID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }
    }
}
