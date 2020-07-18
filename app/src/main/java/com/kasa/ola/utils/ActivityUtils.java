package com.kasa.ola.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtils {

    public static ArrayList<Activity> arrayList = new ArrayList<Activity>();

    /**
     * 当进入一个新的activity的时候便把这个activity放入ArrayList中
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        if (!arrayList.contains(activity)) {
            arrayList.add(activity);
        }
    }

    /**
     * 结束arrayList中的所有的activity
     */
    public static void finishActivity() {
        Activity activity = null;
        int length = arrayList.size() - 1;
        for (int i = length; i >= 0; i--) {
            activity = arrayList.get(i);
            if (activity != null) {
                activity.finish();
                arrayList.remove(i);
            }
        }
    }

    /**
     * 结束arrayList中的所有的activity(除第一个外)
     */
    public static void finishOthersActivity() {
        Activity activity = null;
        int length = arrayList.size() - 1;
        for (int i = length; i > 0; i--) {
            activity = arrayList.get(i);
            if (activity != null) {
                activity.finish();
                arrayList.remove(i);
            }
        }
    }

    /**
     * 结束arrayList中的除给定的activity外的所有
     */
    public static void finishOthersActivity(Activity act) {
        Activity activity = null;
        int length = arrayList.size() - 1;
        for (int i = length; i > 0; i--) {
            activity = arrayList.get(i);
            if (activity != null && activity != act) {
                activity.finish();
                arrayList.remove(i);
            }
        }
    }

    /**
     * 结束arrayList中已有的指定activity
     *
     * @param activity
     */
    public static void finishOtherActivity(Activity activity) {
        Activity act = null;
        int length = arrayList.size() - 1;
        for (int i = length; i >= 0; i--) {
            act = arrayList.get(i);
            if (act != null && act == activity) {
                act.finish();
                arrayList.remove(i);
            }
        }
    }

    public static void finishActivity(Class<?> c) {
        Activity act = null;
        int length = arrayList.size() - 1;
        for (int i = length; i >= 0; i--) {
            act = arrayList.get(i);
            if (act != null && act.getClass() == c) {
                if (!act.isFinishing()) {
                    act.finish();
                    arrayList.remove(i);
                }
            }
        }
    }

    public static void finishOtherSameActivity(Activity activity) {
        Activity act = null;
        int length = arrayList.size() - 1;
        for (int i = length; i >= 0; i--) {
            act = arrayList.get(i);
            if (act != null && act.getClass() == activity.getClass() && act != activity) {
                if (!act.isFinishing()) {
                    act.finish();
                    arrayList.remove(i);
                }
            }
        }
    }

    //销毁c以外所有的activity
    public static void finishOtherActivity(Class<?> c) {
        Activity act = null;
        int length = arrayList.size() - 1;
        for (int i = length; i >= 0; i--) {
            act = arrayList.get(i);
            if (act != null && act.getClass() != c) {
                if (!act.isFinishing()) {
                    act.finish();
                    arrayList.remove(i);
                }
            }
        }
    }
    //销毁c之上的所有的activity
    public static Activity finishTopActivity(Class<?> c) {
        Activity act = null;
        int length = arrayList.size() - 1;
        for (int i = length; i >= 0; i--) {
            act = arrayList.get(i);
            if (act != null && act.getClass() != c) {
                if (!act.isFinishing()) {
                    act.finish();
                    arrayList.remove(i);
                }
            }else {
                return act;
            }
        }
        return null;
    }
    //销毁c之上的所有的activity
    public static boolean isHaveActivity(Class<?> c) {
        Activity act = null;
        int length = arrayList.size() - 1;
        for (int i = length; i >= 0; i--) {
            act = arrayList.get(i);
            if (act != null && act.getClass() != c) {
               return false;
            }else {
                return true;
            }
        }
        return true;
    }
    public static Activity getTopActivity() {
        if (arrayList != null && arrayList.size() > 0) {
            return arrayList.get(arrayList.size() - 1);
        }
        return null;
    }

    public static boolean isActivityAtTop(Class<?> c) {
        if (arrayList != null && arrayList.size() > 0) {
            Activity act = arrayList.get(arrayList.size() - 1);
            return act.getClass() == c;
        }
        return false;
    }

    public static Activity getActivity(Class<?> c) {
        for (Activity act : arrayList) {
            if (act.getClass() == c && !act.isFinishing()) {
                return act;
            }
        }
        return null;
    }

    public static boolean isAppInFront(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        final String packageName = context.getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}
