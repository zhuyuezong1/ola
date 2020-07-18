package com.kasa.ola.utils;

import android.content.Context;
import android.content.Intent;

import com.kasa.ola.R;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.ModPasswordActivity;

public class PayUtils {
    public static boolean checkPayPwd(Context context) {
        if (LoginHandler.get().getMyInfo().optInt("hasPayPwd") == 0) {
            CommonDialog.Builder builder = new CommonDialog.Builder(context);
            builder.setMessage(context.getString(R.string.set_pwd_tips))
                    .setLeftButton(context.getString(R.string.cancel_pay))
                    .setRightButton(context.getString(R.string.confirm_pay))
                    .setDialogInterface(new CommonDialog.DialogInterface() {

                        @Override
                        public void leftButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void rightButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(context, ModPasswordActivity.class);
                            intent.putExtra(Const.PWD_TYPE_KEY, 2);
                            context.startActivity(intent);
                        }
                    })
                    .create()
                    .show();
            return false;
        }
        return true;
    }
}
