package com.kasa.ola.wxapi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.kasa.ola.R;
import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.BitmapUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

public class WXShareEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private String imgUrl;
    private Bitmap bitmap;
    private String m_url;
    private String m_title;
    private String m_description;
    private int logoType;
    private String shareText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int wxShareType = getIntent().getIntExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, 0);
        shareText = getIntent().getStringExtra(Const.SHARE_TEXT);
        imgUrl = getIntent().getStringExtra(Const.EXTRA_KEY_SHARE_IMAGE_URL);
        m_url = getIntent().getStringExtra(Const.EXTRA_KEY_SHARE_WECHAT_URL);
        m_title = getIntent().getStringExtra(Const.EXTRA_KEY_SHARE_WECHAT_TITLE);
        m_description = getIntent().getStringExtra(Const.EXTRA_KEY_SHARE_WECHAT_DESCRIPTION);
        logoType = getIntent().getIntExtra(Const.IMAGE_LOGO_TYPE,0);
        api = WXAPIFactory.createWXAPI(this, Const.APP_ID);
        api.registerApp(Const.APP_ID);
        if (isWechatAvailable(this, api)) {
            if (!TextUtils.isEmpty(shareText)){
                shareText(wxShareType,shareText);
            }else {
                wechatShare(wxShareType);
            }
        } else {
            finish();
        }
    }
    private void shareText(int flag,String text){
        WXTextObject wxTextObject = new WXTextObject();
        wxTextObject.text = text;
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.mediaObject = wxTextObject;
        wxMediaMessage.description = "商品分享";
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
        finish();
    }

    private void wechatShare(final int flag) {
        if (!TextUtils.isEmpty(imgUrl)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bitmap = Glide.with(WXShareEntryActivity.this).asBitmap().load(imgUrl).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        if (bitmap == null) {
                            if (logoType==0){
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo);
                            }else if (logoType==1){
                                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_launcher_holiday);
                            }
                        }

                        WXWebpageObject webpage = new WXWebpageObject();
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        webpage.webpageUrl = m_url;
                        msg.title = m_title;
                        msg.description = m_description;
                        Bitmap createBitmapThumbnail = createBitmapThumbnail(bitmap);
                        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                        boolean compress = createBitmapThumbnail.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream);
                        if (compress) {
                            msg.thumbData = arrayOutputStream.toByteArray();
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis());
                        req.message = msg;
                        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                        api.sendReq(req);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        bitmap = BitmapUtils.getBitmapFromResource(getResources(),R.mipmap.icon_logo);
                        if (logoType==0){
                            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo);
                        }else if (logoType==1){
                            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_launcher_holiday);
                        }
                        WXWebpageObject webpage = new WXWebpageObject();
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        webpage.webpageUrl = m_url;
                        msg.title = m_title;
                        msg.description = m_description;
                        Bitmap createBitmapThumbnail = createBitmapThumbnail(bitmap);
                        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                        boolean compress = createBitmapThumbnail.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream);
                        if (compress) {
                            msg.thumbData = arrayOutputStream.toByteArray();
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = String.valueOf(System.currentTimeMillis());
                        req.message = msg;
                        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                        api.sendReq(req);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private boolean isWechatAvailable(Context context, IWXAPI api) {
        return api.isWXAppInstalled();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

    }
    public Bitmap createBitmapThumbnail(Bitmap bitMap) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        int newWidth = 70;
        int newHeight = newWidth*height/width;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
                matrix, true);
        return newBitMap;
    }
}
