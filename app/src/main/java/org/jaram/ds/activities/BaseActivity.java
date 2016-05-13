package org.jaram.ds.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public abstract class BaseActivity<FragmentType extends BaseFragment> extends AppCompatActivity {

    protected static final String FRAGMENT_TAG = "single_fragment";

    protected FragmentType fragment;

    protected abstract FragmentType createFragment();

    /**
     * Google Analytics 화면 추적에 사용할 이름을 전달한다.
     * {@link BaseActivity}에서 Toolbar의 기본 타이틀로도 사용한다.
     * 직접 Toolbar의 타이틀을 변경하기 위해선 {@link #setTitle(CharSequence)} 혹은 {@link #setTitle(int)} 메소드를 사용한다.
     *
     * @return 화면 이름
     */
    public abstract String getScreenName();

    @Nullable @Bind(R.id.drawerContainer) DrawerLayout drawerContainer;
    @Nullable @Bind(R.id.navigator) ViewGroup navigatorContainer;

    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;
    private View toolbarProgressView;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
        initToolbar();
        setupNavigator();

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            try {
                fragment = (FragmentType) manager.findFragmentById(R.id.fragment);
            } catch (Exception e) {
                // TODO: unchecked
            }

            if (fragment == null) {
                fragment = createFragment();
            }

            manager.beginTransaction()
                    .add(R.id.fragment, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    protected int getLayoutResourceId() {
        return R.layout.activity_default;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                if (this instanceof MainActivity || this instanceof TempUserActivity) {
//                    return false;
//                }
                onBackPressed();
                return true;
            case R.id.navigator:
                try {
                    drawerContainer.openDrawer(GravityCompat.END);
                } catch (NullPointerException|IllegalArgumentException e) {
                    //do nothing
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        GoogleAnalytics.getInstance(this).reportActivityStart(this);
//        sendScreenName();
//    }

//    protected void sendScreenName() {
//        sendScreenName(getScreenName());
//    }

//    protected void sendScreenName(String screenName) {
//        ZummaApp app = (ZummaApp) getApplication();
//        Tracker tracker = app.getTracker();
//        tracker.setScreenName(screenName);
//        tracker.send(new HitBuilders.ScreenViewBuilder().build());
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        GoogleAnalytics.getInstance(this).reportActivityStop(this);
//    }

    protected void initToolbar() {
        if (toolbar == null) {
            return;
        }
        toolbarProgressView = ButterKnife.findById(toolbar, R.id.toolbarProgress);
//        toolbar.setTitle(getScreenName());
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setToolbarTitle(CharSequence title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    public CharSequence getToolbarTitle() {
        CharSequence title = "";
        if (toolbar != null) {
            title = toolbar.getTitle();
        }

        return title;
    }

    protected void setToolbarTitle(@StringRes int titleId) {
        setToolbarTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (toolbar != null) {
            setToolbarTitle(title);
        } else {
            super.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (toolbar != null) {
            setToolbarTitle(titleId);
        } else {
            super.setTitle(titleId);
        }
    }

    public void showTitleProgress() {
        if (toolbar != null && toolbarProgressView != null) {
            toolbarProgressView.setVisibility(View.VISIBLE);
        } else {
            throw new IllegalStateException("showTitleProgress need to toolbar and toolbarProgressView");
        }
    }

    public void hideTitleProgress() {
        if (toolbar != null && toolbarProgressView != null) {
            toolbarProgressView.setVisibility(View.GONE);
        } else {
            throw new IllegalStateException("showTitleProgress need to toolbar and toolbarProgressView");
        }
    }

    public void enableDrawer(int gravity) {
        if (drawerContainer != null) {
            drawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, gravity);
        }
    }

    public void disableDrawer(int gravity) {
        if (drawerContainer != null) {
            drawerContainer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, gravity);
        }
    }

    protected void setupNavigator() {
        if (navigatorContainer == null) {
            return;
        }

        View orderButton = ButterKnife.findById(navigatorContainer, R.id.order);
        if (this instanceof OrderActivity) {
            orderButton.setSelected(true);
        } else {
            orderButton.setOnClickListener(v -> startOrderActivity());
        }

        View orderManageButton = ButterKnife.findById(navigatorContainer, R.id.orderManage);
        if (this instanceof OrderManageActivity) {
            orderManageButton.setSelected(true);
        } else {
            orderManageButton.setOnClickListener(v -> startOrderManageActivity());
        }

        View statisticButton = ButterKnife.findById(navigatorContainer, R.id.statistic);
        if (this instanceof StatisticActivity) {
            statisticButton.setSelected(true);
        } else {
            statisticButton.setOnClickListener(v -> startStatisticActivity());
        }

        View menuManageButton = ButterKnife.findById(navigatorContainer, R.id.menuManage);
        if (this instanceof OrderActivity) {
            menuManageButton.setSelected(true);
        } else {
            menuManageButton.setOnClickListener(v -> startMenuManageActivity());
        }

        View settingButton = ButterKnife.findById(navigatorContainer, R.id.setting);
//        if (this instanceof OrderActivity) {
//            settingButton.setSelected(true);
//        } else {
            settingButton.setOnClickListener(v -> startSettingActivity());
//        }
    }

    protected void startOrderActivity() {
        if (this instanceof OrderActivity) {
            return;
        }

        startActivity(new Intent(this, OrderActivity.class));
    }

    protected void startOrderManageActivity() {
        if (this instanceof OrderManageActivity) {
            return;
        }

        startActivity(new Intent(this, OrderManageActivity.class));
    }

    protected void startStatisticActivity() {
        if (this instanceof StatisticActivity) {
            return;
        }

        startActivity(new Intent(this, StatisticActivity.class));
    }

    protected void startMenuManageActivity() {
        if (this instanceof MenuManageActivity) {
            return;
        }

        startActivity(new Intent(this, MenuManageActivity.class));
    }

    protected void startSettingActivity() {
        Toast.makeText(this, R.string.message_yet, Toast.LENGTH_SHORT).show();
//        if (this instanceof OrderActivity) {
//            return;
//        }
//
//        startActivity(new Intent(this, OrderActivity.class));
    }
}