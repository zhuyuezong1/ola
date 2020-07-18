package com.kasa.ola.bean.entity;

public class PriceGroupBean {


    /**
     * groupContent : 59_150_516
     * totalPrice : 0.00
     * inventory : 999
     */

    private String groupContent;
    private String totalPrice;
    private int inventory;

    public String getGroupContent() {
        return groupContent;
    }

    public void setGroupContent(String groupContent) {
        this.groupContent = groupContent;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }
}
