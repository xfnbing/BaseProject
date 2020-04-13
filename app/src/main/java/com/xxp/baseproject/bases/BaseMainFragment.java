package com.xxp.baseproject.bases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.base.view.WrapperMvpFragment;
import com.gyf.barlibrary.ImmersionBar;
import com.xxp.baseproject.R;

import butterknife.BindView;

/**
 * description:位于与栈底activity的fragment,不可再退
 */

public abstract class BaseMainFragment<P extends MvpBasePresenter> extends WrapperMvpFragment {

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    public static final String KEYTITLE="KEYTITLE";

    @BindView(R.id.tv_toolbar_title)
    public TextView tvTitle;
    @BindView(R.id.base_toolbar)
    public Toolbar toolbar;
    @BindView(R.id.view_line)
    public View viewLine;

    private long TOUCH_TIME = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= LayoutInflater.from(_mActivity).inflate(R.layout.ui_base_layout,null,false);
        View contentView=LayoutInflater.from(_mActivity).inflate(getViewLayout(),null,false);

        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.getRootView().setLayoutParams(layoutParams);

        FrameLayout flContent=rootView.findViewById(R.id.fl_content);
        flContent.addView(contentView);

       return rootView;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar();
    }

    /**
     * 初始化Toolbar,子类可复写该方法实现自己想要的效果
     * 暴露的方法有:
     * 1.设置顶部toolbarTitle文字属性,文字颜色和文字内容, 默认使用清单文件中设置的lable属性
     * 2.设置Toolbar背景色
     * 3.设置Toolbar返回键图片
     */
    protected void initToolbar() {
        Bundle bundle=getArguments();//bundle 即使没有设置也不会为空
        String strTitle=bundle.getString(KEYTITLE);
        if(!TextUtils.isEmpty(strTitle)){
            tvTitle.setText(strTitle);
            ImmersionBar.setTitleBar(_mActivity, toolbar);
        }else {
            toolbar.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        }
    }
    /**
     * 处理回退事件
     *
     * @return
     */
    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity.finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(_mActivity, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
