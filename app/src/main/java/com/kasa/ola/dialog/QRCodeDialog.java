package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.WebActivity;
import com.kasa.ola.utils.ImageLoaderUtils;

public class QRCodeDialog extends Dialog {

    public QRCodeDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public QRCodeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    private QRCodeDialog.DialogInterface dialogInterface;
    public interface DialogInterface {
        void close(QRCodeDialog dialog);
    }

    public static class Builder {

        private Context context;
        private DialogInterface dialogInterface;
        private String contentText ="";
        private String codeImageUrl ="";
        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDialogInterface(DialogInterface dialogInterface) {
            this.dialogInterface = dialogInterface;
            return this;
        }
        public Builder setMessage(String contentText) {
            this.contentText = contentText;
            return this;
        }
        public Builder setCodeImageUrl(String codeImageUrl) {
            this.codeImageUrl = codeImageUrl;
            return this;
        }
        public QRCodeDialog create() {
            final QRCodeDialog qrCodeDialog = new QRCodeDialog(context);
            qrCodeDialog.setContentView(R.layout.view_qr_code_dialog);
            ImageView iv_qr_code = qrCodeDialog.findViewById(R.id.iv_qr_code);
            ImageView iv_close = qrCodeDialog.findViewById(R.id.iv_close);
            TextView tv_content = qrCodeDialog.findViewById(R.id.tv_content);
            if (null != contentText && !TextUtils.isEmpty(contentText)) {
                tv_content.setText(contentText);
            } else {
                tv_content.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(codeImageUrl)){
                ImageLoaderUtils.imageLoad(context,codeImageUrl,iv_qr_code);
            }else {

            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInterface.close(qrCodeDialog);
                }
            });
            return qrCodeDialog;
        }

    }
}
