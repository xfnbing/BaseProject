package com.xxp.baseproject.bases;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.base.view.WrapperMvpActivity;
import com.easyder.wrapper.base.view.WrapperMvpFragment;
import com.gyf.barlibrary.ImmersionBar;
import com.xxp.baseproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * description:toolbar有返回箭头的fragment父类
 */

public abstract class BaseBackFragment<P extends MvpBasePresenter> extends WrapperMvpFragment {
    public static final String KEYTITLE = "KEYTITLE";

    @BindView(R.id.tv_toolbar_title)
    public TextView tvTitle;
    @BindView(R.id.toolbar_nav_text)
    public TextView navText;
    @BindView(R.id.tv_right_lable)
    public TextView rightLable;
    @BindView(R.id.base_toolbar)
    public Toolbar toolbar;
    @BindView(R.id.iv_right)
    public ImageView ivRight;
    @BindView(R.id.view_line)
    public View viewLine;


    protected int unWrite = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(_mActivity).inflate(R.layout.ui_base_layout, null, false);
        View contentView = LayoutInflater.from(_mActivity).inflate(getViewLayout(), null, false);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout flContent = rootView.findViewById(R.id.fl_content);
        flContent.setBackgroundColor(getResources().getColor(android.R.color.white));
        flContent.addView(contentView,layoutParams);
        return rootView;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rightLable.setVisibility(View.GONE);
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
        Bundle bundle = getArguments();
        String strTitle = bundle.getString(KEYTITLE);
        if (!TextUtils.isEmpty(strTitle)) {
            toolbar.setTitle("");
            ((WrapperMvpActivity)_mActivity).setSupportActionBar(toolbar);
            navText =toolbar.findViewById(R.id.toolbar_nav_text);
            navText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pop();
                }
            });
            ImmersionBar.setTitleBar(_mActivity, toolbar);
        } else {
            toolbar.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        }
    }

    protected void setCenterTitle(String title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    protected void setRightTitle(String right) {
        rightLable.setText(right);
        rightLable.setVisibility(View.VISIBLE);
    }


    protected void setLeftTitle(String left) {
        navText.setText(left);
    }

    public void setTopBar(View viewStatus){
        if (Build.VERSION.SDK_INT >= 19) {
            ImmersionBar mImmersionBar = ImmersionBar.with(this);
            mImmersionBar.titleBar(viewStatus).navigationBarWithKitkatEnable(false).statusBarDarkFont(true, 0.2f).init();
        } else {
            viewStatus.setVisibility(View.GONE);
        }
    }
}
