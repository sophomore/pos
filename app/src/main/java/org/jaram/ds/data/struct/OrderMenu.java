package org.jaram.ds.data.struct;

import android.util.Log;

import org.jaram.ds.data.Data;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.PriorityQueue;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kjydiary on 15. 9. 21..
 */
public class OrderMenu extends RealmObject {

    @PrimaryKey
    private int id;
    private Menu menu;
    private Order order;
    private int pay;
    private boolean curry;
    private boolean twice;
    private int totalprice;
    @Ignore
    private Manager manager;
    public OrderMenu() {

    }
    public OrderMenu(int id, Menu menu, Order order, int pay, boolean curry, boolean twice) {
        this.id = id;
        this.menu = menu;
        this.order = order;
        this.pay = pay;
        this.curry = curry;
        this.twice = twice;
        this.totalprice = menu.getPrice();
        if (curry) totalprice += Data.CURRY;
        if (twice) totalprice += Data.TWICE;
        order.getOrdermenus().add(this);
        order.setTotalprice(order.getTotalprice() + this.totalprice);
    }

    public int getId() {
        return id;
    }

    public Menu getMenu() {
        return menu;
    }

    public Order getOrder() {
        return order;
    }

    public int getPay() {
        return pay;
    }

    public boolean isCurry() {
        return curry;
    }

    public boolean isTwice() {
        return twice;
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

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public void setCurry(boolean curry) {
        this.curry = curry;
    }

    public void setTwice(boolean twice) {
        this.twice = twice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public void setManager() {
        manager = new Manager();
    }

    public static int getNextKey(Realm db) {
        return (int)db.where(org.jaram.ds.data.struct.OrderMenu.class).maximumInt("id") + 1;
    }

    public class Manager {

        public void setCurry() {
            OrderMenu.this.setCurry(true);
            setTotalprice(getTotalprice() + Data.CURRY);
            getOrder().setTotalprice(getOrder().getTotalprice() + Data.CURRY);
        }

        public void setTwice() {
            OrderMenu.this.setTwice(true);
            setTotalprice(getTotalprice() + Data.TWICE);
            getOrder().setTotalprice(getOrder().getTotalprice() + Data.TWICE);
        }

        public void resetCurry() {
            OrderMenu.this.setCurry(false);
            setTotalprice(getTotalprice() - Data.CURRY);
            getOrder().setTotalprice(getOrder().getTotalprice() - Data.CURRY);
        }

        public void resetTwice() {
            OrderMenu.this.setTwice(false);
            setTotalprice(getTotalprice() - Data.TWICE);
            getOrder().setTotalprice(getOrder().getTotalprice() - Data.TWICE);
        }

        public JSONObject toJson() {
            JSONObject jo = new JSONObject();
            try {
                jo.put("id", getId());
                jo.put("menu", getMenu().getId());
                jo.put("order", getOrder().getId());
                jo.put("pay", getPay());
                jo.put("curry", isCurry());
                jo.put("twice", isTwice());
                jo.put("totalprice", getTotalprice());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  jo;
        }

        public void set(OrderMenu ordermenu) {
            set(ordermenu.getId(), ordermenu.getMenu(), ordermenu.getOrder(), ordermenu.getPay(), ordermenu.isCurry(), ordermenu.isTwice());
        }

        public void set(Menu menu, Order order, int pay, boolean curry, boolean twice) {
            OrderMenu.this.setMenu(menu);
            OrderMenu.this.setOrder(order);
            OrderMenu.this.setPay(pay);
            OrderMenu.this.setTotalprice(menu.getPrice());
            if (curry) setCurry();
            if (twice) setTwice();
        }

        public void set(int id, Menu menu, Order order, int pay, boolean curry,  boolean twice) {
            OrderMenu.this.setId(id);
            set(menu, order, pay, curry, twice);
        }
    }
}
