package org.jaram.ds.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.fragment.BaseFragment;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

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

    @Nullable @BindView(R.id.drawerContainer) DrawerLayout drawerContainer;
    @Nullable @BindView(R.id.navigator) ViewGroup navigatorContainer;

    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    private ProgressDialog progressDialog;

    @BindColor(R.color.accent) int progressColor;
    @BindString(R.string.message_wait) String defaultProgressDialogMessage;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
        initToolbar();
        initProgressDialog();
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

    protected void initToolbar() {
        if (toolbar == null) {
            return;
        }
//        toolbar.setTitle(getScreenName());
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(createProgressDrawable());
        progressDialog.setMessage(defaultProgressDialogMessage);
    }

    protected Drawable createProgressDrawable() {
        return new CircularProgressDrawable.Builder(this)
                .color(progressColor)
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .build();
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

    public void showProgress() {
        progressDialog.show();
    }

    public void hideProgress() {
        progressDialog.dismiss();
    }

    public void setProgressMessage(CharSequence message) {
        progressDialog.setMessage(message);
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
        if (this instanceof MenuManageActivity) {
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
        if (drawerContainer != null) {
            drawerContainer.closeDrawer(GravityCompat.END);
        }
    }

    protected void startOrderManageActivity() {
        if (this instanceof OrderManageActivity) {
            return;
        }

        startActivity(new Intent(this, OrderManageActivity.class));
        if (drawerContainer != null) {
            drawerContainer.closeDrawer(GravityCompat.END);
        }
    }

    protected void startStatisticActivity() {
        if (this instanceof StatisticActivity) {
            return;
        }

        startActivity(new Intent(this, StatisticActivity.class));
        if (drawerContainer != null) {
            drawerContainer.closeDrawer(GravityCompat.END);
        }
    }

    protected void startMenuManageActivity() {
        if (this instanceof MenuManageActivity) {
            return;
        }

        startActivity(new Intent(this, MenuManageActivity.class));
        if (drawerContainer != null) {
            drawerContainer.closeDrawer(GravityCompat.END);
        }
    }

    protected void startSettingActivity() {
        Toast.makeText(this, R.string.message_yet, Toast.LENGTH_SHORT).show();
//        if (this instanceof OrderActivity) {
//            return;
//        }
//
//        startActivity(new Intent(this, OrderActivity.class));
//        if (drawerContainer != null) {
//            drawerContainer.closeDrawer(GravityCompat.END);
//        }
    }
}