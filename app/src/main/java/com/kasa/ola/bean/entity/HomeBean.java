package com.kasa.ola.bean.entity;

import java.util.List;

public class HomeBean {
    private List<BannerBean> bannerList;
    private List<MallProductBean> vipProductList;
    private List<MallProductBean> goodProductList;
    private List<MallProductBean> todayProductList;
    private List<MallProductBean> promotionProductList;
    private String rqTip;
    private String promotionBanner;

    public List<MallProductBean> getPromotionProductList() {
        return promotionProductList;
    }

    public void setPromotionProductList(List<MallProductBean> promotionProductList) {
        this.promotionProductList = promotionProductList;
    }

    public String getPromotionBanner() {
        return promotionBanner;
    }

    public void setPromotionBanner(String promotionBanner) {
        this.promotionBanner = promotionBanner;
    }

    public List<MallProductBean> getVipProductList() {
        return vipProductList;
    }

    public void setVipProductList(List<MallProductBean> vipProductList) {
        this.vipProductList = vipProductList;
    }

    public List<MallProductBean> getGoodProductList() {
        return goodProductList;
    }

    public void setGoodProductList(List<MallProductBean> goodProductList) {
        this.goodProductList = goodProductList;
    }

    public List<MallProductBean> getTodayProductList() {
        return todayProductList;
    }

    public void setTodayProductList(List<MallProductBean> todayProductList) {
        this.todayProductList = todayProductList;
    }

    public String getRqTip() {
        return rqTip;
    }

    public void setRqTip(String rqTip) {
        this.rqTip = rqTip;
    }

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
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

    public class MallProductBean{
        private String productID;
        private String imageUrl;
        private String productName;
        private String price;
        private String describe;
        private String unit;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

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

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

}
