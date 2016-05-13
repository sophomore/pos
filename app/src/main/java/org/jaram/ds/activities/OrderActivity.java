package org.jaram.ds.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;

import org.jaram.ds.R;
import org.jaram.ds.fragment.OrderFragment;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class OrderActivity extends BaseActivity<OrderFragment> {

    @Override
    public String getScreenName() {
        return getString(R.string.screen_order);
    }

    @Override
    protected OrderFragment createFragment() {
        return OrderFragment.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableDrawer(GravityCompat.END);
    }
}
