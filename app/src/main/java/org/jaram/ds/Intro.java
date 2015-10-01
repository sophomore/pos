package org.jaram.ds;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.*;
import org.jaram.ds.data.struct.Order;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

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

    Realm db;

    private TextView noticeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        noticeView = (TextView)findViewById(R.id.intro_notice);

        noticeView.setText("앱 실행을 위한 준비를 하고있습니다.");
        init();
        startApp();
//        try {
//            JSONArray ja = new JSONArray(Http.get(Data.SERVER_URL+"menu", null));
//            for (int i=0; i<ja.length(); i++) {
//                JSONObject jo = ja.getJSONObject(i);
//                new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(jo.getInt("category_id")));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void startApp() {
        startActivity(new Intent(Intro.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
        finish();
    }

    private void init() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Realm db = Realm.getInstance(Intro.this);

        db.beginTransaction();
        db.where(Order.class).findAll().clear(); //TODO
        db.where(OrderMenu.class).findAll().clear();//TODO
        db.where(Category.class).findAll().clear();
        Data.categories.put(1, db.createObjectFromJson(Category.class, "{\"name\": \"돈까스\", \"id\": 1}"));
        Data.categories.put(2, db.createObjectFromJson(Category.class, "{\"name\": \"덮밥\", \"id\": 2}"));
        Data.categories.put(3, db.createObjectFromJson(Category.class, "{\"name\": \"면류\", \"id\": 3}"));
        Data.categories.put(4, db.createObjectFromJson(Category.class, "{\"name\": \"기타\", \"id\": 4}"));
        db.commitTransaction();

        RealmQuery<org.jaram.ds.data.struct.Order> query = db.where(org.jaram.ds.data.struct.Order.class);
        RealmResults<org.jaram.ds.data.struct.Order> result = query.findAllSorted("date", false);
        if (result.size()>0) {
            org.jaram.ds.data.struct.Order order = result.get(0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getDate());
            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            if (calendar.get(Calendar.YEAR)!=today.get(Calendar.YEAR)
                    || calendar.get(Calendar.MONTH)!=today.get(Calendar.MONTH)
                    || calendar.get(Calendar.DAY_OF_MONTH)!=today.get(Calendar.DAY_OF_MONTH)) {
                close();
            }
        }

        noticeView.setText("서버에서 데이터를 가져오고 있습니다.");

        //set menu
        db.beginTransaction();
        db.where(Menu.class).findAll().clear();
        try {
            JSONArray menusJSN = new JSONArray(Http.get(Data.SERVER_URL + "menu", null));
            for (int i=0; i<menusJSN.length(); i++) {
                JSONObject jo = menusJSN.getJSONObject(i);
                Menu menu = db.createObjectFromJson(Menu.class, jo);
                menu.setCategory(Data.categories.get(jo.getInt("category_id")));
                Data.menus.put(jo.getInt("id"), menu);
                menu.getCategory().getMenus().add(menu);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.commitTransaction();
        noticeView.setText("환영합니다!");
    }

    private void close() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        noticeView.setText("서버에 로컬에 저장된 주문을 전송합니다.");

        Realm db = Realm.getInstance(Intro.this);
        RealmQuery<org.jaram.ds.data.struct.Order> orderQuery = db.where(org.jaram.ds.data.struct.Order.class);
        RealmResults<org.jaram.ds.data.struct.Order> queryResult = orderQuery.findAll();
        boolean isAllSuccess = true;
        for (int i=0; i<queryResult.size(); i++) {
            org.jaram.ds.data.struct.Order order = queryResult.get(i);
            HashMap<String, Object> param = new HashMap<>();
            param.put("time", new SimpleDateFormat().format(order.getDate()));
            param.put("totalprice", order.getTotalprice());
            param.put("ordermenus", order.getManager().getOrdermenusAtJson());
            boolean isSuccess = true;
            try {
                int count = 0;
                JSONObject result = null;
                do {
                    result = new JSONObject(Http.post(Data.SERVER_URL+"order", param));
                    count++;
                    if (count > 5) {
                        isSuccess = false;
                        break;
                    }
                } while(result.getString("result").equals("error"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isSuccess) {
                db.beginTransaction();
                order.removeFromRealm();
                db.commitTransaction();
            }
            else {
                isAllSuccess = false;
            }
        }
        if (!isAllSuccess) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("오류")
                    .setMessage("서버에 주문 정보를 전송하는 도중 오류가 발생했습니다.\n재시도 하시겠습니까?(취소를 누르시면 다음 마감 때 재시도하며 그 전까지는 통계에 반영되지 않으며 주문목록에 잘못 표시될 수 있습니다.")
                    .setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            close();
                        }
                    })
                    .setNegativeButton("닫기", null)
                    .show();
        }
    }

    private class InitTask extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            Realm db = Realm.getInstance(Intro.this);
            RealmQuery<org.jaram.ds.data.struct.Order> query = db.where(org.jaram.ds.data.struct.Order.class);
            RealmResults<org.jaram.ds.data.struct.Order> result = query.findAllSorted("date", false);
            org.jaram.ds.data.struct.Order order = result.get(0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getDate());
            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            if (calendar.get(Calendar.YEAR)!=today.get(Calendar.YEAR)
                    || calendar.get(Calendar.MONTH)!=today.get(Calendar.MONTH)
                    || calendar.get(Calendar.DAY_OF_MONTH)!=today.get(Calendar.DAY_OF_MONTH)) {
                new CloseTask().execute();
            }
            noticeView.setText("서버에서 데이터를 가져오고 있습니다.");
            //TODO: 날짜가 다르면 마감작업을 함. 같으면 바로 데이터를 가져오고 실행.
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            startApp();
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            noticeView.setText("저장된 주문을 서버로 전송하고 있습니다.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Realm db = Realm.getInstance(Intro.this);
            RealmQuery<org.jaram.ds.data.struct.Order> orderQuery = db.where(org.jaram.ds.data.struct.Order.class);
            RealmResults<org.jaram.ds.data.struct.Order> queryResult = orderQuery.findAll();
            boolean isAllSuccess = true;
            for (int i=0; i<queryResult.size(); i++) {
                org.jaram.ds.data.struct.Order order = queryResult.get(i);
                HashMap<String, Object> param = new HashMap<>();
                param.put("time", new SimpleDateFormat().format(order.getDate()));
                param.put("totalprice", order.getTotalprice());
                param.put("ordermenus", order.getManager().getOrdermenusAtJson());
                boolean isSuccess = true;
                try {
                    int count = 0;
                    JSONObject result = null;
                    do {
                        result = new JSONObject(Http.post(Data.SERVER_URL+"order", param));
                        count++;
                        if (count > 5) {
                            isSuccess = false;
                            break;
                        }
                    } while(result.getString("result").equals("error"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess) {
                    db.beginTransaction();
                    order.removeFromRealm();
                    db.commitTransaction();
                }
                else {
                    isAllSuccess = false;
                }
            }
            return isAllSuccess;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("오류")
                        .setMessage("서버에 주문 정보를 전송하는 도중 오류가 발생했습니다.\n재시도 하시겠습니까?(취소를 누르시면 다음 마감 때 재시도하며 그 전까지는 통계에 반영되지 않으며 주문목록에 잘못 표시될 수 있습니다.")
                        .setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CloseTask.this.execute();
                            }
                        })
                        .setNegativeButton("닫기", null)
                        .show();
                return;
            }
            noticeView.setText("주문정보를 서버에 정상적으로 전송하였습니다.");
        }
    }
}