package com.wys.wysplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import com.wys.wysplayer.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * create by wys
 * on2019/4/9,11:33
 * Emial : 424126544@qq.com
 */
public class AudioBean implements Serializable {
    private String data;
    private String title;
    private int duration;
    private long size;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public AudioBean() {

    }

    public static ArrayList<AudioBean> getAudioInfoList(Cursor cursor) {
        ArrayList<AudioBean> aList = new ArrayList<>();
        if (null == cursor && cursor.getCount() == 0) {
            return aList;
        }
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            AudioBean bean = getAudioBean(cursor);
            aList.add(bean);
        }
        return aList;
    }

    public static AudioBean getAudioBean(Cursor cursor) {
        AudioBean bean = new AudioBean();
        if (null != cursor && cursor.getCount() != 0) {
            bean.title = StringUtil.getAudioTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            bean.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            bean.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            bean.data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        }

        return bean;
    }
}
