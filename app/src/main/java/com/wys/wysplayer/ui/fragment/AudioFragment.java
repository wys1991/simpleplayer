package com.wys.wysplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wys.wysplayer.R;
import com.wys.wysplayer.adapter.AudioListAdapter;
import com.wys.wysplayer.bean.AudioBean;
import com.wys.wysplayer.ui.activity.AudioPlayerActivity;
import com.wys.wysplayer.utils.ToastUtil;

import java.util.ArrayList;

/**
 * create by wys
 * on2019/4/8,9:37
 * Emial : 424126544@qq.com
 */
public class AudioFragment extends BaseFragment {

    private ListView lv;
    private Cursor cursor;

    @Override
    public int getLayout() {
        return R.layout.fragmeng_audio;
    }

    @Override
    public void initView() {
        lv = (ListView) findViewByid(R.id.lv);
    }

    @Override
    public void initData() {
        ContentResolver resolver = getActivity().getContentResolver();
        cursor = resolver.query(Audio.Media.EXTERNAL_CONTENT_URI, new String[]{Audio.Media._ID, Audio.Media.DISPLAY_NAME, Audio.Media.SIZE, Audio.Media.DURATION, Audio.Media.DATA}, null, null, null);
        lv.setAdapter(new AudioListAdapter(getActivity(), cursor));
    }
    @Override
    public void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<AudioBean> list = AudioBean.getAudioInfoList(cursor);
                Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
                intent.putExtra("list",list);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });
    }
}
