package com.wys.wysplayer.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * create by wys
 * on2019/4/8,8:44
 * Emial : 424126544@qq.com
 */
public class ToastUtil {
    private static Looper myLooper = Looper.myLooper();
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            mToast.show();
        }
    };
    private static Toast mToast;


    public static void showToast(final String msg) {
        if (null == mToast) {
            mToast = Toast.makeText(MApplication.mContext, msg, Toast.LENGTH_LONG);
            if (Looper.getMainLooper() == myLooper) {
                mToast.setText(msg);
                mToast.show();
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mToast.setText(msg);
                    }
                });
            }
        } else {
            mToast.setText(msg);
            mToast.show();
        }
    }

}
