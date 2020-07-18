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


public class ActivityGildRuleDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tv_rule, btnConfirm;

    public ActivityGildRuleDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
        this.context = context;
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_gold_rule_layout);
        tv_rule = findViewById(R.id.tv_rule);
        btnConfirm = findViewById(R.id.btn_confirm);
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
