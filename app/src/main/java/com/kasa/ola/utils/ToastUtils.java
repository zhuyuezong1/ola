package com.kasa.ola.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kasa.ola.R;


/**
 * Created by guan on 2018/6/2 0002.
 */
public class ToastUtils {

    private static Toast toast;
    private static View view;

    private static void initToast(Context context, CharSequence msg) {
        if (null == toast) {
            toast = new Toast(context);
        }else {
        }
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_toast_custom, null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
    }

    public static void showContinuationToast(Context context, CharSequence msg) {
        if (null == toast) {
            toast = new Toast(context);
        }else {
            toast.cancel();
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_toast_custom, null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        try {
            initToast(context, msg);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
        }
    }


    public static void showShortToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, CharSequence msg) {
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, int resId) {
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, CharSequence msg, int duration) {
        try {
            initToast(context, msg);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
        }
    }

    private static void showToast(Context context, int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            initToast(context, context.getString(resId));
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
        }
    }

//    public static void cancelToast() {
//        if (toast != null) {
//            toast.cancel();
//        }
//    }

}