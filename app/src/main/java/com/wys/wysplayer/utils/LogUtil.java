package com.wys.wysplayer.utils;

import android.util.Log;

/**
 * create by wys
 * on2019/4/8,8:29
 * Emial : 424126544@qq.com
 */
public class LogUtil {
    private Boolean isLog = true;

    public void LogR(String TAG, String msg) {
        if (isLog) {
            Log.e(TAG, msg);
        }

    }

    public void LogI(String TAG, String msg) {
        if (isLog) {
            Log.i(TAG, msg);
        }

    }

    public void LogV(String TAG, String msg) {
        if (isLog) {
            Log.v(TAG, msg);
        }

    }

}