package com.kasa.ola.pop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kasa.ola.R;
import com.kasa.ola.ui.listener.OnConfirmListener;
import com.kasa.ola.utils.DisplayUtils;

import razerdp.basepopup.BasePopupWindow;

public class SlideFromTopCommissionToBalancePop extends BasePopupWindow {
    EditText etContent;
    public SlideFromTopCommissionToBalancePop(Activity context, final OnConfirmListener onConfirmListener) {
        super(context);
        View viewShadow = findViewById(R.id.view_shadow);
        Button btnConfirm = findViewById(R.id.btn_confirm);
        etContent = findViewById(R.id.tv_content);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(context);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmListener!=null){
                    onConfirmListener.confirm(etContent.getText().toString(),"");
                }
            }
        });
        setAutoShowInputMethod(etContent, true);
        setPopupGravity(Gravity.TOP);
    }
    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(-1f,0 , 350);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0, -1f, 350);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_slide_from_top);
    }
}
