package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;


/**
 * Created by guan on 2018/7/30 0030.
 */
public class MemberExplainDialog extends Dialog {

    public MemberExplainDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public MemberExplainDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    private DialogInterface dialogInterface;
    public interface DialogInterface {
        void close(MemberExplainDialog dialog);
    }


    public static class Builder {

        private Context context;
        private String message;
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

        public MemberExplainDialog create() {
            final MemberExplainDialog commonDialog = new MemberExplainDialog(context);
            commonDialog.setContentView(R.layout.view_member_explain_dialog);
            ImageView iv_close = commonDialog.findViewById(R.id.iv_close);
            TextView tv_content = commonDialog.findViewById(R.id.tv_content);
            if (!TextUtils.isEmpty(message)){
                tv_content.setText(message);
            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInterface.close(commonDialog);
                }
            });
            return commonDialog;
        }

    }
}
