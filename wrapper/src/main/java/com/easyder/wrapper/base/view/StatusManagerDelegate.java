package com.easyder.wrapper.base.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.easyder.wrapper.R;
import com.easyder.wrapper.core.network.ResponseInfo;
import com.easyder.wrapper.status.DefaultStatus;
import com.easyder.wrapper.status.OnDefaultStatusListener;
import com.easyder.wrapper.status.OnStatusListener;
import com.easyder.wrapper.status.StatusManager;
import com.easyder.wrapper.status.StatusProvider;

/**
 * Auther:  winds
 * Data:    2018/3/12
 * Version: 1.0
 * Desc:
 */


public abstract class StatusManagerDelegate {

    protected StatusManager statusManager;

    public StatusManagerDelegate(View view) {
        if (view != null) {
            init(view);
        }
    }

    /**
     * 初始化StatusManager
     *
     * @param view
     */
    public void init(View view) {
        statusManager = new StatusManager(view);
    }

    /**
     * 展示StatusManager加载页面
     */
    public void initView() {
        showStatus(DefaultStatus.STATUS_LOADING);
    }


    /**
     * 展示对应的状态布局
     *
     * @param status
     */
    public void showStatus(String status) {
        if (statusManager != null) {
            statusManager.show(status);
        }
    }

    public void showLoadingStatus() {
        showStatus(DefaultStatus.STATUS_LOADING);
    }

    public void showNetErrorStatus() {
        showStatus(DefaultStatus.STATUS_NO_NETWORK);
    }

    public void showEmptyStatus() {
        showStatus(DefaultStatus.STATUS_EMPTY);
    }

    /**
     * 展示对应StatusProvider的状态布局
     *
     * @param provider
     * @param listener
     */
    public void showStatus(StatusProvider provider, OnStatusListener listener) {
        if (statusManager != null) {
            statusManager.show(provider, listener);
        }
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    /**
     * 移除当前的状态布局
     */
    public void removeStatus() {
        removeStatus(null);
    }

    /**
     * 移除对应的状态布局
     *
     * @param status
     */
    public void removeStatus(String status) {
        if (statusManager != null) {
            if (TextUtils.isEmpty(status)) {
                statusManager.removeStatus();
            } else {
                statusManager.removeStatus(status);
            }
        }
    }

    /**
     * 正确做法是  先判断当前的状态是否时没有网络，再判断当前页面网络请求数量  若小于等于0时 调用此方法
     *
     * @param responseInfo
     */
    public void onErrorStatus(ResponseInfo responseInfo) {
        processErrorStatus(responseInfo);
    }

    /**
     * 提供指定连接默认的无网络的处理方法，在非无网络时  执行默认的处理方法
     * 此方法需要考虑当前连接是否需要执行此方式
     *
     * @param responseInfo
     */
    public void processErrorStatus(ResponseInfo responseInfo) {
        switch (responseInfo.getState()) {
            //网络问题
            case ResponseInfo.NO_INTERNET_ERROR:
            case ResponseInfo.TIME_OUT:
                processNetError(responseInfo);
                break;
            case ResponseInfo.FAILURE: //连接服务器失败
                processFailureError(responseInfo);
                break;
            default:
                processStatusOnError(responseInfo);
                break;
        }
    }

    /**
     * 提供默認的處理statusManager處理方法，按界面需求重寫
     */
    public void processStatusOnSuccess() {
        if (getRequestCount() <= 0) {
            removeStatus();
        }
    }

    /**
     * 移除当前的状态页面
     * 先判断当前的状态是否时没有网络的状态，此种情况下不做处理
     * 再判断当前页面网络请求数目  当小于等于0时，再移除掉状态页面
     */
    public void processStatusOnError(ResponseInfo responseInfo) {
        if (statusManager == null || statusManager.getCurrentStatus().equals(DefaultStatus.STATUS_NO_NETWORK)
                || statusManager.getCurrentStatus().equals(DefaultStatus.STATUS_LOAD_ERROR)) {
            return;
        }
        processStatusOnSuccess();
    }

    /**
     * 访问请求失败时回调
     */
    public void processFailureError(ResponseInfo responseInfo) {
        if (statusManager != null && statusManager.getCurrentStatus().equals(DefaultStatus.STATUS_LOADING)) {
            processFailureErrorStatusDefault();
        } else {
            //此时表示非从loadData请求的其他接口 默认不处理，如需处理 请实现此方法

        }
    }

    /**
     * 提供指定连接默认的无网络的处理方法，在非无网络时  执行默认的处理方法
     * 此方法需要考虑当前连接是否需要执行此方式
     *
     * @param responseInfo
     */
    public void processNetError(ResponseInfo responseInfo) {
        if (statusManager != null && statusManager.getCurrentStatus().equals(DefaultStatus.STATUS_LOADING)) {
            processNetErrorStatusDefault();
        } else {
            //此时表示非从loadData请求的其他接口
            processNetErrorStatus(responseInfo);
        }
    }

    /**
     * 非load方法的错误处理
     * 默认不提供实现
     */
    public void processNetErrorStatus(ResponseInfo responseInfo) {

    }


    /**
     * 此处理方法仅供参考  如使用 请覆盖重写此方法
     */
    public void processFailureErrorStatusDefault() {
        /**
         * 从loadData进入的方法
         * @see #loadData(Bundle)
         */
        statusManager.show(DefaultStatus.STATUS_LOAD_ERROR, new OnDefaultStatusListener() {
            @Override
            public void onStatusViewCreate(String status, View statusView) {
                statusView.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        statusManager.show(DefaultStatus.STATUS_LOADING);
                        load();
                    }
                });
            }
        });
    }

    /**
     * 处理网络异常问题时的状态显示
     * 此方法的回调仅实现重新初始化
     * 此处理方法仅供参考  如使用 请覆盖重写此方法
     */
    public void processNetErrorStatusDefault() {
        /**
         * 从loadData进入的方法
         * @see #loadData(Bundle)
         */
        statusManager.show(DefaultStatus.STATUS_NO_NETWORK, new OnDefaultStatusListener() {
            @Override
            public void onStatusViewCreate(String status, View statusView) {
                statusView.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        statusManager.show(DefaultStatus.STATUS_LOADING);
                        load();
                    }
                });
            }
        });
    }

    /**
     * 获取当前剩下的加载次数 用于是否隐藏当前的状态布局
     *
     * @return 当返回小于等于0时  隐藏状态布局
     */
    public abstract int getRequestCount();

    /**
     * 此方法用于在点击重试时重新加载数据
     */
    public abstract void load();
}
