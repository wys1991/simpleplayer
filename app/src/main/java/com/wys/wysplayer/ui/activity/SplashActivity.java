package com.wys.wysplayer.ui.activity;

import android.Manifest;
import android.os.Handler;
import android.os.Message;

import com.wys.wysplayer.R;
import com.wys.wysplayer.utils.LogUtil;

/**
 * create by wys
 * on2019/4/8,9:36
 * Emial : 424126544@qq.com
 * 闪屏页面做一些前边的准备工作
 */
public class SplashActivity extends BaseActivity {
    private String TAG = "SplashActivity";


    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        LogUtil.LogI(TAG, "initView");
    }

    @Override
    public void initData() {
        LogUtil.LogI(TAG, "initData");
//        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startActivity(SplashActivity.this, MainActivity.class);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    public void initListener() {
        LogUtil.LogI(TAG, "initListener");
    }


}
