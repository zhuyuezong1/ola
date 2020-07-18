package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;


/**
 * Created by guan on 2018/7/30 0030.
 */
public class ShopApplyDialog extends Dialog {

    public ShopApplyDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public ShopApplyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void close(ShopApplyDialog dialog);
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

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

//        public Builder setLeftButton(String leftButtonText) {
//            this.leftButtonText = leftButtonText;
//            return this;
//        }
//
//        public Builder setRightButton(String rightButtonText) {
//            this.rightButtonText = rightButtonText;
//            return this;
//        }

        public ShopApplyDialog create() {
            final ShopApplyDialog shopApplyDialog = new ShopApplyDialog(context);
//            shopApplyDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
            shopApplyDialog.setContentView(R.layout.view_shop_apply_dialog);
            TextView messageText = shopApplyDialog.findViewById(R.id.tv_explain_content);
            if (null != message && !TextUtils.isEmpty(message)) {
                messageText.setText(message);
            } else {
                messageText.setVisibility(View.GONE);
            }
            ImageView iv_close = shopApplyDialog.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInterface.close(shopApplyDialog);
                }
            });
//            commonDialog.setCanceledOnTouchOutside(false);
            return shopApplyDialog;
        }

    }
}
