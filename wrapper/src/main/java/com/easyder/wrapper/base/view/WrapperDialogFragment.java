package com.easyder.wrapper.base.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.easyder.wrapper.R;
import com.easyder.wrapper.base.adapter.ViewHelper;
import com.easyder.wrapper.base.listener.OnViewHelper;
import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.base.presenter.MvpPresenter;
import com.easyder.wrapper.core.NetworkChanged;
import com.easyder.wrapper.core.network.ResponseInfo;
import com.easyder.wrapper.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.ParameterizedType;

import butterknife.ButterKnife;

/**
 * @author 刘琛慧
 *         date 2015/8/11.
 */
public abstract class WrapperDialogFragment<P extends MvpBasePresenter> extends DialogFragment implements MvpView {
    protected P presenter;
    protected WrapperDialog progressDialog;
    private boolean loadSuccess;
    private boolean loadFinish; //请求是否成功
    protected Activity _Activity;

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
        //setStyle(DialogFragment.STYLE_NO_TITLE | DialogFragment.STYLE_NO_FRAME, 0);
//        progressDialog = new MaterialDialog.Builder(getActivity())
//                .customView(R.layout.layout_progress_bar, false)
//                .canceledOnTouchOutside(false)
//                .cancelable(true).build();
        _Activity = getActivity();
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
        View contentView = inflater.inflate(getViewLayout(), container, false);
        ButterKnife.bind(this, contentView);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //去除标题栏
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        loadData(savedInstanceState);
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

    protected void showToast(Context context, String msg) {
        ToastView.showToastInCenter(context, msg);
    }

    protected void showToast(String msg) {
        ToastView.showToastInCenter(getActivity(), msg);
    }


    @Override
    public void beforeSuccess() {
        loadSuccess = true;
        loadFinish = true;
    }

    @Override
    public void onLoading() {
        if (!progressDialog.isShowing()) {
            loadSuccess = false;
            progressDialog.show();
        }
    }

    @Override
    public void onError(ResponseInfo responseInfo) {
        loadFinish = true;
    }

    @Subscribe
    public void onEvent(NetworkChanged networkStateEvent) {
        if (!networkStateEvent.connected) {
            ToastView.showToastInCenter(getActivity(), "网络连接已断开", Toast.LENGTH_LONG);
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
        if (progressDialog!=null&&progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 实例化对应layoutId的view同时生成ViewHelper
     *
     * @param group    可为null
     * @param layoutId
     * @param listener
     * @return
     */
    protected View getHelperView(ViewGroup group, int layoutId, OnViewHelper listener) {
        ViewHelper helper = new ViewHelper(getActivity().getLayoutInflater().inflate(layoutId, group == null ? null : group instanceof RecyclerView ? (ViewGroup) group.getParent() : group, false));
        if (listener != null) {
            listener.help(helper);
        }
        return helper.getItemView();
    }

}
