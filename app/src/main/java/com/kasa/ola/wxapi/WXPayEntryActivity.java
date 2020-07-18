package com.kasa.ola.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kasa.ola.R;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.WechatPayManager;
import com.kasa.ola.utils.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Const.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    /**
     * @param resp 0	成功	展示成功页面
     *             -1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
     *             -2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
     */
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode != 0) {
                ToastUtils.showShortToast(this, getString(R.string.pay_failed));
                WechatPayManager.get().onWechatPayError(resp.errCode, resp.errStr);
            } else {
                ToastUtils.showShortToast(this, getString(R.string.pay_succeed));
                WechatPayManager.get().onWechatPaySuccess();
            }
        }
        finish();
    }
}