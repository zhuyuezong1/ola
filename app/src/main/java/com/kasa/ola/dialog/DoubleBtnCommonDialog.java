package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;


public class DoubleBtnCommonDialog extends Dialog {

    public DoubleBtnCommonDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public DoubleBtnCommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void leftButtonClick(DoubleBtnCommonDialog dialog);
        void rightButtonClick(DoubleBtnCommonDialog dialog);
    }

    public static class Builder {

        private Context context;
        private String message;
        private String title;
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

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
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

        public DoubleBtnCommonDialog create() {
            final DoubleBtnCommonDialog commonDialog = new DoubleBtnCommonDialog(context);
            commonDialog.setContentView(R.layout.view_double_btn_common_dialog);
            TextView titleText = commonDialog.findViewById(R.id.dialog_title);
            TextView messageText = commonDialog.findViewById(R.id.dialog_message);
            if (null != title && !TextUtils.isEmpty(title)) {
                titleText.setText(title);
            } else {
                titleText.setVisibility(View.GONE);
            }
            if (null != message && !TextUtils.isEmpty(message)) {
                messageText.setText(message);
            } else {
                messageText.setVisibility(View.GONE);
            }
            TextView leftBtn = commonDialog.findViewById(R.id.btn_left);
            if (null != leftButtonText && !TextUtils.isEmpty(leftButtonText)) {
                leftBtn.setText(leftButtonText);
                leftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.leftButtonClick(commonDialog);
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
