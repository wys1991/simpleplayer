package com.wys.wysplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wys.wysplayer.R;
import com.wys.wysplayer.bean.VedioBean;
import com.wys.wysplayer.utils.StringUtil;


/**
 * create by wys
 * on2019/4/9,11:13
 * Emial : 424126544@qq.com
 */
public class VedioListAdapter extends CursorAdapter {
    private View view;

    public VedioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.adapter_list_item, null);
        view.setTag(new ViewHolder());
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        VedioBean bean = VedioBean.getVedioBean(cursor);
        holder.tv_duration.setText(StringUtil.formatTime(bean.getDuration()));
        holder.tv_title.setText(bean.getTitle());
        holder.tv_size.setText(Formatter.formatFileSize(context, bean.getSize()));
    }

    public class ViewHolder {
        private TextView tv_duration, tv_title, tv_size;

        public ViewHolder() {
            tv_duration = view.findViewById(R.id.tv_time);
            tv_size = view.findViewById(R.id.tv_size);
            tv_title = view.findViewById(R.id.tv_title);
        }
    }
}
