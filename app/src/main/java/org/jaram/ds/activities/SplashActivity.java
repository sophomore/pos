package org.jaram.ds.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;

import org.jaram.ds.R;
import org.jaram.ds.fragment.SplashFragment;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class SplashActivity extends BaseActivity<SplashFragment> {

    @Override
    public String getScreenName() {
        return getString(R.string.screen_splash);
    }

    @Override
    protected SplashFragment createFragment() {
        return SplashFragment.newInstance();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        disableDrawer(GravityCompat.END);
    }
}