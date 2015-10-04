package org.jaram.ds.fragment;

import android.app.ProgressDialog;
import android.content.Context;
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
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.*;
import org.jaram.ds.data.struct.Order;
import org.jaram.ds.dialog.OrderSearch;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class OrderManager extends Fragment {

    OrderListAdapter adapter = null;
    ArrayList<org.jaram.ds.data.struct.Order> orders = null;

    OrderDetailMenuAdapter orderDetailAdapter = null;
    ArrayList<OrderMenu> ordermenus = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_manager, container, false);

        //TODO: Get Orders from DB before close

        orders = new ArrayList<org.jaram.ds.data.struct.Order>();
        adapter = new OrderListAdapter(orders, getActivity());

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
                dateView.setText(new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초").format(order.getDate()));
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
                //TODO: search Action
                OrderSearch dialog = new OrderSearch(getActivity());
                dialog.show();
            }
        });

        orders.addAll(Data.dbOrder.getAll());

//        new GetOrder(getActivity()).execute(); //TODO: 삭제된 메뉴 불러올 때 오류 처리

        return view;
    }

    private class GetOrder extends AsyncTask<Void, Void, JSONArray> {

        Context context;
        ProgressDialog dialog;
        GetOrder(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(context, "", "주문 목록을 로드하고 있습니다.", true, false);
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray result = null;
            try {
                result = new JSONArray(Http.get(Data.SERVER_URL+"order", null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

//        @Override
//        protected void onPostExecute(JSONArray result) {
//            try {
//                for (int i=0; i<result.length(); i++) {
//                    JSONObject jo = result.getJSONObject(i);
//                    org.jaram.ds.data.struct.Order order =
//                            new org.jaram.ds.data.struct.Order(jo.getInt("id"),
//                                    Data.dateFormat.parse(jo.getString("time")),
//
//                                    jo.getInt("totalprice"));
//                    orders.add(order);
//                    JSONArray ordermenusJSN = jo.getJSONArray("ordermenus");
//                    ArrayList<OrderMenu> ordermenus = new ArrayList<OrderMenu>();
//                    for (int j=0; j<ordermenusJSN.length(); j++) {
//                        JSONObject ordermenuObj = ordermenusJSN.getJSONObject(j);
//                        order.linkOrderMenu(new OrderMenu(ordermenuObj.getInt("id"),
//                                Data.menus.get(ordermenuObj.getInt("menu_id")), order,
//                                ordermenuObj.getInt("pay"), ordermenuObj.getBoolean("curry"),
//                                ordermenuObj.getBoolean("twice"), ordermenuObj.getBoolean("takeout")));
//                    }
//                }
//            } catch(JSONException e) {
//                Log.e("JSONParse Error", e.toString());
//
//            } catch (ParseException e) {
//                Log.e("DateFormatParse Error", e.toString());
//            }
//            adapter.notifyDataSetChanged();
//            dialog.dismiss();
//        }
    }
}
