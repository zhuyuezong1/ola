package com.kasa.ola.widget.xrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kasa.ola.R;


/**
 * Created by guan on 2018/6/4 0004.
 */
public class RefreshHeader extends LinearLayout implements BaseRefreshHeader {

    private LinearLayout view_container;
    private ImageView iv_loading;
    private TextView tv_tips;
    public int mMeasuredHeight;
    private int mState = STATE_NORMAL;

    public RefreshHeader(Context context) {
        this(context, null);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        view_container = new LinearLayout(getContext());
        view_container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        view_container.setOrientation(LinearLayout.HORIZONTAL);
        view_container.setGravity(Gravity.CENTER);
        iv_loading = new ImageView(getContext());
        iv_loading.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        iv_loading.setPadding(0, 0, 10, 0);
        iv_loading.setImageResource(R.drawable.loading_animation);
        view_container.addView(iv_loading);
        tv_tips = new TextView(getContext());
        tv_tips.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        tv_tips.setPadding(10, 0 , 0 , 0);
        view_container.addView(tv_tips);
        addView(view_container);
        mMeasuredHeight = getMeasuredHeight();
    }

    @Override
    public void onMove(float delta) {
        if(getVisibilityHeight() > 0 || delta > 0) {
            setVisibilityHeight((int) delta + getVisibilityHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                if (getVisibilityHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                }else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibilityHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getVisibilityHeight() > mMeasuredHeight &&  mState < STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <=  mMeasuredHeight) {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    @Override
    public void refreshComplate() {
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                reset();
            }
        }, 200);
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        if (mState == state) {
            return;
        }

        if (state == STATE_NORMAL) {
            stopAnim();
            tv_tips.setText("下拉刷新");
        } else if (state == STATE_RELEASE_TO_REFRESH) {
            stopAnim();
            tv_tips.setText("松开刷新");
        } else if (state == STATE_REFRESHING) {
            startAnim();
            tv_tips.setText("正在加载中...");
        } else if (state == STATE_DONE) {
            stopAnim();
            tv_tips.setText("加载完成");
        }
        mState = state;
    }

    public void reset() {
        smoothScrollTo(0);
        setState(STATE_NORMAL);
    }

    private void smoothScrollTo(int destHeight) {
        if (getVisibilityHeight() != destHeight) {
            ValueAnimator animator = ValueAnimator.ofInt(getVisibilityHeight(), destHeight);
            animator.setDuration(300).start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setVisibilityHeight((int) animation.getAnimatedValue());
                }
            });
            animator.start();
        }
    }

    public void setVisibilityHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) view_container
                .getLayoutParams();
        lp.height = height;
        view_container.setLayoutParams(lp);
    }

    public int getVisibilityHeight() {
        LayoutParams lp = (LayoutParams) view_container.getLayoutParams();
        return lp.height;
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
