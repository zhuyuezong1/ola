package com.kasa.ola.net;


import com.kasa.ola.bean.BaseResponseModel;

/**
 * Created by guan on 2018/6/2 0002.
 */
public interface BusinessBackListener {
    void onBusinessSuccess(BaseResponseModel responseModel);

    void onBusinessError(int code, String msg);
}
