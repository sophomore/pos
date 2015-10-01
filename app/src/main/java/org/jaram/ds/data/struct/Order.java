package org.jaram.ds.data.struct;

import android.util.Log;

import org.jaram.ds.data.Data;
import org.jaram.ds.exception.NotExistException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kjydiary on 15. 9. 21..
 */
public class Order extends RealmObject {

    @PrimaryKey
    private int id;
    private Date date;
    private RealmList<OrderMenu> ordermenus;
    private int totalprice;
    @Ignore
    private Manager manager;
    public Order() {
        date = new Date();
        ordermenus = new RealmList<OrderMenu>();
        totalprice = 0;
    }

    public Order(int id, Date date, int totalprice) {
        this.id = id;
        this.date = date;
        ordermenus = new RealmList<OrderMenu>();
        this.totalprice = totalprice;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public RealmList<OrderMenu> getOrdermenus() {
        return ordermenus;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public Manager getManager() {
        if (manager == null) {
            setManager();
        }
        return manager;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setOrdermenus(RealmList<OrderMenu> ordermenus) {
        this.ordermenus = ordermenus;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public void setManager() {
        manager = new Manager();
    }

    public static int getNextKey(Realm db) {
        return (int)db.where(org.jaram.ds.data.struct.Order.class).maximumInt("id") + 1;
    }

    public class Manager {

        public void addMenu(Realm db, Menu menu) {
            addMenu(db, menu, Data.PAY_CREDIT, false, false);
        }

        public void addMenu(Realm db, Menu menu, int pay, boolean curry, boolean twice) {
            OrderMenu ordermenu = db.createObjectFromJson(OrderMenu.class, "{\"id\": "+OrderMenu.getNextKey(db)+"}");
            ordermenu.getManager().set(menu, Order.this, pay, curry, twice);
            getOrdermenus().add(ordermenu);
            setTotalprice(getTotalprice()+ordermenu.getTotalprice());
            Log.d("ordermenu", ordermenu.getTotalprice()+" | "+ordermenu.getMenu().getPrice()+" | "+ordermenu.getMenu().getId()+" | "+Order.this.getTotalprice());
        }

        public void removeMenu(Menu menu) throws NotExistException {
            for (int i=0; i<ordermenus.size(); i++) {
                OrderMenu ordermenu = ordermenus.get(i);
                if (ordermenu.getMenu().equals(menu) && ordermenu.getPay() == Data.PAY_CREDIT) {
                    ordermenus.remove(i);
                    return;
                }
            }
            throw new NotExistException("Not exist menu");
        }

        public JSONObject toJson() {
            JSONObject jo = new JSONObject();
            try {
                jo.put("id", id);
                jo.put("date", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
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
                ordermenusJSN.put(ordermenus.get(i).getManager().toJson());
            }
            return ordermenusJSN;
        }

//        public void set(Order order) {
//            set(order.getId(), order.getDate(), order.getTotalprice());
//        }
//
//        public void set(int id, Date date, int totalprice) {
//            Order.this.setId(id);
//            Order.this.setDate(date);
//            Order.this.setTotalprice(totalprice);
//        }
    }
}