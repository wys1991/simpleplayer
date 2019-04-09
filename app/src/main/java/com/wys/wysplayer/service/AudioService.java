package com.wys.wysplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.wys.wysplayer.R;
import com.wys.wysplayer.bean.AudioBean;
import com.wys.wysplayer.ui.activity.AudioPlayerActivity;
import com.wys.wysplayer.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * create by wys
 * on2019/4/9,11:13
 * Emial : 424126544@qq.com
 */
public class AudioService extends Service {

    private static final String TAG = "AudioService";
    public static final String ACTION_PREPARED = "com.itheima.mobileplayer.PREPARED";
    public static final int PLAY_MODE_REPATE_ALL = 0;
    public static final int PLAY_MODE_REPATE_SINGLE = 1;
    public static final int PLAY_MODE_RANDOM = 2;
    public static final int NOTIFY_PRE = 0;
    public static final int NOTIFY_NEXT = 1;
    public static final int NOTIFY_CONTENT = 2;

    private String notifyType = "type";

    private int playMode = PLAY_MODE_REPATE_ALL;
    private SharedPreferences mSp;

    public int getPlayMode() {
        return playMode;
    }

    private MediaPlayer mediaPlayer;
    private ArrayList<AudioBean> mAudioList;
    private int mPosition;
    private NotificationManager nm;

    @Override
    public IBinder onBind(Intent intent) {

        return new Music();
    }

    /**
     * 修改音乐的播放进度
     *
     * @param progress
     */
    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    public void switchPlayMode() {
        switch (playMode) {
            case PLAY_MODE_REPATE_ALL: //顺序播放
                playMode = PLAY_MODE_REPATE_SINGLE;
                break;
            case PLAY_MODE_REPATE_SINGLE: //单曲循环
                playMode = PLAY_MODE_RANDOM;
                break;
            case PLAY_MODE_RANDOM: //随机播放播放
                playMode = PLAY_MODE_REPATE_ALL;
                break;
            default:
                break;
        }
        //存储播放模式
        mSp.edit().putInt("play_mode", playMode).commit();
    }

    /**
     * 根据播放模式自动播放下一首歌
     */
    private void autoPlayNext() {
        switch (playMode) {
            case PLAY_MODE_REPATE_ALL: //顺序播放
                if (mPosition == mAudioList.size() - 1) {
                    mPosition = 0;
                } else {
                    mPosition++;
                }
                break;
            case PLAY_MODE_REPATE_SINGLE: //单曲循环
                //mposition不需要修改
                break;
            case PLAY_MODE_RANDOM: //随机播放播放
                Random random = new Random();
                int r = random.nextInt(mAudioList.size() - 1);
                mPosition = r;
                break;
            default:
                break;
        }
        playItem();
    }

    /**
     * 下一曲
     */
    public void playNext() {
        if (mPosition != mAudioList.size() - 1) {
            mPosition++;
            playItem();
        } else {
            Toast.makeText(this, "亲，已经是最后一首歌了，你还想咋地", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 上一曲
     */
    public void playPre() {
        if (mPosition != 0) {
            mPosition--;
            playItem();
        } else {
            Toast.makeText(this, "亲，已经是第一首歌了，你还想咋地", Toast.LENGTH_LONG).show();
        }
    }

    private class Music extends Binder implements MusicInterface {
        @Override
        public AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = getSharedPreferences("config", 0);
        playMode = mSp.getInt("play_mode", PLAY_MODE_REPATE_ALL);

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int type = intent.getIntExtra(notifyType, -1);
        switch (type) {
            case NOTIFY_CONTENT:  //点击通知栏空白处
                LogUtil.LogE(TAG, "点击通知栏空白处");
                notifyUpdateUI();
                break;
            case NOTIFY_PRE:  //上一曲
                LogUtil.LogE(TAG, "上一曲");
                playPre();
                break;
            case NOTIFY_NEXT:  //下一曲
                LogUtil.LogE(TAG, "下一曲");
                playNext();
                break;
            case -1:
            default:
                mAudioList = (ArrayList<AudioBean>) intent.getSerializableExtra("list");
                int position = intent.getIntExtra("pos", -1);
                if (mPosition == position) {
                    //同一首歌
                    notifyUpdateUI();
                } else {
                    mPosition = position;
                    playItem();
                }
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playItem() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            AudioBean audioBean = mAudioList.get(mPosition);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new AudioPreparedListener());
            mediaPlayer.setOnCompletionListener(new CompletionListener());
            mediaPlayer.setDataSource(audioBean.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        showNotify();
    }

    /**
     * 音乐准备完成
     */
    private class AudioPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyUpdateUI();
        }
    }

    private void notifyUpdateUI() {
        Intent intent = new Intent();
        intent.setAction(ACTION_PREPARED);
        intent.putExtra("bean", mAudioList.get(mPosition));
        sendBroadcast(intent);
    }

    /**
     * 播放、暂停音乐
     */
    public void switchPlayPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            removeNotify();
        } else {
            mediaPlayer.start();
            showNotify();
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 获取视频的总时间
     */
    public int getTotalTime() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取音乐播放的当前时间
     */
    public int getCurrentTime() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 音乐播放完成
     */
    private class CompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            autoPlayNext();
        }
    }


    /**
     * 移除通知栏
     */
    public void removeNotify() {
        nm.cancel(0);
    }

    /**
     * 显示通知栏
     */
    public void showNotify() {
        Notification.Builder builder = new Notification.Builder(this);
        //状态来
        builder.setTicker("正在播放：" + mAudioList.get(mPosition).getTitle());
        builder.setSmallIcon(R.mipmap.notification_music_playing);

        //通知栏
        builder.setContent(getRemoteView());
        builder.setContentIntent(getPendingIntent());


        Notification notification = builder.build();

        //显示通知栏
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }

    /**
     * 获取自定义通知栏的布局
     */
    private RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_audio);
        remoteViews.setTextViewText(R.id.tv_notify_title, mAudioList.get(mPosition).getTitle());
        remoteViews.setOnClickPendingIntent(R.id.iv_notify_pre, getPrePendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_notify_next, getNextPendingIntent());
        return remoteViews;
    }

    /**
     * 点击通知栏，打开音乐播放界面
     */
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(notifyType, NOTIFY_CONTENT);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private PendingIntent getNextPendingIntent() {
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtra(notifyType, NOTIFY_NEXT);
        PendingIntent pi = PendingIntent.getService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private PendingIntent getPrePendingIntent() {
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtra(notifyType, NOTIFY_PRE);
        PendingIntent pi = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

}
