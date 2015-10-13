package org.jaram.ds;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import org.jaram.ds.data.Closing;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Category;
import org.jaram.ds.data.struct.Menu;
import org.jaram.ds.data.struct.Order;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Intro extends Activity {
    /*
    * 1. 마감 처리가 되어있는지 확인
    * 2. 마감 처리가 안되어있으면
    * 3. 서버에 변경된 정보가 있는지 확인
    * 4. 변경된 정보가 있으면 데이터 가져오기
    * 5. 주문 관리화면 실행*/

    private TextView noticeView;

    private static boolean isOnline(Context context) { // network 연결 상태 확인
        try {
            ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

//            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile ConnectivityManager.TYPE_MOBILE
//            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
//                return true;
//            }

        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Data.dbCategory = new org.jaram.ds.data.query.Category(Intro.this);
        Data.dbMenu = new org.jaram.ds.data.query.Menu(Intro.this);
        Data.dbOrder = new org.jaram.ds.data.query.Order(Intro.this);
        Data.dbOrderMenu = new org.jaram.ds.data.query.OrderMenu(Intro.this);

        noticeView = (TextView)findViewById(R.id.intro_notice);

        noticeView.setText("앱 실행을 위한 준비를 하고있습니다.");

//        if (isOnline(Intro.this)) {
//            new AlertDialog.Builder(Intro.this, R.style.Base_V21_Theme_AppCompat_Light_Dialog)
//                    .setTitle("경고")
//                    .setMessage("wifi에 연결되어 있지 않습니다. 일부 기능을 사용하실 수 없습니다.")
//                    .setNegativeButton("닫기", null)
//                    .show();
//        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                init();
            }
        }, 500);
    }

    private void startApp() {
        startActivity(new Intent(Intro.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
        finish();
    }

    private void init() {
        ArrayList<Category> categories = Data.dbCategory.getAll();
        for (int i=0; i<categories.size(); i++) {
            Data.categories.put(categories.get(i).getId(), categories.get(i));
        }

        boolean isDoClosing = false;
        ArrayList<Order> result = Data.dbOrder.getAll();
        if (result.size()>0) {
            org.jaram.ds.data.struct.Order order = result.get(0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getDate());
            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            if (calendar.get(Calendar.YEAR)!=today.get(Calendar.YEAR)
                    || calendar.get(Calendar.MONTH)!=today.get(Calendar.MONTH)
                    || calendar.get(Calendar.DAY_OF_MONTH)!=today.get(Calendar.DAY_OF_MONTH)) {
                isDoClosing = true;
                new Closing(Intro.this, new ClosingListener(), noticeView, Closing.VIEW_TEXTVIEW);
            }
        }
        if(!isDoClosing) {
            postClosing();
        }
    }

    private void postClosing() {
        new GetMenu().execute();
    }

    private void endInit() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                noticeView.setText("환영합니다!");
                startApp();
            }
        }, 1000);
    }

    private class ClosingListener implements Closing.Listener {

        @Override
        public void endClosing(boolean isSuccess) {
            postClosing();
        }
    }

    private class GetMenu extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            noticeView.setText("서버에서 메뉴정보를 가져오고 있습니다.");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String recieveData = Http.get(Data.SERVER_URL + "menu", null);
                Data.dbMenu.clear();
                JSONArray menusJSN = new JSONArray(recieveData);
                for (int i=0; i<menusJSN.length(); i++) {
                    JSONObject jo = menusJSN.getJSONObject(i);
                    new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"),
                            Data.categories.get(jo.getInt("category_id"))).create();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            noticeView.setText("메뉴정보를 가져오는데 성공했습니다.");
            endInit();
        }
    }
}