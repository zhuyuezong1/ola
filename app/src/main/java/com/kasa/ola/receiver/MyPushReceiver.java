package com.kasa.ola.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.kasa.ola.R;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.TestActivity;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.PlayVoice;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyPushReceiver";
    private boolean isOpenVibrate = true;
    private boolean isOpenSound = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//                Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...
                LogUtil.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//                Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                LogUtil.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//                processCustomMessage(context, bundle);
                getNotification(context, bundle);
//                Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//                Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");
                //打开自定义的Activity
                LogUtil.d(TAG, "[MyReceiver] 用户点击打开了通知");
                Intent i = new Intent(context, TestActivity.class);
                i.putExtras(bundle);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                context.startActivity(i);
            } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                LogUtil.d(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
//                Logger.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
            } else {
                LogUtil.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
//                Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e){

        }
    }

    private void getNotification(Context context, Bundle bundle) throws Exception {
        defaultMediaPlayer(context);
//        PlayVoice.playVoice(context);
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
////        builder.statusBarDrawable = R.mipmap.icon;//设置推送的图标
//        if (isOpenVibrate && !isOpenSound) {//只有振动
//            builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
//        } else if (isOpenSound && !isOpenVibrate) {//只有声音
//            builder.notificationDefaults = Notification.DEFAULT_SOUND;
//        } else if (isOpenSound && isOpenVibrate) {//两个都有
//            builder.notificationDefaults = Notification.DEFAULT_ALL;
//        } else {//只有呼吸灯
//            builder.notificationDefaults = Notification.DEFAULT_LIGHTS;
//        }
//        JPushInterface.setDefaultPushNotificationBuilder(builder);
//        BasicPushNotificationBuilder builder=new BasicPushNotificationBuilder(context);
//        builder.statusBarDrawable = R.mipmap.icon_logo;//设置推送的图标
//        builder.notificationFlags=Notification.FLAG_INSISTENT;//设置为点击后自动消失
//        builder.notificationDefaults=Notification.DEFAULT_SOUND;//设置为铃声
//        JPushInterface.setDefaultPushNotificationBuilder(builder);

    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setAutoCancel(true)
                .setContentText("自定义推送声音")
                .setContentTitle("极光测试")
                .setSmallIcon(R.mipmap.icon_logo);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (!TextUtils.isEmpty(extras)) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (null != extraJson && extraJson.length() > 0) {
                    String sound = extraJson.getString("sound");
                    if("test.mp3".equals(sound)){
                        notification.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.test));
                    }
                }
            } catch (JSONException e) {

            }

        }
        Intent mIntent = new Intent(context, TestActivity.class);
        mIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
        notification.setContentIntent(pendingIntent);
        notificationManager.notify(2, notification.build());
    }
    public void defaultMediaPlayer(final Context context) throws Exception {
        CountDownTimer timer = new CountDownTimer(1000, 1000) {
            Vibrator vibrator;
            public void onTick(long millisUntilFinished) {
                try {
                    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {800, 150, 400, 130}; // OFF/ON/OFF/ON...
                    vibrator.vibrate(pattern, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFinish() {
                try {
                    if (null != vibrator) {
                        vibrator.cancel();
                        vibrator = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

    }

}
