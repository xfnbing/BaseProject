package com.easyder.wrapper.status;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 */
public abstract class StatusProvider {

    protected FrameLayout container; //承装的父布局
    protected View statusView;      //当前对应的状态View
    protected String status;        //对应的状态
    protected OnStatusListener listener;

    public StatusProvider(@NonNull String status) {
        this.status = status;
    }

    /**
     * 展示当前状态view
     */
    public void showStatusView(ViewGroup container) {
        if (statusView == null) {
            statusView = getStatusView(container);
            if (listener != null) {
                listener.onStatusViewCreate(status, statusView);
            }
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(statusView, lp);
        }
        statusView.setVisibility(View.VISIBLE);
        statusView.bringToFront();
        if (listener != null) {
            listener.onStatusViewShow(status, statusView);
        }
    }

    /**
     * 隐藏当前状态view
     */
    public void hideStatusView() {
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }
        if (listener != null) {
            listener.onStatusViewHide(status, statusView);
        }
    }


    /**
     * 去除当前状态view
     */
    public void removeStatusView() {
        if (statusView != null) {
            ViewParent parent = statusView.getParent();
            if(parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(statusView);
            }
            statusView = null;
        }
        if (listener != null) {
            listener.onStatusViewRemove(status, statusView);
        }
    }

    /**
     * 获取当前的状态
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    public StatusProvider setOnStatusListener(OnStatusListener listener) {
        this.listener = listener;
        return this;
    }

    public abstract View getStatusView(ViewGroup containerView);
}
