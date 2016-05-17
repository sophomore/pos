package org.jaram.ds.activities;

import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;

import org.jaram.ds.R;
import org.jaram.ds.fragment.MenuManageFragment;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class MenuManageActivity extends BaseActivity<MenuManageFragment> {

    @Override
    protected MenuManageFragment createFragment() {
        return MenuManageFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_menu_manage);
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
        getMenuInflater().inflate(R.menu.menu_manage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
