package org.jaram.ds.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.adapter.OrderDetailMenuAdapter;
import org.jaram.ds.adapter.OrderListAdapter;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.*;
import org.jaram.ds.dialog.OrderSearch;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class OrderManager extends Fragment implements OrderSearch.Callbacks {

    OrderListAdapter adapter = null;
    ArrayList<org.jaram.ds.data.struct.Order> orders = null;

    ListView orderList;
    TextView totalpriceView;
    TextView dateView;
    OrderDetailMenuAdapter orderDetailAdapter = null;
    ArrayList<OrderMenu> ordermenus = null;

    ImageButton moreBtn;

    HashMap<Integer, Menu> menus = null;

    ProgressDialog dialog = null;

    Callbacks callbacks;

    boolean isEnableBtn = false;

    Date lastDate = null;

    TextView today_total;

    private static OrderManager view;
    public static OrderManager getInstance() {
        if (view == null) {
            view = new OrderManager();
        }
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_manager, container, false);

        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);

        orders = new ArrayList<org.jaram.ds.data.struct.Order>();
        adapter = new OrderListAdapter(orders, getActivity());

        menus = new HashMap<>();

        orderList = (ListView)view.findViewById(R.id.orderList);

        totalpriceView = (TextView)view.findViewById(R.id.totalprice);
        dateView = (TextView)view.findViewById(R.id.date);

        orderList.setAdapter(adapter);
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                org.jaram.ds.data.struct.Order order = orders.get(position);
                ordermenus.clear();
                ordermenus.addAll(order.getOrdermenus());
                orderDetailAdapter.notifyDataSetChanged();
                totalpriceView.setText("총 " + order.getTotalprice() + "원");
                dateView.setText(new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA).format(order.getDate()));
                adapter.setCurrentSelected(position);
                adapter.notifyDataSetChanged();
            }
        });
        moreBtn = new ImageButton(getActivity());
        moreBtn.setImageResource(R.drawable.ic_arrow_drop_down_black_48dp);
        moreBtn.setBackgroundResource(R.drawable.white_btn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Data.pref.getBoolean("network", false)) {
                    new GetOrder(getActivity()).execute();
                }
            }
        });
        orderList.addFooterView(moreBtn);

