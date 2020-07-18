package com.kasa.ola.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.kasa.ola.R;

//开始播放声音
public class PlayVoice {
    private static MediaPlayer mediaPlayer;

    public static void playVoice(Context context){
        try {
            mediaPlayer= MediaPlayer.create(context, R.raw.test);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停止播放声音
    public  static void stopVoice(){
        if(null!=mediaPlayer) {
            mediaPlayer.stop();
        }
    }
}
