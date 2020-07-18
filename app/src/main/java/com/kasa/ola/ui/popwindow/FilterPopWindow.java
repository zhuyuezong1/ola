package com.kasa.ola.ui.popwindow;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kasa.ola.R;
import com.kasa.ola.utils.DateUtil;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.AutoLineLayout;
import com.kasa.ola.widget.datepicker.DatePickerPopWin;


/**
 * Created by guan on 2018/7/21 0021.
 */
public class FilterPopWindow extends PopupWindow implements View.OnClickListener {

    private Activity activity;
    private String[] dateList;
    private View rootView;
    private AutoLineLayout dateGroup;
    private TextView tempText, startTimeText, endTimeText;
    private int currentPos = 0;
    private FilterListener filterListener;
    private long startTime;
    private long endTime;

    public interface FilterListener {
        void confirmClick(long startTime, long endTime);

        void resetClick();
    }

    public void setFilterListener(FilterListener l) {
        filterListener = l;
    }

    public FilterPopWindow(Activity activity, long startTime, long endTime) {
        this.activity = activity;
        this.startTime = startTime;
        this.endTime = endTime;

        dateList = new String[]{"全部", "昨日", "今日", "本周"};

        rootView = LayoutInflater.from(activity).inflate(R.layout.view_filter_pop, null);
        setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.COLOR_80000000)));
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.filter_pop_anim);

        setContentView(rootView);

        startTimeText = rootView.findViewById(R.id.tv_start_time);
        endTimeText = rootView.findViewById(R.id.tv_end_time);
        if (startTime != 0 && endTime != 0) {
            startTimeText.setText(DateUtil.formatTimestampYear(startTime));
            endTimeText.setText(DateUtil.formatTimestampYear(endTime));
        }
        startTimeText.setOnClickListener(this);
        endTimeText.setOnClickListener(this);
        rootView.findViewById(R.id.btn_reset).setOnClickListener(this);
        rootView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        initDateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                if (null != filterListener) {
                    filterListener.resetClick();
                    dismiss();
                }
                break;
            case R.id.btn_confirm:
                String startTimeSrt = startTimeText.getText().toString();
                String endTimeStr = endTimeText.getText().toString();
                if ((!TextUtils.isEmpty(startTimeSrt) && TextUtils.isEmpty(endTimeStr)) ||
                        (TextUtils.isEmpty(startTimeSrt) && !TextUtils.isEmpty(endTimeStr))) {
                    ToastUtils.showShortToast(activity, activity.getString(R.string.write_time_warning));
                    return;
                }
                if (null != filterListener) {
                    filterListener.confirmClick(startTime, endTime);
                    dismiss();
                }
                break;
            case R.id.tv_start_time:
                showDateDialog(startTimeText, 0);
                break;
            case R.id.tv_end_time:
                showDateDialog(endTimeText, 1);
                break;
        }
    }

    private void initDateView() {
        dateGroup = rootView.findViewById(R.id.view_date_group);
        for (int i = 0; i < dateList.length; i++) {
            final TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.textview_filter, dateGroup, false);
            tv.setText(dateList[i]);
            if (i == 0) {
                tv.setSelected(true);
                tv.setTextColor(activity.getResources().getColor(R.color.COLOR_801E90FF));
                tempText = tv;
                currentPos = i;
            }
            final int pos = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentPos != pos) {
                        tv.setSelected(true);
                        tempText.setSelected(false);
                        tv.setTextColor(activity.getResources().getColor(R.color.COLOR_801E90FF));
                        tempText.setTextColor(activity.getResources().getColor(R.color.COLOR_FF1A1A1A));
                        tempText = tv;
                        currentPos = pos;
                    }
                }
            });
            dateGroup.addView(tv);
        }
    }

    private void showDateDialog(final TextView tv, final int pos) {
        final String currentDay = TextUtils.isEmpty(tv.getText()) ? DateUtil.getToday() : tv.getText().toString();
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(activity, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                if (DateUtil.formatDateStr(dateDesc, DateUtil.ymd) > DateUtil.formatDateStr(DateUtil.getToday(), DateUtil.ymd)) {
                    ToastUtils.showShortToast(activity, "不能超过当前日期");
                    return;
                }
                if (pos == 0) {
                    startTime = DateUtil.formatDateStr(dateDesc, DateUtil.ymd);
                    if (endTime != 0) {
                        if (startTime < endTime) {
                            tv.setText(dateDesc);
                        } else {
                            ToastUtils.showShortToast(activity, "请选择正确的时间段");
                        }
                    } else {
                        tv.setText(dateDesc);
                    }
                } else if (pos == 1) {
                    endTime = DateUtil.formatDateStr(dateDesc, DateUtil.ymd);
                    if (endTime > startTime) {
                        tv.setText(dateDesc);
                    } else {
                        ToastUtils.showShortToast(activity, "请选择正确的时间段");
                    }
                }
            }
        }).minYear(1990) //min year in loop
                .maxYear(2250) // max year in loop
                .dateChose(currentDay) // date chose when init popwindow
                .build();
        pickerPopWin.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

}