//        orderList.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    final int lastItem = firstVisibleItem + visibleItemCount;
//                    if(lastItem == totalItemCount){
//                        if (Data.pref.getBoolean("network", false)) {
//                            new GetOrder(getActivity()).execute();
//                        }
//                    }
//            }
//        });

        ordermenus = new ArrayList<OrderMenu>();
        orderDetailAdapter = new OrderDetailMenuAdapter(ordermenus, getActivity()) {

            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        };

        ListView ordermenuList = (ListView)view.findViewById(R.id.ordermenuDetailList);
        ordermenuList.setAdapter(orderDetailAdapter);

        ImageButton orderBtn = (ImageButton)view.findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOrder();
            }
        });

        ((Button)view.findViewById(R.id.printReceiptBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableBtn && Data.pref.getBoolean("network", false)) {
                    org.jaram.ds.data.struct.Order.print_receipt(orders.get(adapter.getCurrentSelected()).getId());
                }
            }
        });
        ((Button)view.findViewById(R.id.printStatementBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableBtn && Data.pref.getBoolean("network", false)) {
                    org.jaram.ds.data.struct.Order.print_statement(orders.get(adapter.getCurrentSelected()).getId());
                }
            }
        });
        ((Button)view.findViewById(R.id.deleteBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableBtn) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("주문 삭제")
                            .setMessage("정말로 주문을 삭제하시겠습니까")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    org.jaram.ds.data.struct.Order.delete(orders.get(adapter.getCurrentSelected()).getId());
                                    refresh();
                                }
                            })
                            .setNegativeButton("아니오", null)
                            .show();
                }
            }
        });
        final OrderSearch searchView = new OrderSearch(OrderManager.this, menus);
        searchView.setCancelable(false);

        ImageButton searchBtn = new ImageButton(getActivity());
        searchBtn.setImageResource(R.drawable.ic_search_white_24dp);
        searchBtn.setBackgroundResource(R.drawable.default_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.show();
            }
        });
        callbacks.addActionBarBtn(searchBtn);

        RelativeLayout actionbaritem = (RelativeLayout)LayoutInflater.from(getActivity()).inflate(R.layout.ordermanager_total, null, false);

        today_total = (TextView)actionbaritem.findViewById(R.id.today_total);

        new SetTodayTotal().execute();

        callbacks.addViewAtActionBar(actionbaritem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return view;
    }

    private void newOrder() {
        startActivity(new Intent(getActivity(), org.jaram.ds.Order.class));
    }

    private void refresh() {
        orders.clear();
        lastDate = null;
        orders.addAll(Data.dbOrder.getAll());
        adapter.notifyDataSetChanged();

        if (Data.pref.getBoolean("network", false)) {
            new GetAllMenuList().execute();
            isEnableBtn = true;
            moreBtn.setClickable(true);
        }
        else {
            if (orders.size() > 0) {
                doSelectFirstItem();
            }
            isEnableBtn = false;
            moreBtn.setClickable(false);
        }
    }

    private void doSelectFirstItem() {
        orderList.setSelection(0);
        org.jaram.ds.data.struct.Order order = orders.get(0);
        ordermenus.clear();
        ordermenus.addAll(order.getOrdermenus());
        orderDetailAdapter.notifyDataSetChanged();
        totalpriceView.setText("총 " + order.getTotalprice() + "원");
        dateView.setText(new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA).format(order.getDate()));
        adapter.setCurrentSelected(0);
        adapter.notifyDataSetChanged();
    }

    HashMap<String, Object> searchParam = new HashMap<>();

    @Override
    public void applySearchResult(Calendar startDate, Calendar endDate, ArrayList<Menu> menus, boolean cash, boolean card, boolean service, boolean credit) {
        if (Data.pref.getBoolean("network", false)) {
            searchParam.put("startDate", Data.onlyDateFormat.format(startDate.getTime()));
            searchParam.put("endDate", Data.onlyDateFormat.format(endDate.getTime()));
            ArrayList<Integer> menu_ids = new ArrayList<>();
            for (int i=0; i<menus.size(); i++) {
                menu_ids.add(menus.get(i).getId());
            }
            searchParam.put("menus", menu_ids.toString());
            ArrayList<Integer> pays = new ArrayList<>();
            if (cash) pays.add(1);
            if (card) pays.add(2);
            if (service) pays.add(3);
            if (credit) pays.add(4);
            searchParam.put("pay", pays.toString());
            lastDate = null;
            new GetOrder(getActivity()).execute();
        }
        else {
            Toast.makeText(getActivity(), "서버에 접속할 수 없어 주문을 검색할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks)activity;
    }

    private class SetTodayTotal extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                JSONObject priceJsn = new JSONObject(Http.get(Data.SERVER_URL+"today", null));
                return priceJsn.getInt("price");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer price) {
            today_total.setText("오늘 하루 매출 : "+price+"원");
        }
    }

    private class GetOrder extends AsyncTask<Void, Void, JSONArray> {

        Context context;
        GetOrder(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("주문 목록을 로드하고 있습니다");
            dialog.show();
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray result = null;
            try {
                if (searchParam.containsKey("startDate")) {
                    result = new JSONArray(Http.post(Data.SERVER_URL+"order/search", searchParam));
                    lastDate = null;
                }
                else {
                    if (lastDate == null) {
                        result = new JSONArray(Http.get(Data.SERVER_URL+"order", null));
                    }
                    else {
                        HashMap<String, Object> param = new HashMap<>();
                        param.put("lastDate", Data.dateFormat.format(lastDate));
                        result = new JSONArray(Http.put(Data.SERVER_URL+"order", param));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                SharedPreferences.Editor ed = Data.pref.edit();
                ed.putBoolean("network", false);
                ed.apply();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (result == null) return;
            if (searchParam.containsKey("startDate")) {
                moreBtn.setClickable(false);
            }
            else {
                moreBtn.setClickable(true);
            }
            try {
                if (lastDate == null) {
                    orders.clear();
                }
                for (int i=0; i<result.length(); i++) {
                    JSONObject jo = result.getJSONObject(i);
                    org.jaram.ds.data.struct.Order order =
                            new org.jaram.ds.data.struct.Order(jo.getInt("id"),
                                    Data.dateFormat.parse(jo.getString("time")),

                                    jo.getInt("totalprice"));
                    orders.add(order);
                    JSONArray ordermenusJSN = jo.getJSONArray("ordermenus");
                    ArrayList<OrderMenu> ordermenus = new ArrayList<OrderMenu>();
                    for (int j=0; j<ordermenusJSN.length(); j++) {
                        JSONObject ordermenuObj = ordermenusJSN.getJSONObject(j);
                        ordermenus.add(new OrderMenu(ordermenuObj.getInt("id"),
                                menus.get(ordermenuObj.getInt("menu_id")), order,
                                ordermenuObj.getInt("pay"), ordermenuObj.getBoolean("curry"),
                                ordermenuObj.getBoolean("twice"), ordermenuObj.getBoolean("takeout")));
                    }
                    order.setOrdermenus(ordermenus);
                }
            } catch(JSONException e) {
                Log.e("JSONParse Error", e.toString());

            } catch (ParseException e) {
                Log.e("DateFormatParse Error", e.toString());
            }
            adapter.notifyDataSetChanged();
            if (orders.size() > 0) {
                if (lastDate == null) {
                    doSelectFirstItem();
                }
                isEnableBtn = true;
                lastDate = orders.get(orders.size()-1).getDate();
            }
            else {
                ordermenus.clear();
                orderDetailAdapter.notifyDataSetChanged();
                totalpriceView.setText("");
                dateView.setText("");
                isEnableBtn = false;
            }
            dialog.dismiss();
        }
    }

    private class GetAllMenuList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            dialog.setMessage("서버에서 메뉴 데이터를 가져오는 중입니다.");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONArray menuJsn = new JSONArray(Http.get(Data.SERVER_URL+"menu/all", null));
                for (int i=0; i<menuJsn.length(); i++) {
                    JSONObject jo = menuJsn.getJSONObject(i);
                    switch(jo.getInt("category_id")) {
                        case 1:
                            menus.put(jo.getInt("id"), new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(1)));
                            break;
                        case 2:
                            menus.put(jo.getInt("id"), new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(2)));
                            break;
                        case 3:
                            menus.put(jo.getInt("id"), new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(3)));
                            break;
                        case 4:
                            menus.put(jo.getInt("id"), new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(4)));
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                SharedPreferences.Editor ed = Data.pref.edit();
                ed.putBoolean("network", false);
                ed.apply();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Data.pref.getBoolean("network", false)) {
                new GetOrder(getActivity()).execute();
            }
            dialog.dismiss();
        }
    }

    public interface Callbacks {
        void addActionBarBtn(View view);
        void addViewAtActionBar(View view, ViewGroup.LayoutParams params);
    }
}
