package org.jaram.ds.activities;

import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;

import org.jaram.ds.R;
import org.jaram.ds.fragment.OrderManageFragment;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class OrderManageActivity extends BaseActivity<OrderManageFragment> {

    @Override
    public String getScreenName() {
        return getString(R.string.screen_order_manage);
    }

    @Override
    protected OrderManageFragment createFragment() {
        return OrderManageFragment.newInstance();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initToolbar() {
        super.initToolbar();
        toolbar.setNavigationIcon(null);
        toolbar.setTitleTextColor(Color.WHITE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_manage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
