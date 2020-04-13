package com.easyder.wrapper.base.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easyder.wrapper.base.adapter.ViewHelper;
import com.easyder.wrapper.base.listener.OnViewHelper;
import com.easyder.wrapper.utils.LogUtils;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Auther:  winds
 * Data:    2017/4/11
 * Desc:
 */

public abstract class WrapperActivity extends SupportActivity {
    private boolean focus = true;  //自动显示和隐藏输入法
    private long lastTime;
    private boolean interceptable;  //是否拦截快速点击事件


    protected Activity mActivity;


    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";
    private static final String LAYOUT_RADIOGROUP = "RadioGroup";


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new FrameLayout(context, attrs);
        } else if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new LinearLayout(context, attrs);
        } else if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new RelativeLayout(context, attrs);
        } else if (name.equals(LAYOUT_RADIOGROUP)) {
            view = new RadioGroup(context, attrs);
        }
        if (view != null) {
            return view;
        }
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeOnCreate();
        setContentView(getViewLayout());
        init();

        initView(savedInstanceState, getIntent());
    }


    protected void init() {
        ButterKnife.bind(this);
        mActivity = this;
    }

    protected void beforeOnCreate() {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //去除默认actionbar
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//统一管理竖屏
    }

    /**
     * 返回值为0或者-1时不添加
     *
     * @return
     */
    protected abstract int getViewLayout();

    protected abstract void initView(Bundle savedInstanceState, Intent intent);

    /**
     * 设置是否拦截快速点击
     *
     * @param interceptable 默认拦截   设置不拦截请设置为 false
     */
    protected void setInterceptable(boolean interceptable) {
        this.interceptable = !interceptable;
    }


    /**
     * 判断是否是快速点击
     *
     * @return
     */
    public boolean isInvalidClick() {
        long time = System.currentTimeMillis();
        long duration = time - lastTime;
        if (duration < 400) {
            return true;
        } else {
            lastTime = time;
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //快速点击拦截
        if (!interceptable && ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInvalidClick()) {
                return true;
            }
        }
        //键盘拦截判断
        if (focus) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideInput(v, ev)) {
                    hideKeyboard(v);
                }
                return super.dispatchTouchEvent(ev);
            }
            // 其他组件响应点击事件
            if (getWindow().superDispatchTouchEvent(ev)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 判断键盘是否应该隐藏
     * 点击除EditText的区域隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                ((EditText) v).setCursorVisible(true);
                return false;
            } else {
                ((EditText) v).setCursorVisible(false);  //隐藏光标
                return true;
            }
        }
        return false;
    }

    /**
     * 设置键盘显否
     *
     * @param v
     * @param visible
     */
    protected void setKeyboardVisible(View v, boolean visible) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (visible) {
                imm.showSoftInput(v, 0);
            } else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    /**
     * 实例化对应layoutId的view同时生成ViewHelper帮助类
     *
     * @param group    可为null
     * @param layoutId
     * @param listener
     * @return
     */
    protected View getHelperView(ViewGroup group, int layoutId, OnViewHelper listener) {
        ViewHelper helper = new ViewHelper(getLayoutInflater().inflate(layoutId, group == null ? null : group instanceof RecyclerView ? (ViewGroup) group.getParent() : group, false));
        if (listener != null) {
            listener.help(helper);
        }
        return helper.getItemView();
    }


    /**
     * 设置自动隐藏输入法
     *
     * @param focus 默认 true 自动隐藏
     */
    protected void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * 普通Toast提示
     *
     * @param msg
     */
    protected void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastView.showToastInCenter(mActivity, msg, Toast.LENGTH_SHORT);
            }
        });
    }
    /**
     * 普通Toast提示
     *
     * @param msg
     */
    protected void showToastTop(String msg) {
        ToastView.showToastInTop(mActivity, msg, Toast.LENGTH_SHORT);
    }
    /**
     * 显示toast
     */
    public  void showSafeToast(final String msg, final boolean isTop) {
        // 判断是在子线程，还是主线程
        if ("main".equals(Thread.currentThread().getName())) {
            if(isTop){
                showToastTop(msg);
            }else {
                showToast(msg);
            }
        } else {
            // 子线程
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isTop){
                        showToastTop(msg);
                    }else {
                        showToast(msg);
                    }

                }
            });
        }
    }
        /**
         * 图文样式的Toast提示
         *
         * @param msg
         * @param resId
         */
    protected void showToast(String msg, int resId) {
        ToastView.showVerticalToast(mActivity, msg, resId);
    }

}
