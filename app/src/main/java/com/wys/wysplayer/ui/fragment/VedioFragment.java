package com.wys.wysplayer.ui.fragment;

import android.widget.TextView;

import com.wys.wysplayer.R;
import com.wys.wysplayer.utils.ToastUtil;

/**
 * create by wys
 * on2019/4/8,9:37
 * Emial : 424126544@qq.com
 */
public class VedioFragment extends BaseFragment {
    @Override
    public int getLayout() {
        return R.layout.fragment_vedio;
    }

    @Override
    public void initView() {
        TextView tv_vedio = (TextView) findViewByid(R.id.tv_vedio);
    }

    @Override
    public void initData() {
        ToastUtil.showToast("VedioFragment");
    }

    @Override
    public void initListener() {

    }
}
