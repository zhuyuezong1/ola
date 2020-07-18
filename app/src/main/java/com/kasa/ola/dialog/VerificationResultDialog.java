package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;


public class VerificationResultDialog extends Dialog {

    public VerificationResultDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public VerificationResultDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void leftButtonClick(VerificationResultDialog dialog);

        void rightButtonClick(VerificationResultDialog dialog);
    }

    public static class Builder {

        private Context context;
        private String message;
        private String messageDesc;
        private int imageRes = 0;
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
        public Builder setMessageDesc(String messageDesc) {
            this.messageDesc = messageDesc;
            return this;
        }
        public Builder setStatusImage(int imageRes) {
            this.imageRes = imageRes;
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

        public VerificationResultDialog create() {
            final VerificationResultDialog commonDialog = new VerificationResultDialog(context);
            commonDialog.setContentView(R.layout.view_verification_result_dialog);
            ImageView iv_verification_status = commonDialog.findViewById(R.id.iv_verification_status);
            View view_blank = commonDialog.findViewById(R.id.view_blank);
            TextView messageText = commonDialog.findViewById(R.id.dialog_message);
            TextView dialog_message_desc = commonDialog.findViewById(R.id.dialog_message_desc);
            View verification_divider = commonDialog.findViewById(R.id.verification_divider);
            if (imageRes!=0){
                iv_verification_status.setImageResource(imageRes);
            }else {
                iv_verification_status.setVisibility(View.GONE);
                view_blank.setVisibility(View.GONE);
            }
            if (null != message && !TextUtils.isEmpty(message)) {
                messageText.setText(message);
            } else {
                messageText.setVisibility(View.GONE);
            }
            if (null != messageDesc && !TextUtils.isEmpty(messageDesc)) {
                dialog_message_desc.setText(messageDesc);
            } else {
                dialog_message_desc.setVisibility(View.GONE);
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
                verification_divider.setVisibility(View.GONE);
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
                verification_divider.setVisibility(View.GONE);
            }
            commonDialog.setCanceledOnTouchOutside(false);
            return commonDialog;
        }

    }
}
