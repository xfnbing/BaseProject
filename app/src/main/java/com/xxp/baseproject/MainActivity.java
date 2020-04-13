package com.xxp.baseproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.easyder.wrapper.core.model.BaseVo;
import com.xxp.baseproject.bases.BaseActivity;
import com.xxp.baseproject.bases.PubPresenter;

public class MainActivity extends BaseActivity<PubPresenter> {

    @Override
    protected int getViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState, Intent intent) {

    }

    @Override
    protected void loadData(Bundle savedInstanceState, Intent intent) {

    }

    @Override
    public void showContentView(String url, BaseVo dataVo) {

    }
}
