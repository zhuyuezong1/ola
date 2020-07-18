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
import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.AmountView;


public class ReviseNumDialog extends Dialog {

    public ReviseNumDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public ReviseNumDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public interface DialogInterface {
        void leftButtonClick(ReviseNumDialog dialog);

        void rightButtonClick(ReviseNumDialog dialog, int num);
    }

    public static class Builder {

        private Context context;
        private String message;
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

        public Builder setLeftButton(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public Builder setRightButton(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        public ReviseNumDialog create(){
            return create(1);
        }

        public ReviseNumDialog create(int num) {
            final ReviseNumDialog commonDialog = new ReviseNumDialog(context);
            commonDialog.setContentView(R.layout.view_revise_num_dialog);
            TextView btn_reduce = commonDialog.findViewById(R.id.btn_reduce);
            final EditText et_num = commonDialog.findViewById(R.id.et_num);
            et_num.setInputType(InputType.TYPE_CLASS_NUMBER);
            et_num.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s!=null && !TextUtils.isEmpty(s.toString())){
                        int num = Integer.parseInt(s.toString());
                        if (num>999){
                            ToastUtils.showLongToast(context,"最多只能选择" + Const.MAX_NUM + "件哟");
                            et_num.setText(Const.MAX_NUM+"");
                        }
                    }
                }
            });
            TextView btn_add = commonDialog.findViewById(R.id.btn_add);
            et_num.setText(num+"");
            btn_reduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int amount = Integer.parseInt(et_num.getText().toString());
                    if (amount>1){
                        amount--;
                        et_num.setText(amount+"");
                    }else {
                        ToastUtils.showLongToast(context, context.getString(R.string.no_product_prompt));
                    }
                }
            });
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int amount = Integer.parseInt(et_num.getText().toString());
                    if (amount< Const.MAX_NUM){
                        amount++;
                        et_num.setText(amount+"");
                    }else {
                        ToastUtils.showShortToast(context, "最多只能选择" + Const.MAX_NUM + "件哟");
                    }
                }
            });
//            final AmountView amount_view = commonDialog.findViewById(R.id.amount_view);
//            amount_view.setGoods_storage(999);
//            amount_view.setAmount(num);
////            amount_view.setFocusable(true);
//            amount_view.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
//                @Override
//                public void onAmountChange(View view, final int amount) {
//                    amount_view.setAmount(amount);
//                }
//            });
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
                            dialogInterface.rightButtonClick(commonDialog,Integer.parseInt(et_num.getText().toString()));
                        }
                    }
                });
            } else {
                rightBtn.setVisibility(View.GONE);
            }
            return commonDialog;
        }

    }
}
