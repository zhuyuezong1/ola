package com.kasa.ola.ui.passwordinputwin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kasa.ola.R;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.ModPasswordActivity;


public class PasswordPopupWin {
    private PopupWindow popupWindow;
    private Activity mContext;
    private EditText[] etPassword = new EditText[6];
    private int[] nPasswordNum = {0};
    private PasswordPopListener passwordPopListener;

    public PasswordPopupWin(Activity context) {
        mContext = context;
        initPopupWindow();
    }

    public interface PasswordPopListener {
        void onPasswordVerify(String pwd);
    }

    public void setPasswordPopListener(PasswordPopListener l) {
        passwordPopListener = l;
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        View popupView = mContext.getLayoutInflater().inflate(R.layout.layout_passwordkeyboard_win, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置点击空白处不消失
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));

        popupWindow.getContentView().setFocusableInTouchMode(true);
        popupWindow.getContentView().setFocusable(true);
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        });

        /***************密码显示框********************/
        int[] editText_id = {R.id.editText_0, R.id.editText_1, R.id.editText_2, R.id.editText_3,
                R.id.editText_4, R.id.editText_5};
        int i = 0;
        for (int id : editText_id) {
            etPassword[i] = popupView.findViewById(id);
            etPassword[i].setEnabled(false);
            i++;
        }

        /***************关闭********************/
        ImageButton imgb_close = popupView.findViewById(R.id.close);
        imgb_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        /***************忘记密码********************/
        TextView tv_forgetPayPassword = popupView.findViewById(R.id.keyboard_forget_paypassword);
        String strPassword = mContext.getString(R.string.forget_paypassword);
        SpannableStringBuilder builder = new SpannableStringBuilder(strPassword);

        int nEndLen = strPassword.length();

        //设置字体颜色
        builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.COLOR_FF01AAEF)), 0,
                nEndLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置点击监听
        builder.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(mContext, ModPasswordActivity.class);
                intent.putExtra(Const.PWD_TYPE_KEY, 2);
                mContext.startActivity(intent);
            }

            //去掉下划线，重新updateDrawState并且setUnderlineText(false)
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 0, nEndLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_forgetPayPassword.setText(builder);
        tv_forgetPayPassword.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        tv_forgetPayPassword.setMovementMethod(LinkMovementMethod.getInstance());  // 设置TextView为可点击状态

        /***************密码键盘输入********************/
        PasswordKeyboardView pkeyboardView = popupView.findViewById(R.id.passwordKeyboardView);
        pkeyboardView.setIOnKeyboardListener(new PasswordKeyboardView.IOnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                if (nPasswordNum[0] < 6) {
                    etPassword[nPasswordNum[0]].setText(text);
                    nPasswordNum[0]++;
                }

                String str = "";
                if (nPasswordNum[0] == 6) {
                    for (int i = 0; i < nPasswordNum[0]; i++) {
                        str += etPassword[i].getText().toString();
                    }
                    //此处为密码输到第六位的监听，替换成付款逻辑即可
                    if (null != passwordPopListener) {
                        passwordPopListener.onPasswordVerify(str);
                    }
                }
            }

            @Override
            public void onDeleteKeyEvent() {
                if (nPasswordNum[0] > 0) {
                    nPasswordNum[0]--;
                    etPassword[nPasswordNum[0]].setText("");
                }
            }
        });

    }

    public void clearPwd() {
        nPasswordNum[0] = 0;
        for (int i = 0; i < 6; i++) {
            etPassword[i].setText("");
        }
    }

    /**
     * 设置PopupWindow位于底部
     *
     * @param parent
     * @param view
     */
    public void showAtBottom(View parent, View view) {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mContext.getWindow().setAttributes(lp);

        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        popupWindow.showAsDropDown(view);
    }

    /**
     * dismiss PopupWindow
     */
    public void dismiss() {
        // 设置背景颜色回复
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = 1f;
        mContext.getWindow().setAttributes(lp);
        clearPwd();
        popupWindow.dismiss();
    }
}
