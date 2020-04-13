package com.easyder.wrapper.status;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.easyder.wrapper.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;


public class StatusManager {

    private Map<String, StatusProvider> map = new HashMap<>();
    private StatusProvider currentStatusProvider;
    protected View contentView;     //内容View
    protected ViewGroup containerView; //承装的父布局

    /**
     * @param contentView 當前内容View對象 view需要有父佈局，如若没有不会正常显示
     *                    目前测试在Fragment中，如若承装Fragment的父布局为FrameLayout可通过Fragment中getView获取当前内容布局，无需包裹父布局
     *                    如若承装Fragment的父布局为ViewPager，需要在Fragment内容布局外包裹层父布局
     */
    public StatusManager(View contentView) {
        this.contentView = contentView;
        ViewParent parent = this.contentView.getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                containerView = (ViewGroup) parent;
            } else {
                throw new RuntimeException(contentView.getClass().getName() + "必须作为ViewGroup的子类");
            }
        } else {
            throw new RuntimeException(contentView.getClass().getName() + "必须要有ViewParent");
        }
    }

    /**
     * 加入对应的StatusProvider
     *
     * @param p
     */
    public void addStatusProvider(StatusProvider p) {
        map.put(p.getStatus(), p);
    }


    public void show(StatusProvider provider, OnStatusListener listener) {
        map.put(provider.getStatus(), provider);
        show(provider.getStatus(), listener);
    }

    public void show(StatusProvider provider) {
        show(provider, null);
    }

    /**
     * 展示对应的状态布局
     *
     * @param status
     */
    public void show(String status) {
        show(status, null);
    }


    /**
     * 展示对应的状态布局
     *
     * @param status   对应status的key
     * @param listener status的生命周期listener
     */
    public void show(String status, OnStatusListener listener) {
        Log.i("--> ", "--> " + status);
        if (currentStatusProvider != null) {
            currentStatusProvider.removeStatusView();
        }
        StatusProvider provider = map.get(status);
        if (provider != null) {
            if (listener != null) {
                provider.setOnStatusListener(listener);
            }
            provider.showStatusView(containerView);
            currentStatusProvider = provider;
        } else {
            switch (status) {
                case DefaultStatus.STATUS_LOADING:
                    show(new DefaultStatusProvider.DefaultLoadingStatusProvider(), listener);
                    break;
                case DefaultStatus.STATUS_EMPTY:
                    show(new DefaultStatusProvider.DefaultEmptyStatusProvider(), listener);
                    break;
                case DefaultStatus.STATUS_NO_NETWORK:
                    show(new DefaultStatusProvider.DefaultNoNetWorkStatusProvider(), listener);
                    break;
                case DefaultStatus.STATUS_NOT_LOGIN:
                    show(new DefaultStatusProvider.DefaultNotLoginStatusProvider(), listener);
                    break;
                case DefaultStatus.STATUS_LOAD_ERROR:
                    show(new DefaultStatusProvider.DefaultLoadErrorStatusProvider(), listener);
                    break;
                default:
                    Log.e("--> ", "--> 请先加入对应的StatusProvider");
                    break;
            }
        }
    }

    /**
     * 展示内容view
     * 此方法不外暴漏，需要展示当前内容布局，请先移除或隐藏当前状态布局
     */
    protected void showContentView() {
        contentView.setVisibility(View.VISIBLE);
        contentView.bringToFront();
    }

    /**
     * 获取当前展示的状态
     *
     * @return
     */
    public String getCurrentStatus() {
        return currentStatusProvider == null ? "" : currentStatusProvider.getStatus();
    }

    /**
     * 隐藏当前的状态布局，以便再次使用重新实例化
     * 使用此方法请确保在有合适的实际调用@{#removeStatus}来移除当前状态布局
     */
    public void hideStatus() {
        removeStatus(false);
    }


    /**
     * 清除当前的状态布局
     */
    public void removeStatus() {
        removeStatus(true);
    }


    /**
     * 清除指定的状态布局
     *
     * @param status
     */
    public void removeStatus(String status) {
        StatusProvider provider = map.get(status);
        if (provider != null) {
            if (provider == currentStatusProvider) {
                removeStatus();
            } else {
                provider.removeStatusView();
            }
            map.remove(status);
        }
    }

    /**
     * 是否清除当前的状态布局
     *
     * @param full true 表示移除  false表示单纯隐藏
     */
    public void removeStatus(boolean full) {
        if (currentStatusProvider != null) {
            if (full) {
                currentStatusProvider.removeStatusView();
                map.remove(currentStatusProvider.getStatus());
            } else {
                currentStatusProvider.hideStatusView();
            }
            showContentView();
        }
    }

    /**
     * 清除指定的状态布局
     *
     * @param status
     * @param full   true 表示移除  false表示单纯隐藏
     */
    public void removeStatus(String status, boolean full) {
        StatusProvider provider = map.get(status);
        if (provider != null) {
            if (provider == currentStatusProvider) {
                removeStatus(full);
            } else {
                if (full) {
                    provider.removeStatusView();
                    map.remove(status);
                } else {
                    provider.hideStatusView();
                }
            }
        }
    }

    /**
     * 清空所有状态布局
     */
    public void removeAllStatus() {
        if (map.size() > 0) {
            for (String status : map.keySet()) {
                removeStatus(status);
            }
        }
    }
}
