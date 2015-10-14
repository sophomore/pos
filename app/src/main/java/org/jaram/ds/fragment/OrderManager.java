package org.jaram.ds.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.adapter.OrderDetailMenuAdapter;
import org.jaram.ds.adapter.OrderListAdapter;
import org.jaram.ds.data.Closing;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.*;
import org.jaram.ds.data.struct.Order;
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
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class OrderManager extends Fragment implements OrderSearch.Callbacks {

    OrderListAdapter adapter = null;
    ArrayList<org.jaram.ds.data.struct.Order> orders = null;

    OrderDetailMenuAdapter orderDetailAdapter = null;
    ArrayList<OrderMenu> ordermenus = null;

    HashMap<Integer, Menu> menus = null;

    ProgressDialog dialog = null;

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

        ListView orderList = (ListView)view.findViewById(R.id.orderList);

        final TextView totalpriceView = (TextView)view.findViewById(R.id.totalprice);
        final TextView dateView = (TextView)view.findViewById(R.id.date);

        orderList.setAdapter(adapter);
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                org.jaram.ds.data.struct.Order order = orders.get(position);
                ordermenus.clear();
                ordermenus.addAll(order.getOrdermenus());
                orderDetailAdapter.notifyDataSetChanged();
                totalpriceView.setText("총 " + order.getTotalprice() + "원");
                dateView.setText(new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초", Locale.KOREA).format(order.getDate()));
                adapter.setCurrentSelected(position);
                adapter.notifyDataSetChanged();
            }
        });

        ordermenus = new ArrayList<OrderMenu>();
        orderDetailAdapter = new OrderDetailMenuAdapter(ordermenus, getActivity());

        ListView ordermenuList = (ListView)view.findViewById(R.id.ordermenuDetailList);
        ordermenuList.setAdapter(orderDetailAdapter);

        ImageButton searchBtn = (ImageButton)view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OrderSearch(OrderManager.this, menus).show();
            }
        });

        new GetAllMenuList().execute();

        return view;
    }

    @Override
    public void refresh(Calendar startDate, Calendar endDate, ArrayList<Menu> menus, boolean cash, boolean card, boolean service, boolean credit) {
        String query = "SELECT * FROM `ordermenu` WHERE " +
                Data.onlyDateFormat.format(startDate.getTime())+"<date" +
                " AND " +
                "date<"+Data.onlyDateFormat.format(endDate.getTime()) +
                " AND ";
        Data.dbOrder.readDB().rawQuery("", null);
    }

    private class GetOrder extends AsyncTask<Void, Void, JSONArray> {

        Context context;
        GetOrder(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("주문 목록을 로드하고 있습니다");
            orders.addAll(Data.dbOrder.getAll());
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray result = null;
            try {
                result = new JSONArray(Http.get(Data.SERVER_URL+"order", null));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
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
                        Log.d("ordermanager", menus.toString());
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            new GetOrder(getActivity()).execute();
        }
    }
}
