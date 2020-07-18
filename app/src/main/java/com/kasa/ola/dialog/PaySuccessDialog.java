package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;
import com.kasa.ola.utils.DisplayUtils;


/**
 * Created by guan on 2018/7/30 0030.
 */
public class PaySuccessDialog extends Dialog {

    public PaySuccessDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public PaySuccessDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    private DialogInterface dialogInterface;
    public interface DialogInterface {
        void walkAround(int resultType,PaySuccessDialog dialog);
    }


    public static class Builder {

        private Context context;
        private DialogInterface dialogInterface;
        private int resultType;
        private String rebates;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDialogInterface(DialogInterface dialogInterface) {
            this.dialogInterface = dialogInterface;
            return this;
        }
        public Builder setResult(int resultType,String rebates) {
            this.resultType = resultType;
            this.rebates = rebates;
            return this;
        }
        public PaySuccessDialog create() {
            final PaySuccessDialog commonDialog = new PaySuccessDialog(context);
            commonDialog.setContentView(R.layout.view_pay_success_dialog);
            ImageView iv_close = commonDialog.findViewById(R.id.iv_close);
            iv_close.setVisibility(View.GONE);
            TextView tv_year_card_name = commonDialog.findViewById(R.id.tv_year_card_name);
            TextView tv_continue_walk_around = commonDialog.findViewById(R.id.tv_continue_walk_around);
            if (resultType==1){
                if (!TextUtils.isEmpty(rebates)&&Double.parseDouble(rebates)!=0){
                    tv_year_card_name.setText(context.getString(R.string.pay_successful_text,rebates));
                }else {
                    tv_year_card_name.setText(context.getString(R.string.pay_succeed));
                }
                DisplayUtils.setViewDrawableTop(tv_year_card_name,R.mipmap.successfulpayment);
                tv_continue_walk_around.setText(context.getString(R.string.continue_walk_around));
            }else if (resultType==0){
                tv_year_card_name.setText(context.getString(R.string.pay_fail_tips));
                DisplayUtils.setViewDrawableTop(tv_year_card_name,R.mipmap.failuretopay);
                tv_continue_walk_around.setText(context.getString(R.string.pay_again));
            }
            tv_continue_walk_around.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogInterface!=null){
                        dialogInterface.walkAround(resultType,commonDialog);
                    }
                }
            });
            return commonDialog;
        }

    }
}
