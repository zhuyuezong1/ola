package com.kasa.ola.manager;

import android.content.Context;

import com.kasa.ola.R;
import com.kasa.ola.json.JSONException;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.utils.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WechatPayManager {

    private static WechatPayManager instance = null;

    private IWXAPI api;
    private OnWechatPayListener mOnWechatPayListener = null;

    private WechatPayManager() {
    }

    public static synchronized WechatPayManager get() {
        if (instance == null) {
            instance = new WechatPayManager();
        }
        return instance;
    }

    public void pay(Context context, JSONObject order, OnWechatPayListener l) {
        this.mOnWechatPayListener = l;
        api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(Const.APP_ID);
        PayReq req = new PayReq();
        try {
            req.appId = Const.APP_ID;
            req.partnerId = order.optString("partnerid");
            req.prepayId = order.optString("prepayid");
            req.nonceStr = order.optString("noncestr");
            req.timeStamp = order.optString("timestamp");
            req.packageValue = order.optString("package");
            req.sign = order.optString("sign");
            req.extData = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            if (!api.sendReq(req)) {
                ToastUtils.showShortToast(context, context.getString(R.string.wechat_request_fail));
                onWechatPayRequestError();
            } else {
                onWechatPayRequestSuccess();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showShortToast(context, context.getString(R.string.wechat_request_fail));
            onWechatPayRequestError();
        }

    }

    public void onWechatPayRequestError() {
        if (mOnWechatPayListener != null) {
            mOnWechatPayListener.onWechatPayRequestError();
            mOnWechatPayListener = null;
        }
    }

    public void onWechatPayRequestSuccess() {
        if (mOnWechatPayListener != null) {
            mOnWechatPayListener.onWechatPayRequestSuccess();
        }
    }

    public void onWechatPayError(int code, String msg) {
        if (mOnWechatPayListener != null) {
            mOnWechatPayListener.onWechatPayError(code, msg);
            mOnWechatPayListener = null;
        }
    }

    public void onWechatPaySuccess() {
        if (mOnWechatPayListener != null) {
            mOnWechatPayListener.onWechatPaySuccess();
            mOnWechatPayListener = null;
        }
    }

    public interface OnWechatPayListener {
        // 发起支付请求出错
        void onWechatPayRequestError();

        // 发起支付请求成功
        void onWechatPayRequestSuccess();

        // 支付失败
        void onWechatPayError(int code, String msg);

        // 支付成功
        void onWechatPaySuccess();
    }

}
