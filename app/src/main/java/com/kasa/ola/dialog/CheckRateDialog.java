package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;



public class CheckRateDialog extends Dialog {

    public CheckRateDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public CheckRateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void close(CheckRateDialog dialog);
    }

    public static class Builder {

        private Context context;
        private String message;
//        private String leftButtonText;
//        private String rightButtonText;
        private DialogInterface dialogInterface;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDialogInterface(DialogInterface dialogInterface) {
            this.dialogInterface = dialogInterface;
            return this;
        }

//        public Builder setMessage(String message) {
//            this.message = message;
//            return this;
//        }

//        public Builder setLeftButton(String leftButtonText) {
//            this.leftButtonText = leftButtonText;
//            return this;
//        }
//
//        public Builder setRightButton(String rightButtonText) {
//            this.rightButtonText = rightButtonText;
//            return this;
//        }

        public CheckRateDialog create() {
            final CheckRateDialog checkRateDialog = new CheckRateDialog(context);
            checkRateDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
            checkRateDialog.setContentView(R.layout.view_check_rate_dialog);
//            if (null != message && !TextUtils.isEmpty(message)) {
//                messageText.setText(message);
//            } else {
//                messageText.setVisibility(View.GONE);
//            }
            ImageView iv_close = checkRateDialog.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInterface.close(checkRateDialog);
                }
            });
            return checkRateDialog;
        }

    }
}
