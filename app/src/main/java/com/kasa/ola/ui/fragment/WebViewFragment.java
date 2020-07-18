package com.kasa.ola.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.entity.H5BackBean;
import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.StringUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.jsbridge.BridgeHandler;
import com.kasa.ola.widget.jsbridge.BridgeWebView;
import com.kasa.ola.widget.jsbridge.CallBackFunction;
import com.kasa.ola.widget.jsbridge.DefaultHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class WebViewFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    //    @BindView(R.id.iv_back_frag)
//    ImageView ivBackFrag;
//    @BindView(R.id.tv_title_frag)
//    TextView tvTitleFrag;
//    @BindView(R.id.tv_right_text_frag)
//    TextView tvRightTextFrag;
//    @BindView(R.id.webProgressBar)
//    ProgressBar webProgressBar;
//    @BindView(R.id.view_actionbar_frag)
//    LinearLayout viewActionbarFrag;
//    @BindView(R.id.refresh_layout)
//    SwipeRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.view_web)
    BridgeWebView viewWeb;
    @BindView(R.id.tv_title_frag)
    TextView tvTitleFrag;
    @BindView(R.id.tv_right_text_frag)
    TextView tvRightTextFrag;
    @BindView(R.id.webProgressBar)
    ProgressBar webProgressBar;
    @BindView(R.id.view_actionbar_frag)
    LinearLayout viewActionbarFrag;
    @BindView(R.id.iv_back_frag)
    ImageView ivBackFrag;

    private String url = "";
    private String title = "";
    private boolean isProperty = true;
    private String token;
    private int position;
    private int courseId;

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
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (StringUtils.isEmpty(title)) {
            viewActionbarFrag.setVisibility(View.GONE);
        } else {
            viewActionbarFrag.setVisibility(View.VISIBLE);
            tvTitleFrag.setText(title);
            tvRightTextFrag.setVisibility(View.GONE);
            ivBackFrag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!clickBack()) {
                        finish();
                    }
                }
            });
        }
//        initView();
        initTest();
        return rootView;
    }

    private void initTest() {
        viewWeb.setDefaultHandler(new DefaultHandler());
        viewWeb.setWebChromeClient(new WebChromeClient());
        viewWeb.loadUrl(url);
        viewWeb.registerHandler("on_native_click", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogUtil.d("WebFragment------------", data);
                H5BackBean h5BackBean = JsonUtils.jsonToObject(data, H5BackBean.class);

            }
        });
        viewWeb.send("hello");
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewStatusBar.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        viewStatusBar.setBackgroundColor(Color.parseColor("#000000"));
//        webProgressBar.setVisibility(View.VISIBLE);
//        ivBackFrag.setOnClickListener(this);
// 创建WebView对象添加到布局中
//        viewWeb = new BridgeWebView(getActivity());
//
//        IX5WebViewExtension ix5WebViewExtension = viewWeb.getX5WebViewExtension();
//        ix5WebViewExtension.setScrollBarFadingEnabled(false);
//
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewStatusBar.getLayoutParams();
//        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
//        viewStatusBar.setBackgroundColor(Color.parseColor("#000000"));
//        llFragmentWebviewRoot.addView(viewWeb);


        WebSettings webSettings = viewWeb.getSettings();
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
        viewWeb.requestFocus();
        viewWeb.setScrollBarStyle(View.SCROLL_AXIS_NONE);

        viewWeb.setDefaultHandler(new DefaultHandler());
        viewWeb.setWebChromeClient(new WebChromeClient());
        try {
            if (viewWeb.getX5WebViewExtension() != null) {
                Bundle data = new Bundle();
                data.putBoolean("standardFullScreen", false);
                data.putBoolean("supportLiteWnd", false);
                data.putInt("DefaultVideoScreen", 1);
                viewWeb.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewWeb.loadUrl(url);
        initEvent();
        viewWeb.callHandler("on_native_msg", "refresh", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                LogUtil.d("WebFragment------------", data);
                ToastUtils.showLongToast(getContext(), data);
            }
        });
        viewWeb.send("refresh");


    }

    private void initEvent() {
        viewWeb.registerHandler("on_native_click", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogUtil.d("WebFragment------------", data);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean clickBack() {
        if (null != viewWeb) {
            if (viewWeb.canGoBack()) {
                viewWeb.goBack();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private class BaseWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
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
//            refreshLayout.setRefreshing(false);

        }

    }

//    private class BaseWebChromeClient extends WebChromeClient {
//        @Override
//        public void onProgressChanged(WebView view, int newProgress) {
//            super.onProgressChanged(view, newProgress);
////            if (newProgress == 100) {
////                webProgressBar.setVisibility(View.INVISIBLE);//加载完网页进度条消失
////            } else {
////                webProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
////                webProgressBar.setProgress(newProgress);//设置进度值
////            }
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_frag:
//                callJsBack();
                break;
        }
    }

    /**
     * h5刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.H5Refresh h5Refresh) {
//        switchFragment(homeSwitch.getIndex());
        if (isProperty) {
            viewWeb.reload();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.LoginNotice loginNotice) {
        initView();
    }

    private void callJsBack() {
        viewWeb.callHandler("on_native_msg", "Android传递给js的数据", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                LogUtil.d("WebFragment", data);
                ToastUtils.showLongToast(getContext(), data);
            }
        });
    }

    private void registerInJs() {
        viewWeb.registerHandler("on_native_click", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                LogUtil.d("WebFragment", data);
                ToastUtils.showLongToast(getContext(), "js调用了Android");
                //返回给html
                function.onCallBack("Android回传给js的数据");
            }
        });
    }
//    WebChromeClient wvcc = new WebChromeClient() {
//        @Override
//        public void onReceivedTitle(com.tencent.smtt.sdk.WebView webView, String s) {
//            if (webView!=null&&tvTitleFrag!=null&&!TextUtils.isEmpty(title)&&title.equals("externalUrl")){
//                String webTitle = webView.getTitle();
//                tvTitleFrag.setText(webTitle);
//            }
//            super.onReceivedTitle(webView, s);
//        }
//    };
}
