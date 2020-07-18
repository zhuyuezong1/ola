package com.kasa.ola.bean.entity;

import java.util.List;

public class ConfirmOrderInfoBean {

    /**
     * name :
     * mobile : 15957658962
     * address : {}
     * commitInfoList : [{"suppliersID":1,"suppliers":"oula共享商城","totalNum":1,"shipping":0,"redPack":0,"coupons":0,"totalPrice":999,"realPrice":999,"productList":[{"price":"999.00","storeCount":995,"productNum":"1","productName":"Babydot绘画世界 儿童绘画年卡","productImage":"http://10.0.0.183/public/upload/goods/201911/80a3d2b73542d4bc8423ac1eb72c35cd.jpg"}]}]
     */

    private String name;
    private String mobile;
    private AddressBean address;
    private String rebatesAll;
    private List<CommitInfoListBean> commitInfoList;

    public String getRebatesAll() {
        return rebatesAll;
    }

    public void setRebatesAll(String rebatesAll) {
        this.rebatesAll = rebatesAll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public List<CommitInfoListBean> getCommitInfoList() {
        return commitInfoList;
    }

    public void setCommitInfoList(List<CommitInfoListBean> commitInfoList) {
        this.commitInfoList = commitInfoList;
    }

    public static class CommitInfoListBean {
        /**
         * suppliersID : 1
         * suppliers : oula共享商城
         * totalNum : 1
         * shipping : 0
         * redPack : 0
         * coupons : 0
         * totalPrice : 999
         * realPrice : 999
         * productList : [{"price":"999.00","storeCount":995,"productNum":"1","productName":"Babydot绘画世界 儿童绘画年卡","productImage":"http://10.0.0.183/public/upload/goods/201911/80a3d2b73542d4bc8423ac1eb72c35cd.jpg"}]
         */

        private String suppliersID;
        private String suppliers;
        private String totalNum;
        private String shipping;
        private String redPack;
        private String coupons;
        private String totalPrice;
        private String realPrice;
        private String discountType;
        private String remark;
        private List<ProductListBean> productList;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public String getSuppliersID() {
            return suppliersID;
        }

        public void setSuppliersID(String suppliersID) {
            this.suppliersID = suppliersID;
        }

        public String getSuppliers() {
            return suppliers;
        }

        public void setSuppliers(String suppliers) {
            this.suppliers = suppliers;
        }

        public String getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(String totalNum) {
            this.totalNum = totalNum;
        }

        public String getShipping() {
            return shipping;
        }

        public void setShipping(String shipping) {
            this.shipping = shipping;
        }

        public String getRedPack() {
            return redPack;
        }

        public void setRedPack(String redPack) {
            this.redPack = redPack;
        }

        public String getCoupons() {
            return coupons;
        }

        public void setCoupons(String coupons) {
            this.coupons = coupons;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getRealPrice() {
            return realPrice;
        }

        public void setRealPrice(String realPrice) {
            this.realPrice = realPrice;
        }

        public List<ProductListBean> getProductList() {
            return productList;
        }

        public void setProductList(List<ProductListBean> productList) {
            this.productList = productList;
        }

        public static class ProductListBean {
            /**
             * price : 999.00
             * storeCount : 995
             * productNum : 1
             * productName : Babydot绘画世界 儿童绘画年卡
             * productImage : http://10.0.0.183/public/upload/goods/201911/80a3d2b73542d4bc8423ac1eb72c35cd.jpg
             */

            private String price;
            private String storeCount;
            private String productNum;
            private String productName;
            private String imageUrl;
            private String productID;
            private String spe;
            private String groupContent;
            private String shopProductID;
            private String rebates;

            public String getRebates() {
                return rebates;
            }

            public void setRebates(String rebates) {
                this.rebates = rebates;
            }

            public String getShopProductID() {
                return shopProductID;
            }

            public void setShopProductID(String shopProductID) {
                this.shopProductID = shopProductID;
            }

            public String getGroupContent() {
                return groupContent;
            }

            public void setGroupContent(String groupContent) {
                this.groupContent = groupContent;
            }

            public String getProductID() {
                return productID;
            }

            public void setProductID(String productID) {
                this.productID = productID;
            }

            public String getSpe() {
                return spe;
            }

            public void setSpe(String spe) {
                this.spe = spe;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getStoreCount() {
                return storeCount;
            }

            public void setStoreCount(String storeCount) {
                this.storeCount = storeCount;
            }

            public String getProductNum() {
                return productNum;
            }

            public void setProductNum(String productNum) {
                this.productNum = productNum;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }
        }
    }
}
