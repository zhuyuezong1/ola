package com.kasa.ola.pop;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;

import com.kasa.ola.R;

import razerdp.basepopup.BasePopupWindow;

public class CommentInputPopup extends BasePopupWindow {
    EditText mEditText;

    public CommentInputPopup(Context context, final OnSendClickListener onSendClickListener) {
        super(context);
        mEditText = findViewById(R.id.ed_input);
        findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSendClickListener!=null){
                    onSendClickListener.sendClick(mEditText.getText().toString());
                }
//                ToastUtils.showLongToast(getContext(), mEditText.getText().toString());
                dismiss();
            }
        });
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//              /*  m_etPhoneNumber.setCursorVisible(false);//失去光标
//                m_etSmsCode.setCursorVisible(false);*/
//        findViewById(R.id.ed_input).requestFocus();
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
//        }
        setAutoShowInputMethod(mEditText, true);
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
        return createPopupById(R.layout.comment_input_pop);
    }
    public interface OnSendClickListener {
        void sendClick(String text);
    }
}
