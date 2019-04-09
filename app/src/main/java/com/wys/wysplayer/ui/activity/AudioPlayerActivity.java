package com.wys.wysplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wys.wysplayer.R;
import com.wys.wysplayer.bean.AudioBean;
import com.wys.wysplayer.lyric.LyricView;
import com.wys.wysplayer.service.AudioService;
import com.wys.wysplayer.service.MusicInterface;
import com.wys.wysplayer.utils.StringUtil;

/**
 * create by wys
 * on2019/4/8,17:33
 * Emial : 424126544@qq.com
 */
public class AudioPlayerActivity extends BaseActivity implements View.OnClickListener {


    private static final int MSG_UPDATE_PLAY_TIME = 0;
    private static final int MSG_ROLL_LYRICS = 1;

    private ServiceConnection serviceConnection;
    private AudioService mService;
    private Button btnPlayPause;
    private MusicPreparedBroadcastReceiver musicPreparedBroadcastReceiver;
    private TextView tvTitle;
    private ImageView ivBack;
    private ImageView ivWave;
    private TextView tvPlayTotal;
    private SeekBar sbProgress;
    private Button btnPre;
    private Button btnNext;
    private Button btnPlayMode;
    private LyricView mLyricView;

    @Override
    public int getLayout() {
        return R.layout.activity_audio_player;
    }

    @Override
    public void initView() {
        sbProgress = (SeekBar) findViewById(R.id.sb_audio_progress);
        btnPlayPause = (Button) findViewById(R.id.btn_play_pause);
        ivBack = (ImageView) findViewById(R.id.iv_audio_back);
        tvTitle = (TextView) findViewById(R.id.tv_audio_title);
        ivWave = (ImageView) findViewById(R.id.iv_wave);
        tvPlayTotal = (TextView) findViewById(R.id.tv_play_total_time);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnPlayMode = (Button) findViewById(R.id.btn_play_mode);
        mLyricView = (LyricView) findViewById(R.id.lyric_view);
    }

    @Override
    public void initData() {
        //start开启服务，让其长期运行在后台
        Intent intent = new Intent(getIntent());
        intent.setClass(this, AudioService.class);
        startService(intent);

        //bind绑定服务，可以调用服务里的方法
        serviceConnection = new ServiceConnection();
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        //注册音乐准备完成的广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioService.ACTION_PREPARED);
        musicPreparedBroadcastReceiver = new MusicPreparedBroadcastReceiver();
        registerReceiver(musicPreparedBroadcastReceiver, filter);

        //开启示波器的帧动画
        AnimationDrawable ad = (AnimationDrawable) ivWave.getBackground();
        ad.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(musicPreparedBroadcastReceiver);
    }

    @Override
    public void initListener() {
        btnPlayPause.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(new AudioSeekBarChangeListener());
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPlayMode.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause://播放、暂停
                switchPlayPause();
                break;
            case R.id.iv_audio_back://返回
                finish();
                break;
            case R.id.btn_pre://上一曲
                mService.playPre();
                break;
            case R.id.btn_next://下一曲
                mService.playNext();
                break;
            case R.id.btn_play_mode://设置播放模式
                switchPlayMode();
                break;
            default:
                break;
        }
    }

    private void switchPlayMode() {
        mService.switchPlayMode();
        updatePlayModeStatus();
    }

    /**
     * 修改播放模式的背景图片
     */
    public void updatePlayModeStatus() {
        switch (mService.getPlayMode()) {
            case AudioService.PLAY_MODE_REPATE_ALL: //顺序播放
                btnPlayMode.setBackgroundResource(R.drawable.audio_playmode_repate_all_selector);
                break;
            case AudioService.PLAY_MODE_REPATE_SINGLE: //单曲循环
                btnPlayMode.setBackgroundResource(R.drawable.audio_playmode_repate_single_selector);
                break;
            case AudioService.PLAY_MODE_RANDOM: //随机播放播放
                btnPlayMode.setBackgroundResource(R.drawable.audio_playmode_repate_random_selector);
                break;
            default:
                break;
        }
    }

    /**
     * 播放、暂停
     */
    private void switchPlayPause() {
        mService.switchPlayPause();
        updatePlayPauseStatus();
    }

    private class ServiceConnection implements android.content.ServiceConnection {
        //服务连接成功后调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicInterface music = (MusicInterface) service;
            mService = music.getService();
        }

        //服务失去连接
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * 音乐准备完成
     */
    private class MusicPreparedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePlayPauseStatus();

            //设置音乐标题
            AudioBean bean = (AudioBean) intent.getSerializableExtra("bean");
            String title = bean.getTitle();
            tvTitle.setText(title);

            //把进度的最大值和音乐的总是关联起来
            sbProgress.setMax(mService.getTotalTime());
            //设置音乐已播放、总时间
            startPlayTotalTime();

            //修改播放模式的背景图片
            updatePlayModeStatus();

            //从歌词文件中加载歌词
//            File file = new File("/mnt/sdcard/Download/" + title+".lrc");
//            mLyricView.parseLyrics(file);
            mLyricView.parseLyrics(title);

            //滚动歌词
            rollLyrics();
        }
    }

    private void rollLyrics() {
        mLyricView.roll(mService.getCurrentTime(), mService.getTotalTime());
        //实时处理消息
        mHandler.sendEmptyMessage(MSG_ROLL_LYRICS);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_PLAY_TIME://已播放时间
                    startPlayTotalTime();
                    break;
                case MSG_ROLL_LYRICS:
                    rollLyrics();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置音乐已播放、总时间
     */
    private void startPlayTotalTime() {
        int currentTime = mService.getCurrentTime();

        updatePlayTime(currentTime);

        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_TIME, 500);
    }

    /**
     * 更新音乐的已播放时间、修改进度条的进度
     */
    private void updatePlayTime(int currentTime) {
        int totalTime = mService.getTotalTime();
        tvPlayTotal.setText(StringUtil.formatTime(currentTime) + "/" + StringUtil.formatTime(totalTime));
        sbProgress.setProgress(currentTime);
    }

    /**
     * 更新播放、暂停按钮的背景图片
     */
    private void updatePlayPauseStatus() {
        if (mService.isPlaying()) {
            btnPlayPause.setBackgroundResource(R.drawable.audio_pause_selector);
        } else {
            btnPlayPause.setBackgroundResource(R.drawable.audio_play_selector);
        }
    }

    /**
     * 音乐播放进度的改变
     */
    private class AudioSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            mService.seekTo(progress);
            updatePlayTime(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
