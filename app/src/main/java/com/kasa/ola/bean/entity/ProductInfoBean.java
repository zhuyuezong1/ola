package com.kasa.ola.bean.entity;

import java.util.List;

/**
 * 用于测试新的规格方式
 */
public class ProductInfoBean {


    /**
     * productID : 40
     * productName : 男士韩版高帮休闲鞋
     * status : 1
     * suppliersID : 1
     * sales : 0
     * describe : 2019新款秋季，男士韩版潮鞋高帮学生白鞋
     * price : 0.00
     * rebates : 4.08
     * specialPrice : 59.00
     * suppliers : oula共享商城
     * imgArr : [{"imageUrl":"http://10.0.0.183/public/upload/goods/201910/7505439710dbd05d50cd4a9e8e61e8b7.JPG"},{"imageUrl":"http://10.0.0.183/public/upload/goods/201910/9f92c1078140beead750f02f9c3ea907.JPG"},{"imageUrl":"http://10.0.0.183/public/upload/goods/201910/15317878ba73be223ae79cf57fe37e3e.JPG"},{"imageUrl":"http://10.0.0.183/public/upload/goods/201910/6c2e8e95bc19a24ad45259d8ee89ef73.JPG"}]
     * priceGroup : [{"totalPrice":"59.00","groupContent":"61_74","inventory":200},{"totalPrice":"59.00","groupContent":"60_74","inventory":0},{"totalPrice":"59.00","groupContent":"59_74","inventory":200},{"totalPrice":"59.00","groupContent":"58_74","inventory":200},{"totalPrice":"59.00","groupContent":"57_74","inventory":200},{"totalPrice":"59.00","groupContent":"56_74","inventory":200},{"totalPrice":"59.00","groupContent":"61_73","inventory":200},{"totalPrice":"59.00","groupContent":"60_73","inventory":200},{"totalPrice":"59.00","groupContent":"59_73","inventory":200},{"totalPrice":"59.00","groupContent":"58_73","inventory":0},{"totalPrice":"59.00","groupContent":"57_73","inventory":200},{"totalPrice":"59.00","groupContent":"56_73","inventory":200},{"totalPrice":"59.00","groupContent":"61_72","inventory":200},{"totalPrice":"59.00","groupContent":"60_72","inventory":200},{"totalPrice":"59.00","groupContent":"59_72","inventory":200},{"totalPrice":"59.00","groupContent":"58_72","inventory":200},{"totalPrice":"59.00","groupContent":"57_72","inventory":0},{"totalPrice":"59.00","groupContent":"56_72","inventory":200}]
     * specifications : [{"speTitle":"尺码","speItem":[{"speID":56,"speName":"39","speImgUrl":""},{"speID":57,"speName":"40","speImgUrl":""},{"speID":58,"speName":"41","speImgUrl":""},{"speID":59,"speName":"42","speImgUrl":""},{"speID":60,"speName":"43","speImgUrl":""},{"speID":61,"speName":"44","speImgUrl":""}]},{"speTitle":"颜色","speItem":[{"speID":72,"speName":"白灰","speImgUrl":"http://10.0.0.183/public/upload/goods/201910/56a5527c49aae871f088e5dbccc58c91.JPG"},{"speID":73,"speName":"白红","speImgUrl":"http://10.0.0.183/public/upload/goods/201910/cabbc195a92c6b07032f6405ff090f3a.JPG"},{"speID":74,"speName":"白蓝","speImgUrl":"http://10.0.0.183/public/upload/goods/201910/45a4d92c1182d2207c45736b545ec766.JPG"}]}]
     */

    private int productID;
    private String productName;
    private int status;
    private int suppliersID;
    private int sales;
    private String describe;
    private String price;
    private String rebates;
    private String specialPrice;
    private String suppliers;
    private List<ImgArrBean> imgArr;
    private List<PriceGroupBean> priceGroup;
    private List<SpecificationsBean> specifications;
    private List<ProductCommentBean> comments;
    private List<String> detailImgArr;
    private String cartItemCount;
    private String shareText;

