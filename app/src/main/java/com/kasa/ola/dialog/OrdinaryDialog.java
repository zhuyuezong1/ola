package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;

public class OrdinaryDialog extends Dialog {

    public OrdinaryDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public OrdinaryDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    private OrdinaryDialog.DialogInterface dialogInterface;
    public interface DialogInterface {
        void disagree(OrdinaryDialog dialog);
        void agree(OrdinaryDialog dialog);
    }



    public static class Builder {

        private Context context;
        private DialogInterface dialogInterface;
        private String contentText ="";
        private String leftButtonText;
        private String rightButtonText;
        private String title;
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
        public Builder setLeftButton(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        public Builder setRightButton(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }
        public OrdinaryDialog create() {
            final OrdinaryDialog privateDialog = new OrdinaryDialog(context);
            privateDialog.setContentView(R.layout.view_ordinary_dialog);
            TextView tv_rule = privateDialog.findViewById(R.id.tv_rule);
            TextView tv_rule_title = privateDialog.findViewById(R.id.tv_rule_title);
            TextView btn_cancel = privateDialog.findViewById(R.id.btn_cancel);
            TextView btn_confirm = privateDialog.findViewById(R.id.btn_confirm);
            if (!TextUtils.isEmpty(title)){
                tv_rule_title.setVisibility(View.VISIBLE);
                tv_rule_title.setText(title);
            }else {
                tv_rule_title.setVisibility(View.GONE);
            }
            if (contentText.contains("</font>")){
                tv_rule.setText(Html.fromHtml(contentText));
            }else {
                tv_rule.setText(contentText);
            }
            if (null != leftButtonText && !TextUtils.isEmpty(leftButtonText)) {
                btn_cancel.setText(leftButtonText);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.disagree(privateDialog);
                        }
                    }
                });
            } else {
                btn_cancel.setVisibility(View.GONE);
            }
            if (null != rightButtonText && !TextUtils.isEmpty(rightButtonText)) {
                btn_confirm.setText(rightButtonText);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.agree(privateDialog);
                        }
                    }
                });
            } else {
                btn_confirm.setVisibility(View.GONE);
            }

            return privateDialog;
        }

    }
}
