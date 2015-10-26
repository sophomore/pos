package org.jaram.ds;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.jaram.ds.data.Closing;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.*;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Data.pref = getSharedPreferences("state", 0);
        Data.dbCategory = new org.jaram.ds.data.query.Category(Intro.this);
        Data.dbMenu = new org.jaram.ds.data.query.Menu(Intro.this);
        Data.dbOrder = new org.jaram.ds.data.query.Order(Intro.this);
        Data.dbOrderMenu = new org.jaram.ds.data.query.OrderMenu(Intro.this);

        noticeView = (TextView)findViewById(R.id.intro_notice);

        noticeView.setText("앱 실행을 위한 준비를 하고있습니다.");
        Log.d("intro", "before init");

        new Handler().postDelayed(new Runnable() {
            public void run() {
                init();
            }
        }, 500);
    }

    private void startApp() {
        Log.d("intro", Boolean.toString(Data.pref.getBoolean("network", false)));
        startActivity(new Intent(Intro.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
        finish();
    }

    private void init() {
        Log.d("intro", "init");
        final SharedPreferences.Editor ed = Data.pref.edit();
        ed.putBoolean("network", true);
        ed.apply();
        ArrayList<Category> categories = Data.dbCategory.getAll();
        for (int i=0; i<categories.size(); i++) {
            Data.categories.put(categories.get(i).getId(), categories.get(i));
        }
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Log.d("intro", "process");
                try {
                    Log.d("intro", Http.get(Data.SERVER_URL, null));
                } catch (IOException e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    Log.d("intro", "success");
                    new GetMenu().execute();
                }
                else {
                    Log.d("intro", "fail");
                    noticeView.setText("서버에 접속할 수 없습니다.");
                    ed.putBoolean("network", false);
                    ed.apply();
                    loadSavedData();
                    endInit();
                }
            }

            private void loadSavedData() {
                ArrayList<Menu> menuJSN = Data.dbMenu.getAll();
                for(Menu menu : menuJSN) {
                    menu.regist();
                }
            }
        }.execute();
    }

    private void postGetMenu() {
        ArrayList<org.jaram.ds.data.struct.Order> orders = Data.dbOrder.getAll();
        if (orders.size()>0) {
            new Closing(Intro.this, new ClosingListener(), noticeView, Closing.VIEW_TEXTVIEW);
        }
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
            postGetMenu();
        }
    }

    private class GetMenu extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            noticeView.setText("서버에서 메뉴정보를 가져오고 있습니다.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
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
                return false;
            } catch (IOException e) {
                SharedPreferences.Editor ed = Data.pref.edit();
                ed.putBoolean("network", false);
                ed.apply();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            noticeView.setText("메뉴정보를 성공적으로 가져왔습니다.");
            endInit();
        }
    }
}