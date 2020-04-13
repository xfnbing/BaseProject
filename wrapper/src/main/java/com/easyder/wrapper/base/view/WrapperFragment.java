package com.easyder.wrapper.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.easyder.wrapper.R;
import com.easyder.wrapper.base.adapter.ViewHelper;
import com.easyder.wrapper.base.listener.OnViewHelper;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Auther:  winds
 * Data:    2017/5/26
 * Desc:
 */

public abstract class WrapperFragment extends SupportFragment {


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


    /**
     * 设置键盘显否
     *
     * @param v
     * @param visible
     */
    protected void setKeyboardVisible(View v, boolean visible) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (visible) {
                imm.showSoftInput(v, 0);
            } else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }


    protected void showToast(final String msg) {
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastView.showToastInCenter(_mActivity, msg);
            }
        });

    }

    protected void showToast(final int msgId) {
        _mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastView.showToastInCenter(_mActivity, getString(msgId));
            }
        });

    }


    protected void showToast(String msg, int resId) {
        ToastView.showToastInCenter(_mActivity, msg, resId);
    }

    protected void showToast(String msg, int resId, boolean longtime) {
        ToastView.showVerticalToast(_mActivity, msg, resId, longtime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
    }

    /**
     * 获取通用空布局
     *
     * @param mRecyclerView
     * @param imageId
     * @param empty         3531085503
     * @return
     */
    protected View getEmptyView(RecyclerView mRecyclerView, final int imageId, final CharSequence empty) {
        return getHelperView(mRecyclerView, R.layout.common_empty, new OnViewHelper() {
            @Override
            public void help(ViewHelper helper) {
                if (imageId != -1) {
                    helper.setImageResource(R.id.iv_flag, imageId);
                } else {
//                    helper.setVisible(R.id.iv_flag, false);
                }
                helper.setText(R.id.tv_tip, empty);
            }
        });
    }
}

