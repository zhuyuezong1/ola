package com.kasa.ola.net;

import rx.Subscriber;

/**
 * BaseSubscriber
 * Created by Tamic on 2016-08-03.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private RequestListener requestListener;

    public BaseSubscriber() {
    }

    public void send(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    @Override
    public void onError(Throwable e) {
//        if (e instanceof ExceptionHandle.ResponeThrowable) {
//            onError((ExceptionHandle.ResponeThrowable) e);
//        } else {
//            onError(new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
//        }
        onError(ExceptionHandle.handleException(e));
        if (null != requestListener) {
            requestListener.onNoConnection();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (null != requestListener) {
            requestListener.onStartRequest();
        }
    }

    @Override
    public void onCompleted() {
        if (null != requestListener) {
            requestListener.onFinish();
        }
    }

    @Override
    public void onNext(T t) {
    }

    public abstract void onError(ExceptionHandle.ResponeThrowable e);

}
