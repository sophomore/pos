package org.jaram.ds;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jaram.ds.data.Data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

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
            actionbar.findViewById(R.id.actionbar_titleBox).setOnClickListener(null);
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
            base_container.closeDrawer(Gravity.RIGHT);
            switch(v.getId()) {
                case R.id.drawer_orderBtn:
                    if (getCurrent() != ORDER) {
                        startActivity(new Intent(Base.this, Order.class));
                    }
                    break;
                case R.id.drawer_manageOrderBtn:
                    if (getCurrent() != MANAGE_ORDER && getCurrent() != ORDER) {
                        startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
                        finish();
                    }
                    break;
                case R.id.drawer_statisticBtn:
                    if (getCurrent() != STATISTIC && getCurrent() != TAX && getCurrent() != ORDER) {
                        startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.TAX));
                        finish();
                    }
                    break;
                case R.id.drawer_manageMenuBtn:
                    if (getCurrent() != MANAGE_MENU && getCurrent() != ORDER) {
                        startActivity(new Intent(Base.this, Admin.class).putExtra("view", Base.MANAGE_MENU));
                        finish();
                    }
                    break;
                case R.id.drawer_settingBtn:
                    new AlertDialog.Builder(Base.this)
                            .setTitle("설정")
                            .setItems(new String[]{"서버 주소 설정", "주문 내역 가져오기", "주문 내역 내보내기"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch(which) {
                                        case 0:
                                            final EditText ed = new EditText(Base.this);
                                            ed.setText(Data.pref.getString("url", ""));
                                            LinearLayout layout = new LinearLayout(Base.this);
                                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                            layoutParams.setMargins(48, 0, 48, 0);
                                            layout.addView(ed, layoutParams);
                                            new AlertDialog.Builder(Base.this)
                                                    .setTitle("서버 주소 설정")
                                                    .setMessage("서버의 주소와 포트를 'url:port' 형식으로 입력해주세요.")
                                                    .setView(layout)
                                                    .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            SharedPreferences.Editor editor = Data.pref.edit();
                                                            editor.putString("url", "http://"+ed.getText().toString()+"/");
                                                            editor.apply();
                                                            Log.d("setting", ed.getText().toString());
                                                        }
                                                    })
                                                    .setNegativeButton("취소", null)
                                                    .show();
                                            break;
                                        case 1:
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                            intent.setType("*/*");
                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                                            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 0);
                                            break;
                                        case 2:
                                            break;
                                    }
                                }
                            })
                            .setNegativeButton("닫기", null)
                            .show();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String addr = Data.SERVER_URL+"file/input";
                    RequestParams params = new RequestParams();
                    try {
                        File file = new File(uri.getPath());
                        Log.d("base", file.getAbsolutePath());
                        params.put("file", file, "multipart/form-data");
                    } catch(FileNotFoundException e) {}

// send request
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(addr, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                            // handle success response
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                            // handle failure response
                            Log.d("base", statusCode+"");
                            for (int i=0; i<headers.length; i++) {
                                Log.d("base", headers[i].getName() + " : " + headers[i].getValue());
                            }
                        }
                    });
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}