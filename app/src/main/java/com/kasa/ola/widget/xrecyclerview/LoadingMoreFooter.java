package com.kasa.ola.widget.xrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kasa.ola.R;
import com.kasa.ola.utils.DisplayUtils;


/**
 * Created by guan on 2018/6/4 0004.
 */
public class LoadingMoreFooter extends LinearLayout {

    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;
    public final static int NO_FOOTER = 3;

    private ImageView iv_loading;
    private TextView tv_tips;

    public LoadingMoreFooter(Context context) {
        this(context, null);
    }

    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingMoreFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setPadding(0, 6, 0, 6);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iv_loading = new ImageView(getContext());
        iv_loading.setLayoutParams(new LayoutParams(DisplayUtils.dip2px(getContext(), 25), DisplayUtils.dip2px(getContext(), 8)));
        iv_loading.setImageResource(R.drawable.loading_animation);
        addView(iv_loading);
        tv_tips = new TextView(getContext());
        tv_tips.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv_tips.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv_tips.setText("正在加载更多数据");
        addView(tv_tips);
        setVisibility(View.GONE);
    }

    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                iv_loading.setVisibility(View.VISIBLE);
                startAnim();
                tv_tips.setText("正在加载更多数据");
                setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                stopAnim();
                iv_loading.setVisibility(View.GONE);
                tv_tips.setText("正在加载更多数据");
                setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                stopAnim();
                iv_loading.setVisibility(View.GONE);
                tv_tips.setText("已经全部加载完毕");
                setVisibility(View.VISIBLE);
                break;
            case NO_FOOTER:
                stopAnim();
                setPadding(0, 0, 0, 0);
                iv_loading.setVisibility(View.GONE);
                tv_tips.setVisibility(View.GONE);
                setVisibility(View.GONE);
                break;
        }
    }

    private void startAnim() {
        if (iv_loading == null) {
            return;
        }
        AnimationDrawable anim = (AnimationDrawable) iv_loading.getDrawable();
        if (anim != null && !anim.isRunning()) {
            anim.start();
        }
    }

    private void stopAnim() {
        if (iv_loading == null) {
            return;
        }
        AnimationDrawable anim = (AnimationDrawable) iv_loading.getDrawable();
        if (anim != null && anim.isRunning()) {
            anim.stop();
        }
    }

}
