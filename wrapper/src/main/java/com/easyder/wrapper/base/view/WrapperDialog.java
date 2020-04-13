package com.easyder.wrapper.base.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.easyder.wrapper.R;
import com.easyder.wrapper.base.adapter.ViewHelper;
import com.easyder.wrapper.base.listener.OnViewHelper;

/**
 * Data:    2017/10/17
 * Version: 1.0
 * Desc:
 */


public abstract class WrapperDialog implements OnViewHelper {

    protected Dialog dialog;
    protected Context context;
    protected ViewHelper helper;

    public WrapperDialog(Context context) {
        this(context, R.style.AlertTipsDialogTheme);
    }

    public WrapperDialog(Context context, @StyleRes int themeResId) {
        this.context = context;
        this.dialog = new Dialog(context, themeResId);
        dialog.setContentView(getHelperView(null, getLayoutRes(), this));
        setDialogParams(dialog);
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
        helper = new ViewHelper(LayoutInflater.from(context).inflate(layoutId, group, false));
        if (listener != null) {
            listener.help(helper);
        }
        return helper.getItemView();
    }


    public WrapperDialog show() {
        if (dialog != null && !isShowing()) {
            dialog.show();
        }
        return this;
    }

    public WrapperDialog dismiss() {
        if (isShowing()) {
            dialog.dismiss();
        }
        return this;
    }

    public boolean isShowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * 设置参数的参考实现
     *
     * @param dialog
     * @param width
     * @param height
     * @param gravity
     */
    protected void setDialogAbsParams(Dialog dialog, int width, int height, int gravity) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        window.setGravity(gravity);
        window.setAttributes(params);
    }

    /**
     * 设置参数的参考实现 zhb
     *
     * @param gravity
     */
    public void setDialogParams(int gravity) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(gravity);
        window.setAttributes(params);
    }


    public abstract int getLayoutRes();

    /**
     * 设置dialog的参数
     *
     * @param dialog
     */
    protected abstract void setDialogParams(Dialog dialog);

    @Override
    public abstract void help(ViewHelper helper);


    public WrapperDialog setHelperCallback(HelperCallback callback) {
        callback.help(dialog, helper);
        return this;
    }

    public interface HelperCallback {
        /**
         * 默认帮助方式
         *
         * @param dialog 用来设置dialog的一些默认参数
         * @param helper 用于布局修改与实现
         */
        void help(Dialog dialog, ViewHelper helper);
    }
}
