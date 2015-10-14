package org.jaram.ds;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public abstract class Base extends FragmentActivity {

    public static final int ORDER = 1;
    public static final int MANAGE_ORDER = 2;
    public static final int STATISTIC = 3;
    public static final int TAX = 4;
    public static final int MANAGE_MENU = 5;

    protected RelativeLayout actionbar;
    protected DrawerLayout base_container;

    protected ImageButton open_rightDrawerBtn;

    protected Button drawer_orderBtn;
    protected Button drawer_manageOrderBtn;
    protected Button drawer_statisticBtn;
    protected Button drawer_manageMenuBtn;
    protected Button drawer_settingBtn;

    abstract int getCurrent();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base);

        actionbar = (RelativeLayout)findViewById(R.id.actionbarBox);
        base_container = (DrawerLayout)findViewById(R.id.base_container);
        open_rightDrawerBtn = (ImageButton)findViewById(R.id.open_rightDrawerBtn);

        open_rightDrawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base_container.openDrawer(Gravity.RIGHT);
            }
        });

        drawer_orderBtn = (Button)findViewById(R.id.drawer_orderBtn);
        drawer_manageOrderBtn = (Button)findViewById(R.id.drawer_manageOrderBtn);
        drawer_statisticBtn = (Button)findViewById(R.id.drawer_statisticBtn);
        drawer_manageMenuBtn = (Button)findViewById(R.id.drawer_manageMenuBtn);
        drawer_settingBtn = (Button)findViewById(R.id.drawer_settingBtn);

        BtnListener listener = new BtnListener();

        drawer_orderBtn.setOnClickListener(listener);
        drawer_manageOrderBtn.setOnClickListener(listener);
        drawer_statisticBtn.setOnClickListener(listener);
        drawer_manageMenuBtn.setOnClickListener(listener);
        drawer_settingBtn.setOnClickListener(listener);
    }

    protected void doneAttatch() {
        switch(getCurrent()) {
            case MANAGE_ORDER:
                drawer_manageOrderBtn.setSelected(true);
                break;
            case MANAGE_MENU:
                drawer_manageMenuBtn.setSelected(true);
                break;
            case ORDER:
                drawer_orderBtn.setSelected(true);
                break;
            case STATISTIC:
                drawer_statisticBtn.setSelected(true);
                break;
            case TAX:
                drawer_statisticBtn.setSelected(true);
                break;
        }
    }

    private FrameLayout drawerView;

    protected void setLeftDrawer(FrameLayout view) {
        view.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.MATCH_PARENT, Gravity.START));
        base_container.addView(view);
        base_container.findViewById(R.id.open_leftDrawerBtn).setVisibility(View.VISIBLE);
        base_container.findViewById(R.id.actionbar_titleBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                base_container.openDrawer(Gravity.LEFT);
            }
        });
        drawerView = view;
    }

    protected void resetLeftDrawer() {
        if (drawerView != null) {
            base_container.removeView(drawerView);
            base_container.findViewById(R.id.open_leftDrawerBtn).setOnClickListener(null);
            base_container.findViewById(R.id.open_leftDrawerBtn).setVisibility(View.GONE);
        }
    }

    protected void addViewAtActionBar(View view, ViewGroup.LayoutParams params) {
        view.setLayoutParams(params);
        actionbar.addView(view);
    }

    protected void addButtonAtActionBar(View view) {
        LinearLayout container = (LinearLayout)actionbar.findViewById(R.id.btns);
        container.addView(view, 0);
    }

    boolean isExitProgress = false;
    @Override
    public void onBackPressed() {
        if (isExitProgress) {
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "앱을 종료하시려면 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show();
            isExitProgress = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isExitProgress = false;
                }
            }, 1500);
        }
    }

    private class BtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.drawer_orderBtn:
                    if (getCurrent() != ORDER) {
                        startActivity(new Intent(Base.this, Order.class));
                        finish();
                    }
                    break;
                case R.id.drawer_manageOrderBtn:
                    if (getCurrent() != MANAGE_ORDER) {
                        startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
                        finish();
                    }
                    break;
                case R.id.drawer_statisticBtn:
                    if (getCurrent() != STATISTIC && getCurrent() != TAX) {
                        startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.TAX));
                        finish();
                    }
                    break;
                case R.id.drawer_manageMenuBtn:
                    if (getCurrent() != MANAGE_MENU) {
                        startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.MANAGE_MENU));
                        finish();
                    }
                    break;
                case R.id.drawer_settingBtn: //TODO: 설정화면
//                    Toast.makeText(getApplicationContext(), "설정", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    }
}