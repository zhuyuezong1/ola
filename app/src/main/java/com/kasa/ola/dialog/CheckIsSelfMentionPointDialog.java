package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;


public class CheckIsSelfMentionPointDialog extends Dialog {

    public CheckIsSelfMentionPointDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public CheckIsSelfMentionPointDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void leftButtonClick(String phone);

        void rightButtonClick(CheckIsSelfMentionPointDialog dialog);
    }

    public static class Builder {

        private Context context;
        private String message;
        private String phone;
        private String leftButtonText;
        private String rightButtonText;
        private DialogInterface dialogInterface;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDialogInterface(DialogInterface dialogInterface) {
            this.dialogInterface = dialogInterface;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }
        public Builder setPhoneNumber(String phone) {
            this.phone = phone;
            return this;
        }
        public Builder setLeftButton(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public Builder setRightButton(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        public CheckIsSelfMentionPointDialog create() {
            final CheckIsSelfMentionPointDialog commonDialog = new CheckIsSelfMentionPointDialog(context);
            commonDialog.setContentView(R.layout.view_check_is_self_mention_point_dialog);
            TextView messageText = commonDialog.findViewById(R.id.dialog_message);
            final TextView tv_phone = commonDialog.findViewById(R.id.tv_phone);
            if (null != message && !TextUtils.isEmpty(message)) {
                messageText.setText(message);
            } else {
                messageText.setVisibility(View.GONE);
            }
            if (null != phone && !TextUtils.isEmpty(phone)) {
                tv_phone.setText(phone);
            } else {
                tv_phone.setVisibility(View.GONE);
            }
            TextView leftBtn = commonDialog.findViewById(R.id.btn_left);
            if (null != leftButtonText && !TextUtils.isEmpty(leftButtonText)) {
                leftBtn.setText(leftButtonText);
                leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.leftButtonClick(tv_phone.getText().toString());
                        }
                    }
                });
            } else {
                leftBtn.setVisibility(View.GONE);
            }
            TextView rightBtn = commonDialog.findViewById(R.id.btn_right);
            if (null != rightButtonText && !TextUtils.isEmpty(rightButtonText)) {
                rightBtn.setText(rightButtonText);
                rightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.rightButtonClick(commonDialog);
                        }
                    }
                });
            } else {
                rightBtn.setVisibility(View.GONE);
            }
//            commonDialog.setCanceledOnTouchOutside(false);
            return commonDialog;
        }

    }
}
