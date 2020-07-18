package com.kasa.ola.bean.vo;


import com.kasa.ola.ui.adapter.ProvinceAdapter;

public class ProvinceVO implements ProvinceAdapter.AddressInterface {
    private String provinceName;
    private String provinceCode;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    @Override
    public String getName() {
        return provinceName;
    }

    @Override
    public String getCode() {
        return provinceCode;
    }
}
