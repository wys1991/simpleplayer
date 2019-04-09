package com.wys.wysplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * create by wys
 * on2019/4/9,11:47
 * Emial : 424126544@qq.com
 */
public class StringUtil {

    private static int I_HOUR = 60 * 60 * 1000;
    private static int I_MINATE = 60 * 1000;
    private static int I_SECOND = 1000;

    /*该方法是将内容提供者中返回视频和音乐长度的int值转化为一个标准时长*/
    public static String formatTime(int time) {
        int h = time / I_HOUR;
        int m = time % I_HOUR / I_MINATE;
        int s = time % I_MINATE / I_SECOND;
        String duration = "";
        if (h > 1) {
            duration = String.format("%02d:%02d:%02d", h, m, s);
        } else {
            duration = String.format("%02d:%02d", m, s);
        }
        return duration;
    }

    public static String getAudioTitle(String string) {
        return string.substring(0, string.lastIndexOf("."));
    }

    public static String formatSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

}
