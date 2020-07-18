package com.kasa.ola.widget.xrecyclerview;

/**
 * Created by guan on 2018/6/4 0004.
 */
public interface BaseRefreshHeader {

    public final static int STATE_NORMAL = 0;
    public final static int STATE_RELEASE_TO_REFRESH = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_DONE = 3;

    public void onMove(float delta) ;
    public boolean releaseAction();
    public void refreshComplate();

}
