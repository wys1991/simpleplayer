package com.wys.wysplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wys.wysplayer.R;
import com.wys.wysplayer.bean.AudioBean;
import com.wys.wysplayer.utils.StringUtil;


/**
 * create by wys
 * on2019/4/9,11:13
 * Emial : 424126544@qq.com
 */
public class AudioListAdapter extends CursorAdapter {
    private View view;

    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        view = View.inflate(context, R.layout.adapter_audio_list_item, null);
        view.setTag(new ViewHolder());
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        AudioBean audioBean = AudioBean.getAudioBean(cursor);
        holder.tvSize.setText(Formatter.formatFileSize(context, audioBean.getSize()));
        holder.tvTime.setText(StringUtil.formatTime(audioBean.getDuration()));
        holder.tvTitle.setText(audioBean.getTitle());
    }

    class ViewHolder {
        TextView tvSize, tvTime, tvTitle;

        public ViewHolder() {
            tvSize = (TextView) view.findViewById(R.id.tv_size);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }
}
