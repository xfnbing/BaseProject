package com.easyder.wrapper.base.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.core.model.BaseVo;
import com.easyder.wrapper.core.network.ResponseInfo;

/**
 * Auther:  winds
 * Data:    2018/3/12
 * Version: 1.0
 * Desc:   状态实现类
 */


public abstract class WrapperStatusFragment<P extends MvpBasePresenter> extends WrapperMvpFragment<P> {

    private StatusManagerDelegate delegate;

    /**
     * 为status页面提供父布局 非一般情况 请避免重写此方法
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout parent = new FrameLayout(_mActivity);
        parent.addView(super.onCreateView(inflater, container, savedInstanceState));
        return parent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        delegate = new StatusManagerDelegate(getContentView()) {
            @Override
            public int getRequestCount() {
                return presenter.getRequestCount();
            }

            @Override
            public void load() {
                if (isLazyLoad()) {
                    onLazyInitView(null);
                } else {
                    loadData(null);
                }
            }
        };
        delegate.init(view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        delegate.showLoadingStatus();
    }


    @Override
    public void showContentView(String url, BaseVo dataVo) {
        delegate.processStatusOnSuccess();
    }

    @Override
    public void onError(ResponseInfo responseInfo) {
        super.onError(responseInfo);
        delegate.onErrorStatus(responseInfo);
    }

    public StatusManagerDelegate getStatusDelegate() {
        return delegate;
    }

    /**
     * 此页面是否使用了懒加载
     * 用于重试时自动加载
     *
     * @return
     */
    protected abstract boolean isLazyLoad();
}
