package org.jaram.ds.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import org.jaram.ds.R;

import butterknife.BindView;
import rx.functions.Action0;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class SplashFragment extends BaseFragment {

    @BindView(R.id.notice) TextView noticeView;

    private Action0 onCreatedListener;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (onCreatedListener != null) {
            onCreatedListener.call();
        }
    }

    public void setOnCreatedListener(Action0 listener) {
        this.onCreatedListener = listener;
    }

    public void setNoticeMessage(@StringRes int message) {
        noticeView.setText(message);
    }

    public void setNoticeMessage(CharSequence message) {
        noticeView.setText(message);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_intro;
    }

    @Override
    protected void setupLayout(View view) {
    }
}
