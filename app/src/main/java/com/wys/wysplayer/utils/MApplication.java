package com.wys.wysplayer.utils;

import android.app.Application;
import android.content.Context;

/**
 * create by wys
 * on2019/4/8,9:34
 * Emial : 424126544@qq.com
 */
public class MApplication extends Application {

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this.getApplicationContext();
    }
    /**
     * 抓取全局异常
     * */
    public class MUncaughtHandler implements Thread.UncaughtExceptionHandler{


        @Override
        public void uncaughtException(Thread t, Throwable e) {

        }
    }
}
