package com.easyder.wrapper.status;

import android.view.View;

/**
 * Auther:  winds
 * Data:    2018/1/17
 * Version: 1.0
 * Desc:
 */


public interface OnStatusListener {
    /**
     * 当前状态View被创建时回调
     * @param status        当前状态名
     * @param statusView    不存在null的情况
     */
    void onStatusViewCreate(String status, View statusView);

    /**
     * 当前状态View被展示时回调
     * @param status        当前状态名
     * @param statusView    不存在null的情况
     */
    void onStatusViewShow(String status, View statusView);

    /**
     * 当前状态View被隐藏时回调，statusView存在为空的情况，若为空表示View被去除或者未被inflate
     * @param status        当前状态名
     * @param statusView    存在为空的情况
     */
    void onStatusViewHide(String status, View statusView);

    /**
     * 当前状态View被去除时回调，statusView存在为空的情况，若为空表示View未被inflate
     * @param status        当前状态名
     * @param statusView    存在为空的情况
     */
    void onStatusViewRemove(String status, View statusView);
}
