package com.kasa.ola.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.fragment.WebFragment;
import com.kasa.ola.ui.fragment.WebViewFragment;
import com.kasa.ola.utils.CommonUtils;


public class X5WebActivity extends BaseActivity {

    private WebViewFragment webViewFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = true;
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(Const.INTENT_URL);
        String title = getIntent().getStringExtra(Const.WEB_TITLE);
        webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.INTENT_URL, url);
        bundle.putString(Const.WEB_TITLE, title);
        webViewFragment.setArguments(bundle);
        CommonUtils.addFragmentWithoutBackStack(getSupportFragmentManager(), webViewFragment, WebFragment.class.getName());
    }

    @Override
    public void onBackPressed() {
        if (!webViewFragment.clickBack()) {
            super.onBackPressed();
        }
    }
}
