package org.jaram.ds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
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

        if (getCurrent() != ORDER) drawer_orderBtn.setOnClickListener(listener);
        if (getCurrent() != MANAGE_ORDER) drawer_manageOrderBtn.setOnClickListener(listener);
        if (getCurrent() != STATISTIC || getCurrent() != TAX) drawer_statisticBtn.setOnClickListener(listener);
        if (getCurrent() != MANAGE_MENU) drawer_manageMenuBtn.setOnClickListener(listener);
        drawer_settingBtn.setOnClickListener(listener);
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

    private class BtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.drawer_orderBtn: //TODO: 변경된 사항이 있는지 확인 및 처리 후 주문화면 보여주기
                    startActivity(new Intent(Base.this, Order.class));
                    break;
                case R.id.drawer_manageOrderBtn: //TODO: 마감 처리 후 실행
                    startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
                    break;
                case R.id.drawer_statisticBtn: //TODO: 마감 처리 후 실행
                    startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.TAX));
                    break;
                case R.id.drawer_manageMenuBtn: //TODO: 마감 처리 후 실행
                    startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.MANAGE_MENU));
                    break;
                case R.id.drawer_settingBtn: //TODO: 설정화
                    Toast.makeText(getApplicationContext(), "설정", Toast.LENGTH_SHORT).show();
                    break;
            }
            finish();
        }
    }
}