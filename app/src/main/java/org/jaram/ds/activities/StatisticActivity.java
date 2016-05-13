package org.jaram.ds.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import org.jaram.ds.R;
import org.jaram.ds.fragment.BaseFragment;
import org.jaram.ds.fragment.SimpleStatisticFragment;
import org.jaram.ds.fragment.StatisticFragment;
import org.jaram.ds.fragment.StatisticSettingDrawerFragment;

/**
 * Created by jdekim43 on 2016. 5. 10..
 */
public class StatisticActivity extends BaseActivity<SimpleStatisticFragment> {

    private BaseFragment currentFragment;

    @Override
    public String getScreenName() {
        return getString(R.string.screen_statistic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggle:
                if (currentFragment instanceof StatisticFragment) {
                    setSimpleStatisticMode();
                } else {
                    setStatisticMode();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_statistic;
    }

    @Override
    protected SimpleStatisticFragment createFragment() {
        return SimpleStatisticFragment.newInstance();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.settingContainer, StatisticSettingDrawerFragment.newInstance())
                .commit();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerContainer, toolbar,
                R.string.label_statistic_setting, R.string.label_statistic);
        toggle.setDrawerIndicatorEnabled(true);
        drawerContainer.addDrawerListener(toggle);

        setSimpleStatisticMode();
    }

    protected void setFragment(BaseFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit();
        currentFragment = fragment;
    }

    @SuppressWarnings("ConstantConditions")
    private void setStatisticMode() {
        setFragment(StatisticFragment.newInstance());
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        enableDrawer(GravityCompat.START);
    }

    @SuppressWarnings("ConstantConditions")
    private void setSimpleStatisticMode() {
        setFragment(SimpleStatisticFragment.newInstance());
        toolbar.setNavigationIcon(null);
        disableDrawer(GravityCompat.START);
    }
}
