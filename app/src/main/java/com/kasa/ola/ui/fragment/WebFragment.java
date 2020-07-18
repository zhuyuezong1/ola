package com.kasa.ola.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.entity.H5BackBean;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.ui.ProductInfoActivity;
import com.kasa.ola.ui.WebActivity;
import com.kasa.ola.ui.listener.OnBottomGridShareListener;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.jsbridge.BridgeHandler;
import com.kasa.ola.widget.jsbridge.BridgeWebView;
import com.kasa.ola.widget.jsbridge.CallBackFunction;
import com.kasa.ola.widget.jsbridge.DefaultHandler;
import com.kasa.ola.wxapi.WXShareEntryActivity;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class WebFragment extends BaseFragment implements View.OnClickListener {

    private BridgeWebView mWebView;
    private String url = "";
    private String title = "";
    private boolean isProperty = true;
    private ProgressBar webPgBar;
    private String curUrl = "";
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout view_actionbar_frag;
    private int count = 0;
    private View rootView;
    private TextView tv_title_frag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (null != getArguments()) {
            url = getArguments().getString(Const.INTENT_URL);
            title = getArguments().getString(Const.WEB_TITLE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web, container, false);
        initView(0);
        return rootView;
    }

    @SuppressLint("WrongConstant")
    private void initView(int type) {//0:常规，1，网页回调登录(红包)
        if (TextUtils.isEmpty(title)) {
            rootView.findViewById(R.id.view_actionbar_frag).setVisibility(View.GONE);
        } else {
            //            setActionBar(rootView, title);
            View actionBar = rootView.findViewById(R.id.view_actionbar_frag);
            actionBar.setVisibility(View.VISIBLE);
            tv_title_frag = actionBar.findViewById(R.id.tv_title_frag);
            tv_title_frag.setText(title);
            TextView tv_right_text_frag = actionBar.findViewById(R.id.tv_right_text_frag);
            tv_right_text_frag.setTextColor(getResources().getColor(R.color.textColor_actionBar_right));
            tv_right_text_frag.setText(getString(R.string.share));
            if (title.equals("年终大回馈")){
                tv_right_text_frag.setVisibility(View.VISIBLE);
            }else {
                tv_right_text_frag.setVisibility(View.GONE);
            }

            tv_right_text_frag.setOnClickListener(this);
            actionBar.findViewById(R.id.iv_back_frag).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!clickBack()){
                        finish();
                    }
                }
            });
        }

        if (type==1){
            if (url.contains(LoginHandler.get().getBaseUrlBean().getRedPackUrl())){
                url = LoginHandler.get().getBaseUrlBean().getRedPackUrl()+ "?userID=" + LoginHandler.get().getUserId() + "&token=" + LoginHandler.get().getToken();
            }
        }else {

        }

        View statusBar = rootView.findViewById(R.id.view_status_bar);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) statusBar.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        statusBar.setBackgroundColor(Color.parseColor("#000000"));
        webPgBar = rootView.findViewById(R.id.webProgressBar);
        webPgBar.setVisibility(View.VISIBLE);
        mWebView = rootView.findViewById(R.id.view_web);
//        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setSupportZoom(false);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setUseWideViewPort(true);
////        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
////        webSettings.setTextZoom(100);
////        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabaseEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSettings.setBlockNetworkImage(false); // 解决图片不显示
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
//        mWebView.setFocusable(true);
//        mWebView.setFocusableInTouchMode(true);
//        mWebView.setWebViewClient(new BaseWebViewClient());
//        mWebView.setWebChromeClient(new BaseWebChromeClient());
//        mWebView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                    case MotionEvent.ACTION_UP:
//                        if (!v.hasFocus()) {
//                            v.requestFocus();
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
        WebSettings webSettings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.LOAD_NORMAL);
        }
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.requestFocus();
        mWebView.setScrollBarStyle(View.SCROLL_AXIS_NONE);


        mWebView.setDefaultHandler(new DefaultHandler());
        mWebView.setWebChromeClient(new BaseWebChromeClient());
