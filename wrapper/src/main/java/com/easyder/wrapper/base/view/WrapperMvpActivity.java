package com.easyder.wrapper.base.view;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.easyder.wrapper.R;
import com.easyder.wrapper.base.adapter.ViewHelper;
import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.base.presenter.MvpPresenter;
import com.easyder.wrapper.core.NetworkChanged;
import com.easyder.wrapper.core.network.ResponseInfo;
import com.easyder.wrapper.utils.LogUtils;
import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.ParameterizedType;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator;


/**
 * Auther:  winds
 * Data:    2017/4/11
 * Desc:
 */

public abstract class WrapperMvpActivity<P extends MvpBasePresenter> extends WrapperActivity implements MvpView {
    protected P presenter;
    protected WrapperDialog progressDialog;
    private boolean loadSuccess; //数据是否加载成功
    private boolean loadFinish; //请求是否成功
    private ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mImmersionBar.keyboardEnable(true).navigationBarWithKitkatEnable(false).statusBarDarkFont(true, 0.2f).init();

        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.navigationBarWithKitkatEnable(false).statusBarDarkFont(true, 0.2f).init();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<? extends MvpPresenter> presenterClass = (Class<? extends MvpPresenter>) type.getActualTypeArguments()[0];
        try {
            this.presenter = (P) presenterClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        presenter.attachView(this);

        progressDialog = new WrapperDialog(mActivity) {
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
        loadData(savedInstanceState, getIntent());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //水平切换
//        setFragmentAnimator(new DefaultHorizontalAnimator());
        //竖直切换动画
//        setFragmentAnimator(new DefaultVerticalAnimator());
    }

    @Override
    public void beforeSuccess() {
        loadSuccess = true;
        loadFinish = true;
    }

    @Override
    public void onLoading() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            loadSuccess = false;
        }
    }


    public void showLoadingView() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 状态栏设置
     * https://github.com/gyf-dev/ImmersionBar
     * 文字为黑色，解决状态栏重叠问题
     */
    protected void setStatusColor(@ColorRes int color) {
        if(color==-1){
            ImmersionBar.with(this).transparentBar().init();//全沉浸式
        }else {
            ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(color).statusBarDarkFont(true, 0.2f).init();
        }

    }

    @Override
    public void onError(ResponseInfo responseInfo) {
        loadFinish = true;
    }

    /**
     * 网络状态改变事件，由子类重写实现相关的逻辑
     *
     * @param networkStateEvent
     */
    @Subscribe
    public void onEvent(NetworkChanged networkStateEvent) {
        if (!networkStateEvent.connected) {
            showToast("网络连接已断开!");
        } else {
            if (loadFinish && !loadSuccess) {
                //网络已连接数据没有加载成功，重新加载
                loadData(null, getIntent());
            }
        }
    }

    /**
     * 从intent中获取请求参数，初始化vo对象，并发送请求
     *
     * @param savedInstanceState
     * @param intent
     */
    protected abstract void loadData(Bundle savedInstanceState, Intent intent);

    @Override
    protected void onDestroy() {
        onStopLoading();
        super.onDestroy();
        progressDialog = null;
        presenter.detachView();
        presenter = null;
        EventBus.getDefault().unregister(this);
        ImmersionBar.with(this).destroy(); //必须调用该方法，防止内存泄漏
    }


    @Override
    public void onStopLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}

