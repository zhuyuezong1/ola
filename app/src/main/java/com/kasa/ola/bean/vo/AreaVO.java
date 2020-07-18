package com.kasa.ola.bean.vo;


import com.kasa.ola.ui.adapter.ProvinceAdapter;

public class AreaVO implements ProvinceAdapter.AddressInterface {
    private String area;
    private String areaCode;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String getName() {
        return area;
    }

    @Override
    public String getCode() {
        return areaCode;
    }
}
