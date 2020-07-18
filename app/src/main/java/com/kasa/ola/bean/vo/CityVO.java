package com.kasa.ola.bean.vo;


import com.kasa.ola.ui.adapter.ProvinceAdapter;

public class CityVO implements ProvinceAdapter.AddressInterface{
    private String city;
    private String cityCode;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @Override
    public String getName() {
        return city;
    }

    @Override
    public String getCode() {
        return cityCode;
    }
}
