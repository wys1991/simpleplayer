package com.wys.wysplayer.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.wys.wysplayer.ui.UiInterface;
import com.wys.wysplayer.utils.ToastUtil;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

/**
 * create by wys
 * on2019/4/8,8:28
 * Emial : 424126544@qq.com
 */
public abstract class BaseActivity extends FragmentActivity implements UiInterface {
    private static final int REQUECT_CODE_SDCARD = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initView();
//        MPermissions.requestPermissions(this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET);
        initData();
        initListener();
    }

    public void startActivity(Activity a, Class clazz) {
        Intent i = new Intent(a, clazz);
        startActivity(i);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//
//    @PermissionGrant(REQUECT_CODE_SDCARD)
//    public void requestSdcardSuccess() {
//        ToastUtil.showToast("GRANT ACCESS SDCARD!");
//    }
//
//    @PermissionDenied(REQUECT_CODE_SDCARD)
//    public void requestSdcardFailed() {
//        ToastUtil.showToast("DENY ACCESS SDCARD!");
//    }

}
