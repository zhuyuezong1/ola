package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;


public class NoticeDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ImageView ivNotice;
    private WebView noticeWebview;
    private TextView btnNoShow, btnConfirm;

    public NoticeDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.notice_layout);
        ivNotice = findViewById(R.id.iv_notice);
        noticeWebview = findViewById(R.id.notice_webview);
        btnNoShow = findViewById(R.id.btn_no_show);
        btnConfirm = findViewById(R.id.btn_confirm);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ivNotice.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidth(App.getApp()) - DisplayUtils.dip2px(context, 30) * 2;
        lp.height = (lp.width) * 120 /540;
//        ImageLoaderUtils.imageLoadTopRound(context, LoginHandler.get().getBaseUrlBean().getNoticeImg(), ivNotice, 12);

        WebSettings webSettings = noticeWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setBlockNetworkImage(false); // 解决图片不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        noticeWebview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        noticeWebview.loadUrl(LoginHandler.get().getBaseUrlBean().getNoticeUrl());

        btnNoShow.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no_show:
                LoginHandler.get().saveShownNoticeVersion();
                dismiss();
                break;
            case R.id.btn_confirm:
                dismiss();
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        setCanceledOnTouchOutside(true);
    }
}
