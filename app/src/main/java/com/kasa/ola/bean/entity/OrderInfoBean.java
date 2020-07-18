package com.kasa.ola.bean.entity;

import java.util.List;

public class OrderInfoBean {
    private AddressBean address;
    private TakeAddressBean takeAddress;
    private ExpressBean express;
    private String suppliers;
    private String suppliersID;
    private String totalPrice;
    private String totalNum;
    private String shipping;
    private String redPack;
    private String coupons;
    private String realPrice;
    private String payment;
    private String remark;
    private String orderCreateTime;
    private String orderPayTime;
    private String orderCloseTime;
    private String codeImageUrl;
    private String rebatesAll;
    private List<ProductBean> productList;
    private String orderNo;
    private int orderStatus;
//    private TakeAddressBean takeAddressBean;
//
//    public TakeAddressBean getTakeAddressBean() {
//        return takeAddressBean;
//    }
//
//    public void setTakeAddressBean(TakeAddressBean takeAddressBean) {
//        this.takeAddressBean = takeAddressBean;
//    }


    public TakeAddressBean getTakeAddress() {
        return takeAddress;
    }

    public void setTakeAddress(TakeAddressBean takeAddress) {
        this.takeAddress = takeAddress;
    }

    public String getRebatesAll() {
        return rebatesAll;
    }

    public void setRebatesAll(String rebatesAll) {
        this.rebatesAll = rebatesAll;
    }

    public String getCodeImageUrl() {
        return codeImageUrl;
    }

    public void setCodeImageUrl(String codeImageUrl) {
        this.codeImageUrl = codeImageUrl;
    }

    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public ExpressBean getExpress() {
        return express;
    }

    public void setExpress(ExpressBean express) {
        this.express = express;
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

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
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

    public String getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(String realPrice) {
        this.realPrice = realPrice;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getOrderPayTime() {
        return orderPayTime;
    }

    public void setOrderPayTime(String orderPayTime) {
        this.orderPayTime = orderPayTime;
    }

    public String getOrderCloseTime() {
        return orderCloseTime;
    }

    public void setOrderCloseTime(String orderCloseTime) {
        this.orderCloseTime = orderCloseTime;
    }

    public List<ProductBean> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductBean> productList) {
        this.productList = productList;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
}
