package com.kasa.ola.net;

/**
 * Created by guan on 2018/6/5 0005.
 */
public interface RequestListener {

    /**
     * Only triggered on new requests.
     */
    void onNoConnection();

    /**
     * Could be triggered multiple times for guaranteed requests.
     */
    void onStartRequest();

    /**
     * Could be triggered multiple times for guaranteed requests.
     *
     * @param e
     *            the error object.
     */
    void onConnectionError(Object e);

    /**
     * Triggered only once or never.
     */
    void onFinish();

    /**
     * Triggered only once or never.
     */
    void onCancel();
}
