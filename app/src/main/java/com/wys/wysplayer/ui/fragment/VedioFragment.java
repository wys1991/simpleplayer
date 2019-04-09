package com.wys.wysplayer.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wys.wysplayer.R;
import com.wys.wysplayer.adapter.VedioListAdapter;
import com.wys.wysplayer.bean.VedioBean;
import com.wys.wysplayer.ui.activity.VideoPlayerActivity;
import com.wys.wysplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * create by wys
 * on2019/4/8,10:37
 * Emial : 424126544@qq.com
 */
public class VedioFragment extends BaseFragment {
    private static final String TAG = "VideoFragment";
    private ListView lv;
    private Cursor cursor;

    @Override
    public int getLayout() {
        return R.layout.fragment_vedio;
    }

    @Override
    public void initView() {
        lv = (ListView) findViewByid(R.id.lv);
    }

    @Override
    public void initData() {
        LogUtil.LogI(TAG, "initData");
        //利用内容提供者查询多媒体数据库里的数据
        ContentResolver resolver = getActivity().getContentResolver();
        //查询的时候必须写_id列，不写报无效的参数异常
        cursor = resolver.query(Media.EXTERNAL_CONTENT_URI,
                new String[]{Media._ID, Media.DATA, Media.TITLE, Media.DURATION, Media.SIZE}, null, null, null);
        //设置数据适配
        VedioListAdapter videoListAdapter = new VedioListAdapter(getActivity(), cursor);
        lv.setAdapter(videoListAdapter);
    }

    @Override
    public void initListener() {
        //设置条目点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<VedioBean> list = VedioBean.getListFromCursor(cursor);
                Intent intent = new Intent();
                intent.setClass(getActivity(), VideoPlayerActivity.class);
//                intent.setClass(getActivity(), VitamioVideoPlayerActivity.class);
                intent.putExtra("list", list);
                intent.putExtra("pos", position);

                startActivity(intent);
            }
        });
        LogUtil.LogI(TAG, "initListener");
    }
}
