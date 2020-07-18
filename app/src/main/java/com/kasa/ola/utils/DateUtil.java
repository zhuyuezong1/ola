package com.kasa.ola.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateUtil {

    public static final String ymdhms = "yyyy-MM-dd HH:mm:ss";
    public static final String ymd = "yyyy-MM-dd";

    public static String monthNumToMonthName(String month) {
        String m = month;
        if ("1".equals(month)) {
            m = "一月份";
        } else if ("2".equals(month)) {
            m = "二月份";
        } else if ("3".equals(month)) {
            m = "三月份";
        } else if ("4".equals(month)) {
            m = "四月份";
        } else if ("5".equals(month)) {
            m = "五月份";
        } else if ("6".equals(month)) {
            m = "六月份";
        } else if ("7".equals(month)) {
            m = "七月份";
        } else if ("8".equals(month)) {
            m = "八月份";
        } else if ("9".equals(month)) {
            m = "九月份";
        } else if ("10".equals(month)) {
            m = "十月份";
        } else if ("11".equals(month)) {
            m = "十一月份";
        } else if ("12".equals(month)) {
            m = "十二月份";
        }
        return m;
    }

    public static String getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month > 9 ? month : ("0" + month)) + "-" + day;
    }

    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month > 9 ? month : ("0" + month)) + "-" + day;
    }

    public static List<Integer> getDateForString(String date) {
        String[] dates = date.split("-");
        List<Integer> list = new ArrayList<>();
        list.add(Integer.parseInt(dates[0]));
        list.add(Integer.parseInt(dates[1]));
        list.add(Integer.parseInt(dates[2]));
        return list;
    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    public static String formatDate(String date, String format) {
        String resultD = date;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(date);
            resultD = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultD;
    }

    private static Date format2Date(String date, String format) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            d = sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String formatDate(long milliseconds, String format) {
        String resultD = "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = new Date(milliseconds);
            resultD = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultD;
    }

    public static long formatDateStr(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date1.getTime() / 1000;
    }

    public static String formatTimestamp(long timestamp) {
        String date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(timestamp * 1000));
        return date;
    }

    public static String formatTimeNow() {
        String date = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static String formatTimestampYear(long timestamp) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp * 1000));
        return date;
    }

    public static String formatTimeAll(long timestamp) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(timestamp * 1000));
        return date;
    }

    public static String formatTimesNormalYear(long timestamp) {
        String date = new SimpleDateFormat("yyyy.MM.dd").format(new Date(timestamp * 1000));
        return date;
    }

    public static String formatTimesNormalTime(long timestamp) {
        String date = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(timestamp * 1000));
        return date;
    }

    public static String formatTimestampChineseYear(long timestamp) {
        String date = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(timestamp * 1000));
        return date;
    }


    public static String getTimeDiffer(long startTime) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date((startTime + (24 * 60 * 60 * 30)) * 1000));
        return date;
    }

    /**
     * 通过年份和月份 得到当月的日子
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        Log.d("DateView", "DateView:First:" + calendar.getFirstDayOfWeek());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Log.d("DateView", "DateView:First:" + calendar.getFirstDayOfWeek());

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "周日";

            case 2:
                return "周一";

            case 3:
                return "周二";

            case 4:
                return "周三";

            case 5:
                return "周四";

            case 6:
                return "周五";

            case 7:
                return "周六";

            default:
                return "";

        }
    }

    /*
     * 将时分秒转为秒数
     * */
    public static long formatTurnSecond(String time) {
        String s = time;
        int index1 = s.indexOf(":");
        int index2 = s.indexOf(":", index1 + 1);
        int hh = Integer.parseInt(s.substring(0, index1));
        int mi = Integer.parseInt(s.substring(index1 + 1, index2));
        int ss = Integer.parseInt(s.substring(index2 + 1));

        return hh * 60 * 60 + mi * 60 + ss;
    }

    /*
     * 将毫秒数转为时分秒
     * */
    public static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + "分";
        if (second < 10) {
            time += "0";
        }
        time += second + "秒";
        return time;
    }
    /*
     * 将毫秒数转为时分秒xx:xx
     * */
    public static String timeParse1(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }
    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    public static int getGapCount(String startDate, String endDate, String format) {
        return getGapCount(format2Date(startDate, format), format2Date(endDate, format));
    }

    public static String getCurrentTime(){

        long currentTime = System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date(currentTime);

        String sim = formatter.format(date);
        return sim;
    }
    public static String getCurrentDate(){

        long currentTime = System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date(currentTime);

        String sim = formatter.format(date);
        return sim;
    }
    public static Calendar getCalendar(String time){//yyyy-mm-dd
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
