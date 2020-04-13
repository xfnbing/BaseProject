package com.easyder.wrapper.base.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyder.wrapper.R;
import com.easyder.wrapper.base.adapter.ViewHelper;
import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.base.presenter.MvpPresenter;
import com.easyder.wrapper.core.NetworkChanged;
import com.easyder.wrapper.core.network.ResponseInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.ParameterizedType;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 封装了网络层的Fragment
 *
 * @param <P>
 */
public abstract class WrapperMvpFragment<P extends MvpBasePresenter> extends WrapperFragment implements MvpView {
    protected P presenter;
    protected WrapperDialog progressDialog;
    private boolean loadSuccess;
    private boolean loadFinish; //请求是否成功
    private View contentView;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<? extends MvpPresenter> presenterClass = (Class<? extends MvpPresenter>) type.getActualTypeArguments()[0];
        try {
            this.presenter = (P) presenterClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        presenter.attachView(this);
//        progressDialog = new MaterialDialog.Builder(getActivity())
//                .customView(R.layout.layout_progress_bar, false)
//                .canceledOnTouchOutside(false).build();
//        progressDialog.getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        progressDialog = new WrapperDialog(getActivity()) {
            @Override
            public int getLayoutRes() {
                return R.layout.layout_progress_bar;
            }

            @Override
            protected void setDialogParams(Dialog dialog) {
                setDialogParams(Gravity.CENTER);
                dialog.setCanceledOnTouchOutside(false);
            }

            @Override
            public void help(ViewHelper helper) {

            }
        };
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(getViewLayout(), container, false);
        return contentView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView(savedInstanceState);
        loadData(savedInstanceState);
    }


    @Override
    public void beforeSuccess() {
        loadSuccess = true;
        loadFinish = true;
    }


    @Override
    public void onLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            loadSuccess = false;
            progressDialog.show();
        }
    }

    @Override
    public void onError(ResponseInfo responseInfo) {
        loadFinish = true;
        switch (responseInfo.getState()) {
            case ResponseInfo.TIME_OUT:
                showTimeOutDialog(responseInfo);
                break;
            case ResponseInfo.NO_INTERNET_ERROR:
                showOpenNetworkDialog(responseInfo);
                break;
        }

    }

    @Subscribe
    public void networkStateChanged(NetworkChanged changed) {
        if (!changed.connected) {
            showToast("网络连接已断开");
        } else {
            if (loadFinish && !loadSuccess) {
                //网络已连接数据没有加载成功，重新加载
                loadData(null);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
        super.onDestroy();
        presenter.detachView();
        presenter = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStopLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    /**
     * @return 布局resourceId
     */

    public abstract int getViewLayout();

    /**
     * 初始化View。或者其他view级第三方控件的初始化,及相关点击事件的绑定
     *
     * @param savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 获取请求参数，初始化vo对象，并发送请求
     *
     * @param savedInstanceState
     */
    protected abstract void loadData(Bundle savedInstanceState);


    private void showOpenNetworkDialog(ResponseInfo responseInfo) {
        showToast("网络连接不可用，请打开网络！");
    }


    protected void showTimeOutDialog(ResponseInfo responseInfo) {
        showToast("连接网络超时");
    }


    protected void showLoadingDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public View getContentView() {
        return contentView;
    }

}
