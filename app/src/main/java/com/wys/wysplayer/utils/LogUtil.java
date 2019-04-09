package com.wys.wysplayer.utils;

import android.util.Log;

/**
 * create by wys
 * on2019/4/8,8:29
 * Emial : 424126544@qq.com
 */
public class LogUtil {
    private static Boolean isLog = true;

    public static void LogE(String TAG, String msg) {
        if (isLog) {
            Log.e(TAG, msg);
        }

    }

    public static void LogI(String TAG, String msg) {
        if (isLog) {
            Log.i(TAG, msg);
        }

    }

    public static void LogV(String TAG, String msg) {
        if (isLog) {
            Log.v(TAG, msg);
        }

    }

}
