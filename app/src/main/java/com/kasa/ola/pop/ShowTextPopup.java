package com.kasa.ola.pop;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import com.kasa.ola.R;

import razerdp.basepopup.BasePopupWindow;


public class ShowTextPopup extends BasePopupWindow {
    private Activity context;
    private String title;
    private String content;
    private TextView tv_title;
    private TextView tv_content;

    public void setContent(String content) {
        this.content = content;
        if (!TextUtils.isEmpty(content)){
            tv_content.setText(content);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        if (!TextUtils.isEmpty(title)){
            tv_title.setText(title);
        }
    }

    public ShowTextPopup(Activity context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        ImageView iv_close = findViewById(R.id.iv_close);
        tv_content = findViewById(R.id.tv_content);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setPopupGravity(Gravity.BOTTOM);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0, 350);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0, 1f, 350);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_show_text);
    }
}