//        mWebView.setWebViewClient(new BaseWebViewClient());

        mWebView.loadUrl(url);

        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(url);
            }
        });
        initEvent();
        mWebView.callHandler("on_native_msg", "Android传递给js的数据", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
//                LogUtil.d("WebFragment------------", data);
                ToastUtils.showLongToast(getContext(), data);
            }
        });
        mWebView.send("hello");
    }

    private void initEvent() {
        mWebView.registerHandler("on_native_click", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                H5BackBean h5BackBean = JsonUtils.jsonToObject(data, H5BackBean.class);
                if (h5BackBean.getAction().equals("goProductItem")) {
                    Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                    intent.putExtra(Const.MALL_GOOD_ID_KEY,h5BackBean.getParams().getMsg());
                    startActivity(intent);
                }else if (h5BackBean.getAction().equals("login")) {
                    CommonUtils.checkLogin(getContext());
                }else if (h5BackBean.getAction().equals("goBack")) {
                    finish();
                }else if (h5BackBean.getAction().equals("goShare")){
                    final String shareUrl = h5BackBean.getParams().getUrl();
                    final String title = h5BackBean.getParams().getMsg();
                    final String content = h5BackBean.getParams().getContent();
                    CommonUtils.showShareBottomSheetGrid(getContext(), new OnBottomGridShareListener() {
                        @Override
                        public void wechatShare() {
                            Intent wechatIntent = new Intent(getContext(), WXShareEntryActivity.class);
                            wechatIntent.putExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, Const.WECHAT_SHARE_TYPE_WECHAT);
                            wechatIntent.putExtra(Const.IMAGE_LOGO_TYPE, Const.LOGO_TYPE);
                            wechatIntent.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_URL, shareUrl);
                            wechatIntent.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_TITLE, title);
                            wechatIntent.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_DESCRIPTION,content);
                            getContext().startActivity(wechatIntent);
                        }

                        @Override
                        public void wechatFriendShare() {
                            Intent friendsIntent = new Intent(getContext(), WXShareEntryActivity.class);
                            friendsIntent.putExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, Const.WECHAT_SHARE_TYPE_FRIENDS);
                            friendsIntent.putExtra(Const.IMAGE_LOGO_TYPE, Const.LOGO_TYPE);
                            friendsIntent.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_URL, shareUrl);
                            friendsIntent.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_TITLE, title);
                            friendsIntent.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_DESCRIPTION,content);
                            getContext().startActivity(friendsIntent);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right_text_frag:
                if (CommonUtils.checkLogin(getContext())){
                    String redPackShareUrl = "";
//                    redPackShareUrl =  LoginHandler.get().getBaseUrlBean().getRedPackUrl()+ "?app=android";
                    if (!TextUtils.isEmpty(LoginHandler.get().getUserId())) {
                        redPackShareUrl = LoginHandler.get().getBaseUrlBean().getRedPackUrl() + "?userID=" + LoginHandler.get().getUserId();
                    } else {
                        redPackShareUrl =  LoginHandler.get().getBaseUrlBean().getRedPackUrl();
                    }
                    CommonUtils.showShareBottomSheetGrid(getContext(), redPackShareUrl,1);
                }
                break;
        }
    }


    private class BaseWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url,
                                           boolean isReload) {

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            refreshLayout.setRefreshing(false);
        }

    }


    private class BaseWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                webPgBar.setVisibility(View.INVISIBLE);//加载完网页进度条消失
                refreshLayout.setRefreshing(false);
            } else {
                webPgBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                webPgBar.setProgress(newProgress);//设置进度值
            }
        }

        @Override
        public void onReceivedTitle(WebView webView, String webViewTitle) {
            if (webView!=null&&tv_title_frag!=null&&!TextUtils.isEmpty(title)&&title.equals(getString(R.string.info_title))){
                String webTitle = webView.getTitle();
                tv_title_frag.setText(webTitle);
            }
            super.onReceivedTitle(webView, title);
        }
    }

    public boolean clickBack() {
        if (null != mWebView) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return false;
    }

    private void needRefresh(String url) {
        if (url.contains("type")) {
            ApiManager.get().getMyInfo(null);
            EventBus.getDefault().post(new EventCenter.H5Refresh());
        }
    }

    private boolean h5JumpNative(String url) {
        if (url.contains(APP_INDEX)) {
            EventBus.getDefault().post(new EventCenter.HomeSwitch(3));
            ActivityUtils.finishOtherActivity(MainActivity.class);
            return true;
        }
        if (url.contains(APP_MEMBER)) {
            EventBus.getDefault().post(new EventCenter.HomeSwitch(4));
            if (null != getActivity()) {
                getActivity().finish();
            }
            return true;
        }
        if (url.contains(APP_ASSETS)) {
            EventBus.getDefault().post(new EventCenter.HomeSwitch(2));
            if (null != getActivity()) {
                getActivity().finish();
            }
            return true;
        }

        return false;
    }

    private static final String APP_INDEX = "app-index";
    private static final String APP_MEMBER = "app-center";
    private static final String APP_ASSETS = "app-assets";
    private static final String APP_CF = "app-ChuangFu";

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * h5刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.H5Refresh h5Refresh) {
//        switchFragment(homeSwitch.getIndex());
        if (isProperty) {
            mWebView.reload();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.LoginNotice loginNotice) {
        initView(1);
    }

}
