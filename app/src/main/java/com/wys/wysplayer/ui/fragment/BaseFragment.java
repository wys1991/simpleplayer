package com.wys.wysplayer.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wys.wysplayer.ui.UiInterface;

/**
 * create by wys
 * on2019/4/8,8:32
 * Emial : 424126544@qq.com
 */
public abstract class BaseFragment extends Fragment implements UiInterface {

    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayout(), container);
        return mView;
    }

    public View findViewByid(int resId) {
        return mView.findViewById(resId);
    }
}
