package com.wys.wysplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * create by wys
 * on2019/4/9,11:33
 * Emial : 424126544@qq.com
 */
public class VedioBean implements Serializable {
    private String title;
    private String data;
    private int duration;
    private long size;

    /**
     * 从cursor中获取集合
     */
    public static ArrayList<VedioBean> getListFromCursor(Cursor cursor) {
        ArrayList<VedioBean> list = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return list;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            VedioBean bean = getVedioBean(cursor);
            list.add(bean);
        }
        return list;
    }


    /**
     * 从Cursor中获取JavaBean对象
     *
     * @param cursor
     */
    public static VedioBean getVedioBean(Cursor cursor) {
        VedioBean bean = new VedioBean();

        if (cursor == null || cursor.getCount() == 0) {
            return bean;
        }

        bean.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        bean.data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        bean.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        bean.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

        return bean;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    @Override
    public String toString() {
        return "VideoBean{" +
                "title='" + title + '\'' +
                ", data='" + data + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }
}

