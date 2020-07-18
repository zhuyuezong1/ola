package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;
import com.kasa.ola.utils.DisplayUtils;

public class PermissionDialog extends Dialog {

    public PermissionDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public PermissionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    private PermissionDialog.DialogInterface dialogInterface;
    public interface DialogInterface {
        void close(PermissionDialog dialog);
        void open(PermissionDialog dialog);
    }



    public static class Builder {

        private Context context;
        private DialogInterface dialogInterface;
        private String contentText ="";
        private String title;
        private String clickText;
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
        public Builder setClickText(String clickText) {
            this.clickText = clickText;
            return this;
        }
        public Builder setMessage(String contentText) {
            this.contentText = contentText;
            return this;
        }
        public PermissionDialog create() {
            final PermissionDialog commonDialog = new PermissionDialog(context);
            commonDialog.setContentView(R.layout.view_permission_dialog);
            TextView tv_open_right_now = commonDialog.findViewById(R.id.tv_open_right_now);
            TextView tv_title = commonDialog.findViewById(R.id.tv_title);
            TextView tv_click_area = commonDialog.findViewById(R.id.tv_click_area);
            TextView tv_content = commonDialog.findViewById(R.id.tv_content);
            if (!TextUtils.isEmpty(title)){
                tv_title.setText(title);
            }else {
                tv_title.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(clickText)){
                tv_click_area.setText(Html.fromHtml(clickText));
            }else {
                tv_click_area.setVisibility(View.GONE);;
            }
            if (!TextUtils.isEmpty(contentText)){
                tv_content.setText(Html.fromHtml(contentText));
            }else {
                tv_content.setVisibility(View.GONE);
            }
//            iv_close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (dialogInterface!=null){
//                        dialogInterface.close(commonDialog);
//                    }
//                }
//            });
            tv_open_right_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogInterface!=null){
                        dialogInterface.open(commonDialog);
                    }
                }
            });
            return commonDialog;
        }

    }
}
