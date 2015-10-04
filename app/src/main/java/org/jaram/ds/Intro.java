package org.jaram.ds;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Category;
import org.jaram.ds.data.struct.Menu;
import org.jaram.ds.data.struct.Order;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        new Handler().postDelayed(new Runnable() {
            public void run() {
                init();
            }
        }, 1500);
    }

    private void startApp() {
        startActivity(new Intent(Intro.this, Admin.class).putExtra("view", Base.MANAGE_ORDER));
        finish();
    }

    private void init() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ArrayList<Category> categories = Data.dbCategory.getAll();
        for (int i=0; i<categories.size(); i++) {
            Data.categories.put(categories.get(i).getId(), categories.get(i));
        }

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
                close();
            }
        }

        noticeView.setText("서버에서 데이터를 가져오고 있습니다.");
        //set menu
        Log.d("intro", Data.categories.toString());
        Data.dbMenu.clear();
        try {
            JSONArray menusJSN = new JSONArray(Http.get(Data.SERVER_URL + "menu", null));
            for (int i=0; i<menusJSN.length(); i++) {
                JSONObject jo = menusJSN.getJSONObject(i);
                new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"),
                        Data.categories.get(jo.getInt("category_id"))).create();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        noticeView.setText("환영합니다!");
        startApp();
    }

    private void close() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        noticeView.setText("서버에 로컬에 저장된 주문을 전송합니다.");

        ArrayList<Order> queryResult = Data.dbOrder.getAll();
        boolean isAllSuccess = true;
        for (int i=0; i<queryResult.size(); i++) {
            org.jaram.ds.data.struct.Order order = queryResult.get(i);
            HashMap<String, Object> param = new HashMap<>();
            param.put("time", new SimpleDateFormat().format(order.getDate()));
            param.put("totalprice", order.getTotalprice());
            param.put("ordermenus", order.getOrdermenusAtJson());
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
                order.putDB();
            }
            else {
                isAllSuccess = false;
            }
        }
        if (!isAllSuccess) {
            new AlertDialog.Builder(Intro.this, R.style.Base_V21_Theme_AppCompat_Light_Dialog)
                    .setTitle("오류")
                    .setMessage("서버에 주문 정보를 전송하는 도중 오류가 발생했습니다.\n재시도 하시겠습니까?" +
                            "(취소를 누르시면 다음 마감 때 재시도하며 그 전까지는 통계에 반영되지 않으며 주문목록에 잘못 표시될 수 있습니다.")
                    .setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            close();
                        }
                    })
                    .setNegativeButton("닫기", null)
                    .setCancelable(false)
                    .show();
        }
    }
}