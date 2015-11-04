package org.jaram.ds.data.struct;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jaram.ds.data.Data;
import org.jaram.ds.exception.NotExistException;
import org.jaram.ds.fragment.OrderManager;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by kjydiary on 15. 9. 21..
 */
public class Order {

    private int id;
    private Date date;
    private ArrayList<OrderMenu> ordermenus;
    private int totalprice;
    public Order() {
        date = new Date();
        ordermenus = new ArrayList<OrderMenu>();
        totalprice = 0;
    }

    public Order(int id, Date date, int totalprice) {
        this.id = id;
        this.date = date;
        ordermenus = new ArrayList<OrderMenu>();
        this.totalprice = totalprice;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public ArrayList<OrderMenu> getOrdermenus() {
        return ordermenus;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setOrdermenus(ArrayList<OrderMenu> ordermenus) {
        this.ordermenus = ordermenus;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public void addMenu(Menu menu, int pay, boolean curry, boolean twice, boolean takeout) {
        linkOrderMenu(new OrderMenu(menu, pay, curry, twice, takeout));
    }

    public void linkOrderMenu(OrderMenu ordermenu) {
        this.getOrdermenus().add(ordermenu);
        this.setTotalprice(this.getTotalprice() + ordermenu.getTotalprice());
        ordermenu.setOrder(this);
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("id", id);
            jo.put("date", Data.dateFormat.format(date));
            jo.put("ordermenus", getOrdermenusAtJson());
            jo.put("totalprice", totalprice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    public JSONArray getOrdermenusAtJson() {
        JSONArray ordermenusJSN = new JSONArray();
        for (int i=0; i<ordermenus.size(); i++) {
            ordermenusJSN.put(ordermenus.get(i).toJson());
        }
        return ordermenusJSN;
    }

    public Order putDB() {
        setId(Data.dbOrder.insert(this.getDate(), this.getTotalprice()));
        Log.d("order putDb", id+"");
        for (int i=0; i<ordermenus.size(); i++) {
            OrderMenu ordermenu = ordermenus.get(i);
            Data.dbOrderMenu.insert(ordermenu.getMenu(), this, ordermenu.getPay(),
                    ordermenu.isCurry(), ordermenu.isTwice(), ordermenu.isTakeout(), ordermenu.getTotalprice());
        }
        Log.d("order struct", ordermenus.toString()+" | "+this.toString());
        return this;
    }

    public void deleteDB() {
        for (int i=0; i<ordermenus.size(); i++) {
            ordermenus.get(i).delete();
        }
        Data.dbOrder.delete(this.getId());
        this.setOrdermenus(null);
    }

    public void store(final Listener listener) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... param) {
                boolean success = true;
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("time", Data.dateFormat.format(Order.this.getDate()));
                params.put("totalprice", Order.this.getTotalprice());
                params.put("ordermenus", Order.this.getOrdermenusAtJson());
                try {
                    Http.post(Data.SERVER_URL+"order", params);
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result) {
                    putDB();
                }
                listener.stored(result);
            }
        }.execute();
    }

    public static void print_statement(final int id) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Http.get(Data.SERVER_URL+"order/"+id+"/print/statement", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void print_receipt(final int id) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Http.get(Data.SERVER_URL+"order/"+id+"/print/receipt", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void delete(final int id) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Http.delete(Data.SERVER_URL+"order/"+id, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public interface Listener {
        void stored(boolean result);
    }
}