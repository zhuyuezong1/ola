package com.kasa.ola.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.net.BaseSubscriber;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.net.ExceptionHandle;
import com.kasa.ola.net.RequestListener;
import com.kasa.ola.net.RetrofitClient;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by guan on 2018/6/2 0002.
 */
public class ApiManager {

    private static ApiManager instance = null;

    public static synchronized ApiManager get() {
        if (null == instance) {
            synchronized (ApiManager.class) {
                if (instance == null) {
                    instance = new ApiManager();
                }
            }
        }
        return instance;
    }

    private BaseSubscriber<ResponseBody> initBaseSubscriber(final BusinessBackListener backListener) {
        return new BaseSubscriber<ResponseBody>() {

            @Override
            public void onError(ExceptionHandle.ResponeThrowable e) {
                if (null != backListener) {
                    backListener.onBusinessError(e.code, e.message);
                }

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                if (null != backListener) {
                    try {
                        String responseStr = responseBody.string();
                        if (!TextUtils.isEmpty(responseStr)) {
                            Log.v("response text", responseStr);
                            BaseResponseModel responseModel = new BaseResponseModel(responseStr);
                            switch (responseModel.resultCode) {
                                case 1:
                                    LoginHandler.get().logout();
//                                    Activity topActivity = ActivityUtils.getTopActivity();
                                    Activity activity = ActivityUtils.finishTopActivity(MainActivity.class);
//                                    ActivityUtils.finishOtherActivity(HomeActivity.class);//首页
                                    if (activity!=null) {
                                        ToastUtils.showShortToast(activity, responseModel.resultCodeDetail);
//                                        Intent intent = new Intent(topActivity, LoginActivity.class);//登录页
//                                        topActivity.startActivity(intent);
                                        EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
                                    }
                                    break;
                                case -1:
                                    backListener.onBusinessError(responseModel.resultCode, responseModel.resultCodeDetail);
                                    break;
                                case 0:
                                    backListener.onBusinessSuccess(responseModel);
                                    break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        };
    }

    public void getData(String tag, JSONObject bodyJo, final BusinessBackListener backListener, RequestListener requestListener) {
        JSONObject requestJo = new JSONObject();
        requestJo.put("body", bodyJo);
        requestJo.put("head", RequestHeadManager.get().getHead());  // 暂时删除“head”
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJo.toString());
        BaseSubscriber<ResponseBody> baseSubscriber = initBaseSubscriber(backListener);
        baseSubscriber.send(requestListener);
        RetrofitClient.getInstance(App.getApp()).createBaseApi().post(tag /*+ ".php"*/, body, baseSubscriber);
        Log.v("request text", requestJo.toString());
    }

    public void uploadPictures(String tag, JSONObject bodyJo,/* List<File> files, List<String> fileKeys,*/File file, final BusinessBackListener backListener, RequestListener requestListener) {
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"), bodyJo.toString());
        RequestBody jsonHead = RequestBody.create(MediaType.parse("application/json"), RequestHeadManager.get().getHead().toString());
        Map<String, RequestBody> maps = new HashMap<>();
        maps.put("body", jsonBody);
        maps.put("head", jsonHead);
        RequestBody imgBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        maps.put("file", imgBody);
//        for (int i = 0; i < files.size(); i++) {
//            RequestBody imgBody = RequestBody.create(MediaType.parse("image/jpeg"), files.get(i));
//            maps.put(fileKeys.get(i) + "\"; filename=\"" + fileKeys.get(i), imgBody);
//        }
        BaseSubscriber<ResponseBody> baseSubscriber = initBaseSubscriber(backListener);
        baseSubscriber.send(requestListener);
        RetrofitClient.getInstance(App.getApp()).createBaseApi().upload(tag /*+ ".php"*/, maps, baseSubscriber);
    }
    public void uploadSinglePicture(String tag,File file,final BusinessBackListener backListener, RequestListener requestListener){
        HashMap<String, String> head = new HashMap<>();
        head.put("Content-Type","application/json");
        head.put("token",TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("token"))?"":
                LoginHandler.get().getMyInfo().optString("token"));
        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM).
                addFormDataPart("file",
                        file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .build();
        BaseSubscriber<ResponseBody> baseSubscriber = initBaseSubscriber(backListener);
        baseSubscriber.send(requestListener);
        RetrofitClient.getInstance(App.getApp(),RetrofitClient.baseUrl,head).createBaseApi().post(tag /*+ ".php"*/, formBody, baseSubscriber);
    }
    /**
     * 获取验证码
     *
     * @param mobile
     * @param type         0:注册    1：修改密码    2：修改支付密码
     * @param backListener
     */
    public void getVerifyCode(Context context, String mobile, int type, BusinessBackListener backListener) {
        LoadingDialog verifyDialog = new LoadingDialog.Builder(context)
                .setMessage(context.getString(R.string.get_verify_tips)).create();
        JSONObject jo = new JSONObject();
        jo.put("mobile", mobile);
        jo.put("type", type);
        ApiManager.get().getData(Const.GET_VERIFY_CODE, jo, backListener, verifyDialog);
    }

    /**
     * 登录
     *
     * @param context
     * @param mobile
     * @param password
     * @param backListener
     */
    public void Login(Context context, String mobile, String password, BusinessBackListener backListener) {
        LoadingDialog loginDialog = new LoadingDialog.Builder(context)
                .setMessage(context.getString(R.string.login_tips)).create();
        JSONObject jo = new JSONObject();
        jo.put("mobile", mobile);
        jo.put("password", password);
        jo.put("registrationID", JPushInterface.getRegistrationID(context));
        ApiManager.get().getData(Const.USER_LOGIN, jo, backListener, loginDialog);
    }

    public void loginOut(BusinessBackListener businessBackListener, RequestListener requestListener) {
        ApiManager.get().getData(Const.LOGIN_OUT,
                new JSONObject().put("userID", LoginHandler.get().getUserId()),
                businessBackListener, requestListener);
    }

    public void getMyInfo(BusinessBackListener backListener) {
        if (LoginHandler.get().checkLogined()) {
            ApiManager.get().getData(Const.GET_MY_INFO, new JSONObject().put("userID", LoginHandler.get().getUserId()),
                    backListener == null ? new BusinessBackListener() {
                        @Override
                        public void onBusinessSuccess(BaseResponseModel responseModel) {
                            if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                                JSONObject jo = (JSONObject) responseModel.data;
                                LoginHandler.get().saveMyInfo(jo);
                            }
                        }

                        @Override
                        public void onBusinessError(int code, String msg) {
                        }
                    } : backListener, null);
        }
    }

    public void updateMallOrderState(String orderID,int type, BusinessBackListener l, Context context) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("orderID", orderID);
        jo.put("type", type);
        ApiManager.get().getData(Const.UPDATE_ORDER_STATE_TAG, jo, l,
                new LoadingDialog.Builder(context).setMessage(context.getString(R.string.submitting_tips)).create());
    }

}
