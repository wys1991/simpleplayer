package com.wys.wysplayer.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.wys.wysplayer.ui.UiInterface;

/**
 * create by wys
 * on2019/4/8,8:28
 * Emial : 424126544@qq.com
 */
public abstract class BaseActivity extends FragmentActivity implements UiInterface {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }
}
