package com.kasa.ola.bean.entity;

import java.io.Serializable;
import java.util.List;

public class CartBean {

    private List<ShoppingCartBean> shoppingCartList;
    private List<ShoppingCartBean> invalidProductList;

    public void setShoppingCartList(List<ShoppingCartBean> shoppingCartList) {
        this.shoppingCartList = shoppingCartList;
    }

    public List<ShoppingCartBean> getShoppingCartList() {
        return shoppingCartList;
    }

    public void setInvalidProductList(List<ShoppingCartBean> invalidProductList) {
        this.invalidProductList = invalidProductList;
    }

    public List<ShoppingCartBean> getInvalidProductList() {
        return invalidProductList;
    }

    public static class ShoppingCartBean implements Serializable {

        private String suppliersID;
        private String suppliers;
        private boolean isSelect = false;
        private List<Product> productList;
        private boolean isEffective = true;

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
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

        public List<Product> getProductList() {
            return productList;
        }

        public void setProductList(List<Product> productList) {
            this.productList = productList;
        }

        public boolean isEffective() {
            return isEffective;
        }

        public void setEffective(boolean effective) {
            isEffective = effective;
        }
    }

    public static class Product implements Serializable {

        private String shopProductID;
        private String productID;
        private String imageUrl;
        private String productName;
        private String price;
        private String specialPrice;
        private String productNum;
        private String spe;
        private String status;
        private String groupContent;
        private boolean isSelect = false;
        private String suppliers;
        private String suppliersID;
        private String rebates;

        public String getRebates() {
            return rebates;
        }

        public void setRebates(String rebates) {
            this.rebates = rebates;
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

        public String getSpe() {
            return spe;
        }

        public void setSpe(String spe) {
            this.spe = spe;
        }

        public String getSpecialPrice() {
            return specialPrice;
        }

        public void setSpecialPrice(String specialPrice) {
            this.specialPrice = specialPrice;
        }

        public String getGroupContent() {
            return groupContent;
        }

        public void setGroupContent(String groupContent) {
            this.groupContent = groupContent;
        }

        public String getProductNum() {
            return productNum;
        }

        public void setProductNum(String productNum) {
            this.productNum = productNum;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public void setShopProductID(String shopProductID) {
            this.shopProductID = shopProductID;
        }

        public String getShopProductID() {
            return shopProductID;
        }

        public void setProductID(String productID) {
            this.productID = productID;
        }

        public String getProductID() {
            return productID;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductName() {
            return productName;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPrice() {
            return price;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

    }
}
