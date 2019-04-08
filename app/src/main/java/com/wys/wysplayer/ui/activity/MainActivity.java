package com.wys.wysplayer.ui.activity;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.wys.wysplayer.R;
import com.wys.wysplayer.adapter.MPagerAdapter;
import com.wys.wysplayer.ui.fragment.AudioFragment;
import com.wys.wysplayer.ui.fragment.BaseFragment;
import com.wys.wysplayer.ui.fragment.VedioFragment;
import com.wys.wysplayer.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private TextView tv_audio;
    private TextView tv_vedio;
    private ViewPager vp;
    private View mIndicator;
    private int VEDIO_FLAG = 0;
    private int AUDIO_FLAG = 1;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        tv_audio = findViewById(R.id.tv_audio);
        tv_vedio = findViewById(R.id.tv_vedio);
        vp = findViewById(R.id.vp);
        mIndicator = findViewById(R.id.indicator);
    }

    @Override
    public void initData() {
        updateTab(VEDIO_FLAG);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int defalutWidth = wm.getDefaultDisplay().getWidth();
        mIndicator.getLayoutParams().width = defalutWidth / 2;


    }

    @Override
    public void initListener() {
        tv_audio.setOnClickListener(this);
        tv_vedio.setOnClickListener(this);
        List<BaseFragment> mList = new ArrayList<>();
        mList.add(new VedioFragment());
        mList.add(new AudioFragment());
        vp.setAdapter(new MPagerAdapter(getSupportFragmentManager(), mList));
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                /**
                 * 手动打造一个类似TabHost的功能
                 * */
                LogUtil.LogI(TAG, "onPageScrolled");
                //获取指示器的长度
                int width = mIndicator.getWidth();
                //计算当前指示器应该划过屏幕的距离
                float movePos = width * v;
                //获取当前指示器的当前位置
                int startPos = width * i;
                //计算当前指示器应该在屏幕位置
                int finalPos = (int) (startPos + movePos);
                ViewHelper.setTranslationX(mIndicator, finalPos);
            }

            @Override
            public void onPageSelected(int i) {
                updateTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void updateTab(int flag) {
        vp.setCurrentItem(flag);
        changeTextColorAndIndicator(flag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_audio:
                updateTab(AUDIO_FLAG);
                break;
            case R.id.tv_vedio:
                updateTab(VEDIO_FLAG);
                break;
            default:

                break;
        }
    }

    private void changeTextColorAndIndicator(int flag) {
        if (VEDIO_FLAG == flag) {
            /*check vedio*/
            tv_vedio.setTextColor(Color.GREEN);
            tv_audio.setTextColor(Color.GRAY);

            ViewPropertyAnimator.animate(tv_vedio).scaleX(1.2f);
            ViewPropertyAnimator.animate(tv_vedio).scaleY(1.2f);
            ViewPropertyAnimator.animate(tv_audio).scaleX(1f);
            ViewPropertyAnimator.animate(tv_audio).scaleY(1f);
        } else if (AUDIO_FLAG == flag) {
            /*check audio*/
            tv_audio.setTextColor(Color.GREEN);
            tv_vedio.setTextColor(Color.GRAY);

            ViewPropertyAnimator.animate(tv_audio).scaleX(1.2f);
            ViewPropertyAnimator.animate(tv_audio).scaleY(1.2f);
            ViewPropertyAnimator.animate(tv_vedio).scaleX(1f);
            ViewPropertyAnimator.animate(tv_vedio).scaleY(1f);
        }


    }

}
