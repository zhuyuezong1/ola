package com.kasa.ola.net;


/**
 * 网络返回基类 支持泛型
 * Created by Tamic on 2016-06-06.
 */
public class BaseResponse<T> {

    private int ret_code;
    private String err;
    private T data;

    public int getCode() {
        return ret_code;
    }

    public void setCode(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getMsg() {
        return err;
    }

    public void setMsg(String err) {
        this.err = err;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {
        return ret_code == 200;
    }

}