    public String getShareText() {
        return shareText;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public String getCartItemCount() {
        return cartItemCount;
    }

    public void setCartItemCount(String cartItemCount) {
        this.cartItemCount = cartItemCount;
    }

    public List<String> getDetailImgArr() {
        return detailImgArr;
    }

    public void setDetailImgArr(List<String> detailImgArr) {
        this.detailImgArr = detailImgArr;
    }

    public List<ProductCommentBean> getComments() {
        return comments;
    }

    public void setComments(List<ProductCommentBean> comments) {
        this.comments = comments;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSuppliersID() {
        return suppliersID;
    }

    public void setSuppliersID(int suppliersID) {
        this.suppliersID = suppliersID;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRebates() {
        return rebates;
    }

    public void setRebates(String rebates) {
        this.rebates = rebates;
    }

    public String getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(String specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers;
    }

    public List<ImgArrBean> getImgArr() {
        return imgArr;
    }

    public void setImgArr(List<ImgArrBean> imgArr) {
        this.imgArr = imgArr;
    }

    public List<PriceGroupBean> getPriceGroup() {
        return priceGroup;
    }

    public void setPriceGroup(List<PriceGroupBean> priceGroup) {
        this.priceGroup = priceGroup;
    }

    public List<SpecificationsBean> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<SpecificationsBean> specifications) {
        this.specifications = specifications;
    }

    public static class ImgArrBean {
        /**
         * imageUrl : http://10.0.0.183/public/upload/goods/201910/7505439710dbd05d50cd4a9e8e61e8b7.JPG
         */

        private String imageUrl;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static class PriceGroupBean {
        /**
         * totalPrice : 59.00
         * groupContent : 61_74
         * inventory : 200
         */

        private String totalPrice;
        private String groupContent;
        private int inventory;

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getGroupContent() {
            return groupContent;
        }

        public void setGroupContent(String groupContent) {
            this.groupContent = groupContent;
        }

        public int getInventory() {
            return inventory;
        }

        public void setInventory(int inventory) {
            this.inventory = inventory;
        }
    }

    public static class SpecificationsBean {
        /**
         * speTitle : 尺码
         * speItem : [{"speID":56,"speName":"39","speImgUrl":""},{"speID":57,"speName":"40","speImgUrl":""},{"speID":58,"speName":"41","speImgUrl":""},{"speID":59,"speName":"42","speImgUrl":""},{"speID":60,"speName":"43","speImgUrl":""},{"speID":61,"speName":"44","speImgUrl":""}]
         */

        private String speTitle;
        private List<SpeItemBean> speItem;

        public String getSpeTitle() {
            return speTitle;
        }

        public void setSpeTitle(String speTitle) {
            this.speTitle = speTitle;
        }

        public List<SpeItemBean> getSpeItem() {
            return speItem;
        }

        public void setSpeItem(List<SpeItemBean> speItem) {
            this.speItem = speItem;
        }

        public static class SpeItemBean {
            /**
             * speID : 56
             * speName : 39
             * speImgUrl :
             */

            private String speID;
            private String speName;
            private String speImgUrl;
            private boolean isSelect = false;
            private boolean isCanSelect = true;

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean select) {
                isSelect = select;
            }

            public boolean isCanSelect() {
                return isCanSelect;
            }

            public void setCanSelect(boolean canSelect) {
                isCanSelect = canSelect;
            }

            public String getSpeID() {
                return speID;
            }

            public void setSpeID(String speID) {
                this.speID = speID;
            }

            public String getSpeName() {
                return speName;
            }

            public void setSpeName(String speName) {
                this.speName = speName;
            }

            public String getSpeImgUrl() {
                return speImgUrl;
            }

            public void setSpeImgUrl(String speImgUrl) {
                this.speImgUrl = speImgUrl;
            }
        }
    }
}
