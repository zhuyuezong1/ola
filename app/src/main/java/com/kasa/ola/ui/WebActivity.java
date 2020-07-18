package com.kasa.ola.ui;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.fragment.WebFragment;
import com.kasa.ola.utils.CommonUtils;



public class WebActivity extends BaseActivity {

    private WebFragment webFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = true;
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra(Const.INTENT_URL);
        String title = getIntent().getStringExtra(Const.WEB_TITLE);
        webFragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.INTENT_URL, url);
        bundle.putString(Const.WEB_TITLE, title);
        webFragment.setArguments(bundle);
        CommonUtils.addFragmentWithoutBackStack(getSupportFragmentManager(), webFragment, WebFragment.class.getName());
    }

    @Override
    public void onBackPressed() {
        if (!webFragment.clickBack()) {
            super.onBackPressed();
        }
    }
}
