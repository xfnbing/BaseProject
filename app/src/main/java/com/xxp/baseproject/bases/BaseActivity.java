package com.xxp.baseproject.bases;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.base.view.WrapperMvpActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.xxp.baseproject.R;

import butterknife.BindView;

/**
 * description:这层的baseActivity是应用层自己封装的一些特色功能,根据自身应用去抽取,不同于WrapperMvpActivity以上的父类,那些
 * 都是比较公共性质的
 */

public abstract class BaseActivity<P extends MvpBasePresenter> extends WrapperMvpActivity {
    public static final String KEYTITLE = "KEYTITLE";

    public TextView tvTitle;
    public Toolbar toolbar;
    public TextView navText;
    public View viewLine;


    @Override
    public void setContentView(int layoutResID) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.ui_base_layout, null, false);
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null, false);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.getRootView().setLayoutParams(layoutParams);

        FrameLayout flContent = rootView.findViewById(R.id.fl_content);
        flContent.addView(contentView);

        getWindow().setContentView(rootView);
        tvTitle = findViewById(R.id.tv_toolbar_title);
        toolbar = findViewById(R.id.base_toolbar);//include标签里的id会覆盖引用的资源文件的根view的id
        viewLine = findViewById(R.id.view_line);
        navText = findViewById(R.id.toolbar_nav_text);
        navText.setVisibility(View.GONE);
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
        tvTitle.setText(getTitle());
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));//设置Toolbar背景色
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        ImmersionBar.setTitleBar(this, toolbar);
        setSupportActionBar(toolbar);//项目主题要设置成noActionBar,否则会报错
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        Bundle bundle = getIntent().getExtras();//bundle 即使没有设置也不会为空
        if (bundle != null) {
            String strTitle = bundle.getString(KEYTITLE);
            if (!TextUtils.isEmpty(strTitle)) {
                tvTitle.setText(strTitle);
            } else {
                toolbar.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);
            }
        } else {
            toolbar.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        }
    }

    /**
     * 只有返回
     */
    protected void setNoActionBar() {
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(true);
            toolbar.setTitle(getString(R.string.str_back));
        }
    }

    protected void setHideActionBar() {
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(true);
            toolbar.setVisibility(View.GONE);
        }
    }

    protected void start(Intent intent,String title){
        if(intent != null && !TextUtils.isEmpty(title)){
            intent.putExtra(KEYTITLE,title);
        }
        startActivity(intent);
    }

    protected void startWithPop(Intent intent,String title){
        start(intent,title);
        finish();
    }

}
