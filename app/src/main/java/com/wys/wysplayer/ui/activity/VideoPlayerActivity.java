package com.wys.wysplayer.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wys.wysplayer.R;
import com.wys.wysplayer.bean.VedioBean;
import com.wys.wysplayer.utils.LogUtil;
import com.wys.wysplayer.utils.StringUtil;
import com.wys.wysplayer.view.VideoView;

import java.util.ArrayList;

public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG                    = "VideoPlayerActivity";
    private static final int    MSG_UPDATE_SYSTEM_TIME = 0;
    private static final int    MSG_UPDATE_PLAY_TIME   = 1;
    private static final int    MSG_AUTO_HIDE_CTRL     = 2;

    private VideoView mVideoView;
    private Button                   btnPlayPause;
    private TextView                 tvTitle;
    private TextView                 tvSystemTime;
    private ImageView                ivBattery;
    private BatteryBroadcastReceiver batteryBroadcastReceiver;
    private AudioManager             mAudioManager;
    private ImageView                ivMute;
    private SeekBar                  sbVolume;
    private int                      mCurrentVolume;
    private float                    startY;
    private int                      mScreenHeight;
    private int                      currentVolume;
    private TextView                 tvPlayTime;
    private TextView                 tvTotalTime;
    private SeekBar                  sbPlayPosition;
    private Button                   btnBack;
    private Button                   btnPre;
    private Button                   btnDefaultFullScreen;
    private Button                   btnNext;
    private ArrayList<VedioBean>     mVideoList;
    private int                      mPosition;
    private GestureDetector          mGestureDetectorsture;
    private LinearLayout             llBottom;
    private LinearLayout             llTop;
    private LinearLayout             llLoading;
    private ProgressBar              pbProgress;

    @Override
    public int getLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initView() {
        //视频
        mVideoView = (VideoView) findViewById(R.id.vv);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llLoading.setVisibility(View.VISIBLE);
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);

        //顶部栏
        tvTitle = (TextView) findViewById(R.id.tv_video_title);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        ivMute = (ImageView) findViewById(R.id.iv_mute);
        sbVolume = (SeekBar) findViewById(R.id.seekBar);

        //底部兰
        btnPlayPause = (Button) findViewById(R.id.btn_video_play_pause);
        tvPlayTime = (TextView) findViewById(R.id.tv_play_time);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        sbPlayPosition = (SeekBar) findViewById(R.id.sb_play_position);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnDefaultFullScreen = (Button) findViewById(R.id.btn_default_full_screen);
    }

    @Override
    public void initData() {
        //        VideoBean videoBean = (VideoBean) intent.getSerializableExtra("bean");
        //        LogUtils.e(TAG, videoBean.toString());
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data == null) {
            mVideoList = (ArrayList<VedioBean>) intent.getSerializableExtra("list");
            mPosition = intent.getIntExtra("pos", -1);

            playItem();
        } else {
            mVideoView.setVideoURI(data);
            tvTitle.setText(data.toString());
            btnNext.setEnabled(false);
            btnPre.setEnabled(false);
        }
        //初始化系统时间
        startSystemTime();

        //注册系统电池电量变化的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        registerReceiver(batteryBroadcastReceiver, filter);

        //获取系统的音量
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = getMaxSystemVolume();
        //把系统音量和SeekBar关联起来
        sbVolume.setMax(maxVolume);
        //设置当前的系统音量
        int currentVolume = getCurrentVolume();
        sbVolume.setProgress(currentVolume);
        //        LogUtils.e(TAG, "系统最大音量："+maxVolume);

        //获取手机屏幕的高度
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();
        mScreenHeight = defaultDisplay.getHeight();

    }

    private void playItem() {
        btnPre.setEnabled(mPosition != 0);
        btnNext.setEnabled(mPosition != mVideoList.size() - 1);

        VedioBean videoBean = mVideoList.get(mPosition);
        mVideoView.setVideoURI(Uri.parse(videoBean.getData()));
        //摄者视频的标题
        tvTitle.setText(videoBean.getTitle());
    }

    /**
     * 获取系统当前的音量
     */
    private int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 获取系统最大的音量
     */
    private int getMaxSystemVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity销毁前移除Handler里所有的消息
        mHandler.removeCallbacksAndMessages(null);
        //注销电池电量变化的广播接收者
        unregisterReceiver(batteryBroadcastReceiver);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_SYSTEM_TIME: //更新系统时间
                    startSystemTime();
                    break;
                case MSG_UPDATE_PLAY_TIME://已播放时间
                    updatePlayPosition();
                    break;
                case MSG_AUTO_HIDE_CTRL:
                    hideCtrl();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化系统时间
     */
    private void startSystemTime() {
        //        LogUtils.e(TAG, "当前系统时间：" + System.currentTimeMillis());

        tvSystemTime.setText(StringUtil.formatSystemTime());

        //发送消息
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_SYSTEM_TIME, 500);
    }

    @Override
    public void initListener() {
        //底部兰
        btnPlayPause.setOnClickListener(this);
        mVideoView.setOnPreparedListener(new VideoPreparedListener());
        SeekBarChangeListener seekBarChangeListener = new SeekBarChangeListener();
        sbPlayPosition.setOnSeekBarChangeListener(seekBarChangeListener);
        btnBack.setOnClickListener(this);
        btnDefaultFullScreen.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        //顶部栏
        sbVolume.setOnSeekBarChangeListener(seekBarChangeListener);
        ivMute.setOnClickListener(this);

        //视频
        mVideoView.setOnCompletionListener(new CompletionListener());
        mGestureDetectorsture = new GestureDetector(new SimpleOnGestureListener());
        mVideoView.setOnInfoListener(new VideoInfoListener());
        mVideoView.setOnBufferingUpdateListener(new BufferingUpdateListener());
        mVideoView.setOnErrorListener(new ErrorListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetectorsture.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                currentVolume = getCurrentVolume();
                break;
            case MotionEvent.ACTION_MOVE:
                //4. 划过屏幕的距离   =  移动的坐标 - 按下时的坐标
                float offsetY = event.getY() - startY;

                //3. 划过屏幕的百分比 = 划过屏幕的距离 / 手机屏幕的高度
                float percent = offsetY / mScreenHeight;

                //2. 变化音量 = 划过屏幕的百分比 * 最大音量
                float chanageVolume = percent * getMaxSystemVolume();

                //1. 最终音量 = 按下音量 + 变化音量
                int finalVolume = (int) (currentVolume + chanageVolume);

                updateSystemVolume(finalVolume);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_video_play_pause://播放、暂停
                switchPlayPause();
                break;
            case R.id.iv_mute://静音按钮
                switchMute();
                break;
            case R.id.btn_back://返回
                finish();
                break;
            case R.id.btn_pre://上一曲
                playPre();
                break;
            case R.id.btn_next://下一曲
                palyNext();
                break;
            case R.id.btn_default_full_screen:
                switchFullScreen();
                break;
            default:
                break;
        }
    }

    private void switchFullScreen() {
        mVideoView.switchDefaultFullScreen();
        updateFullScreenStatus();
    }

    private void updateFullScreenStatus() {
        if (mVideoView.isFullScreen()) {
            btnDefaultFullScreen.setBackgroundResource(R.drawable.video_default_screen_selector);
        } else {
            btnDefaultFullScreen.setBackgroundResource(R.drawable.video_fullscreen_selector);
        }
    }

    private void playPre() {
        if (mPosition != 0) {
            mPosition--;
            playItem();
        }
    }

    private void palyNext() {
        if (mPosition != mVideoList.size() - 1) {
            mPosition++;
            playItem();
        }
    }

    /**
     * 静音按钮的处理
     * 如果是静音，回复上一次的音量
     * 如果不是静音，设置为静音
     */
    private void switchMute() {
        int currVolume = getCurrentVolume();
        if (currVolume == 0) {
            //如果是静音，回复上一次的音量
            updateSystemVolume(mCurrentVolume);
        } else {
            //保存当前的音量之
            mCurrentVolume = getCurrentVolume();

            //如果不是静音，设置为静音
            updateSystemVolume(0);
        }
    }

    /**
     * 修改系统音量
     */
    private void updateSystemVolume(int volume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        sbVolume.setProgress(volume);
    }

    private void switchPlayPause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }

        updatePlayPause();
    }

    /**
     * 更新播放、暂停按钮背景
     */
    private void updatePlayPause() {
        if (mVideoView.isPlaying()) {
            //正在播放，显示暂停按钮
            btnPlayPause.setBackgroundResource(R.drawable.video_pause_selector);
            //发送更新已播放时间的消息
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_TIME, 500);

        } else {
            btnPlayPause.setBackgroundResource(R.drawable.video_play_selector);
            mHandler.removeMessages(MSG_UPDATE_PLAY_TIME);
        }
    }

    /**
     * 视频准备完成监听
     */
    private class VideoPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //隐藏loading
            llLoading.setVisibility(View.INVISIBLE);

            //准备完成后播放
            mVideoView.start();

            //设置视频的总时间
            tvTotalTime.setText(StringUtil.formatTime(mp.getDuration()));

            //更新播放、暂停按钮
            updatePlayPause();

            //设置进度条的最大值
            sbPlayPosition.setMax(mp.getDuration());
            //更新已播放时间
            updatePlayPosition();

            //延迟5秒发送隐藏控制面板的消息
            notifyAutoHideCtrl();
        }
    }

    /**
     * 开始更新已播放时间
     */
    private void updatePlayPosition() {
        LogUtil.LogE(TAG, "跟新已播放时间");

        int currentPosition = mVideoView.getCurrentPosition();

        updatePlayTime(currentPosition);

        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_TIME, 500);
    }

    /**
     * 跟新已播放时间
     */
    private void updatePlayTime(int currentPosition) {


        tvPlayTime.setText(StringUtil.formatTime(currentPosition));
        sbPlayPosition.setProgress(currentPosition);
    }

    /**
     * 电池电量变化的广播接收者
     */
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", -1);
            LogUtil.LogE(TAG, "系统电量：" + level);

            updateSystemBatteryStatus(level);
        }
    }

    /**
     * 修改系统电池电量的背景图片
     */
    private void updateSystemBatteryStatus(int level) {
        if (level == 100) {
            ivBattery.setImageResource(R.mipmap.ic_battery_100);
        } else if (level >= 80 && level < 100) {
            ivBattery.setImageResource(R.mipmap.ic_battery_80);
        } else if (level < 80 && level >= 60) {
            ivBattery.setImageResource(R.mipmap.ic_battery_60);
        } else if (level >= 40 && level < 60) {
            ivBattery.setImageResource(R.mipmap.ic_battery_40);
        } else if (level >= 20 && level < 40) {
            ivBattery.setImageResource(R.mipmap.ic_battery_20);
        } else {
            ivBattery.setImageResource(R.mipmap.ic_battery_0);
        }
    }

    /**
     * 进度条拖动的监听
     */
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        //进度变化后回调
        //fromUser：是不是用户拖动的进度条
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtil.LogE(TAG, "fromUser:" + fromUser);
            if (!fromUser) {
                return;
            }
            switch (seekBar.getId()) {
                case R.id.seekBar:
                    //参数3:  是否显示系统的音量控制面板，1显示，0不显示
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 1);
                    break;
                case R.id.sb_play_position:  //已播放时间
                    mVideoView.seekTo(progress);
                    updatePlayTime(progress);
                    break;
                default:
                    break;
            }
        }

        //手指触摸到进度条时调用
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(MSG_AUTO_HIDE_CTRL);
        }

        //手指离开进度条前调用
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            notifyAutoHideCtrl();
        }
    }

    /**
     * 视频播放完成
     */
    private class CompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mHandler.removeMessages(MSG_UPDATE_PLAY_TIME);

            //规避播放完成bug
            int max = mp.getDuration();
            updatePlayTime(max);

            //更新播放、暂停按钮的状态
            updatePlayPause();
        }
    }

    private class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        //长按
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            LogUtil.LogE(TAG, "onLongPress");
            switchPlayPause();
        }

        //双击
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            LogUtil.LogE(TAG, "onDoubleTap");
            switchFullScreen();
            return super.onDoubleTap(e);
        }

        //单击事件
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            LogUtil.LogE(TAG, "onSingleTapConfirmed");
            switchCtrl();
            return super.onSingleTapConfirmed(e);
        }
    }

    private boolean isShowing = true;

    /**
     * 延迟5秒发送隐藏控制面板的消息
     */
    private void notifyAutoHideCtrl() {
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_HIDE_CTRL, 5000);
    }

    private void switchCtrl() {
        if (isShowing) {
            //隐藏控制面板
            hideCtrl();
        } else {
            //显示控制面板
            showCtrl();
        }
    }

    private void showCtrl() {
        ViewPropertyAnimator.animate(llTop).translationY(0);
        ViewPropertyAnimator.animate(llBottom).translationY(0);
        isShowing = true;

        notifyAutoHideCtrl();
    }

    private void hideCtrl() {
        ViewPropertyAnimator.animate(llTop).translationY(-llTop.getHeight());
        ViewPropertyAnimator.animate(llBottom).translationY(llBottom.getHeight());
        isShowing = false;
    }

    private class VideoInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始缓冲
                    pbProgress.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://结束缓冲
                    pbProgress.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     * 网络缓冲的第二进度
     */
    private class BufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            LogUtil.LogE(TAG, "percent:" + percent);

            float pre = percent / 100f;
            int pro = (int) (pre * mp.getDuration());

            sbPlayPosition.setSecondaryProgress(pro);
        }
    }

    /**
     * 视频出错处理
     */
    private class ErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
            builder.setTitle("提示");
            builder.setMessage("亲，您播放的视频有问题哦！");
             builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                     finish();
                 }
             }) ;
            builder.show();
            return false;
        }
    }
}
