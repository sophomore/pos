package org.jaram.ds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.madx.updatechecker.lib.UpdateRunnable;

import org.jaram.ds.data.Closing;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.*;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

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

        new Handler().postDelayed(new Runnable() {
            public void run() {
                chkNewVersion();
            }
        }, 500);
    }

    private void chkNewVersion() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                noticeView.setText("앱의 새로운 버전을 확인하고 있습니다");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return web_update();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    noticeView.setText("새로운 버전이 있습니다");
                    new AlertDialog.Builder(Intro.this)
                            .setTitle("알림")
                            .setMessage("새 버전이 있습니다. 업데이트를 하시겠습니까?")
                            .setPositiveButton("업데이트 하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.jaram.ds")));
                                }
                            })
                            .setNegativeButton("나중에", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    noticeView.setText("앱 실행을 위한 준비를 하고있습니다.");
                                    init();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                else {
                    noticeView.setText("앱 실행을 위한 준비를 하고있습니다.");
                    init();
                }
            }
        }.execute();
    }

    private boolean web_update(){
        try {
            String curVersion = getApplicationContext().getPackageManager().getPackageInfo("org.jaram.ds", 0).versionName;
            String newVersion = curVersion;
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=org.jaram.ds&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
            return value(curVersion) < value(newVersion);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private long value(String string) {
        string = string.trim();
        if( string.contains( "." )){
            final int index = string.lastIndexOf( "." );
            return value( string.substring( 0, index ))* 100 + value( string.substring( index + 1 ));
        }
        else {
            return Long.valueOf( string );
        }
    }

    private void startApp() {
        Log.d("intro", Boolean.toString(Data.pref.getBoolean("network", false)));
        startActivity(new Intent(Intro.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
        finish();
    }

    private void init() {
        Data.SERVER_URL = Data.pref.getString("url", "http://192.168.0.101:80/");
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
            endInit();
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
            if (Data.dbOrder.getAll().size() > 0) {
                new Closing(getApplicationContext(), new ClosingListener(), noticeView, Closing.VIEW_TEXTVIEW);
            }
            else {
                endInit();
            }
        }
    }
}