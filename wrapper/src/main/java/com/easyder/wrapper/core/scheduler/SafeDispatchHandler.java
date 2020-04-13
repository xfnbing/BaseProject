package com.easyder.wrapper.core.scheduler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.easyder.wrapper.utils.LogUtils;


/**
 * a safe Handler avoid crash
 */
public class SafeDispatchHandler extends Handler {

    private static final String TAG = "SafeDispatchHandler ->";

    public SafeDispatchHandler(Looper looper) {
        super(looper);
    }

    public SafeDispatchHandler(Looper looper, Callback callback) {
        super(looper, callback);
    }

    public SafeDispatchHandler() {
        super();
    }

    public SafeDispatchHandler(Callback callback) {
        super(callback);
    }

    @Override
    public void dispatchMessage(Message msg) {
        try {
            super.dispatchMessage(msg);
        } catch (Exception e) {
            LogUtils.error(e);
        } catch (Error error) {
            LogUtils.error(error);
        }

    }

}
