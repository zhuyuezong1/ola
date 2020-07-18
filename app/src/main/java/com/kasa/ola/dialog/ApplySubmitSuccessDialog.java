package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;


public class ApplySubmitSuccessDialog extends Dialog {

    public ApplySubmitSuccessDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public ApplySubmitSuccessDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void goBack(ApplySubmitSuccessDialog dialog);
    }

    public static class Builder {

        private Context context;
        private String message;
        private String backText;
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

        public Builder setBackButton(String backText) {
            this.backText = backText;
            return this;
        }

        public ApplySubmitSuccessDialog create() {
            final ApplySubmitSuccessDialog commonDialog = new ApplySubmitSuccessDialog(context);
            commonDialog.setContentView(R.layout.view_apply_submit_success_dialog);
            TextView messageText = commonDialog.findViewById(R.id.dialog_message);
            TextView tv_back = commonDialog.findViewById(R.id.tv_back);
            if (null != message && !TextUtils.isEmpty(message)) {
                messageText.setText(message);
            } else {
                messageText.setVisibility(View.GONE);
            }

            if (null != backText && !TextUtils.isEmpty(backText)) {
                tv_back.setText(backText);
                tv_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.goBack(commonDialog);
                        }
                    }
                });
            } else {
                tv_back.setVisibility(View.GONE);
            }
//            commonDialog.setCanceledOnTouchOutside(false);
            return commonDialog;
        }

    }
}
