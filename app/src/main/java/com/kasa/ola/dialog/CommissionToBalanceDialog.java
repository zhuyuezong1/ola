package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;


public class CommissionToBalanceDialog extends Dialog {

    public CommissionToBalanceDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public CommissionToBalanceDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void leftButtonClick(CommissionToBalanceDialog dialog);

        void rightButtonClick(CommissionToBalanceDialog dialog, double num);
    }

    public static class Builder {

        private Context context;
        private String message;
        private String limit;
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

        public Builder setLimit(String limit) {
            this.limit = limit;
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

        public CommissionToBalanceDialog create() {
            return create(1);
        }

        public CommissionToBalanceDialog create(int num) {
            final CommissionToBalanceDialog commonDialog = new CommissionToBalanceDialog(context);
            commonDialog.setContentView(R.layout.view_commission_to_balance_dialog);
            TextView thaw_quota = commonDialog.findViewById(R.id.thaw_quota);
            thaw_quota.setText(context.getString(R.string.thaw_limit_now, limit));
            TextView leftBtn = commonDialog.findViewById(R.id.btn_left);
            final EditText etThaw = commonDialog.findViewById(R.id.et_thaw);
//            etThaw.setInputType(InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
            etThaw.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(etThaw.getText())) {
                        if (Double.parseDouble(etThaw.getText().toString()) > Double.parseDouble(limit)) {
                            etThaw.setText(String.format("%d", (long) Double.parseDouble(limit)));
                            etThaw.setSelection(etThaw.getText().length());
                        }
                    }
                }
            });
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
                            dialogInterface.rightButtonClick(commonDialog, Double.parseDouble(TextUtils.isEmpty(etThaw.getText().toString().trim()) ? "0" : etThaw.getText().toString().trim()));
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
