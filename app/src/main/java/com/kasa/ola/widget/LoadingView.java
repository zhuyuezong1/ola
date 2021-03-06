package com.kasa.ola.widget;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kasa.ola.R;
import com.kasa.ola.net.RequestListener;


/**
 * 作者：Picasso on 2016/7/20 11:05
 * 详情：自定义loadingView
 */
public class LoadingView extends RelativeLayout implements View.OnClickListener, RequestListener {
    public static final int LOADING = 0;
    public static final int STOP_LOADING = 1;
    public static final int NO_DATA = 2;
    public static final int NO_NETWORK = 3;
    public static final int GONE = 4;
    public static final int LOADING_DIALOG = 5;

    private ImageView imageView;
    private LinearLayout mRlError;
    private LinearLayout mLlLoading;
    private AnimationDrawable mAni;
    private View mView;
    private TextView mRefresh;

    private OnRefreshListener mListener;

    public void setRefrechListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    public interface OnRefreshListener {

        void refresh();
    }

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.view_loading_layout, this);
        imageView = mView.findViewById(R.id.iv_loading);
        mLlLoading = mView.findViewById(R.id.ll_loading);
        mRlError = mView.findViewById(R.id.rl_error);
        mAni = (AnimationDrawable) imageView.getBackground();

        mRefresh = mView.findViewById(R.id.btn_refresh);
        mRefresh.setOnClickListener(this);

        mRlError.setVisibility(View.GONE);
        mLlLoading.setVisibility(View.GONE);
    }

    public void setStatue(int status) {
        try {
            if (status == LOADING) {//更新
                mRlError.setVisibility(View.GONE);
                mLlLoading.setVisibility(View.VISIBLE);
                mAni.start();
            } else if (status == STOP_LOADING) {
                mAni.stop();
                setVisibility(View.GONE);
            } else if (status == NO_DATA) {//无数据情况
                mAni.stop();
                mRlError.setVisibility(View.VISIBLE);
                mLlLoading.setVisibility(View.GONE);
            } else if (status == NO_NETWORK) {//无网络情况
                mAni.stop();
                mRlError.setVisibility(View.VISIBLE);
                mLlLoading.setVisibility(View.GONE);
            } else {
                mAni.stop();
                mRlError.setVisibility(View.GONE);
                mLlLoading.setVisibility(View.GONE);
            }
        } catch (OutOfMemoryError e) {
        }
    }

    @Override
    public void onClick(View v) {
        if (null != mListener) {
            mListener.refresh();
            setStatue(LOADING);
        }
    }


    @Override
    public void onNoConnection() {
        setStatue(NO_NETWORK);
    }

    @Override
    public void onStartRequest() {
        setStatue(LOADING);
    }

    @Override
    public void onConnectionError(Object e) {

    }

    @Override
    public void onFinish() {
        setStatue(GONE);
    }

    @Override
    public void onCancel() {

    }
}
