package org.jaram.ds.data.struct;

import android.util.Log;

import org.jaram.ds.data.Data;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.PriorityQueue;

/**
 * Created by kjydiary on 15. 9. 21..
 */
public class OrderMenu {

    private int id;
    private Menu menu;
    private Order order;
    private int pay;
    private boolean curry;
    private boolean twice;
    private boolean takeout;
    private int totalprice;
    private boolean isPay;
    public OrderMenu(Menu menu, int pay, boolean curry, boolean twice, boolean takeout) {
        this(0, menu, null, pay, curry, twice, takeout);
    }
    public OrderMenu(int id, Menu menu, int pay, boolean curry, boolean twice, boolean takeout) {
        this(id, menu, null, pay, curry, twice, takeout);
    }
    public OrderMenu(int id, Menu menu, Order order, int pay, boolean curry, boolean twice, boolean takeout) {
        this.id = id;
        this.menu = menu;
        this.order = order;
        this.pay = pay;
        this.curry = curry;
        this.twice = twice;
        this.takeout = takeout;
        this.totalprice = menu.getPrice();
        if (curry) totalprice += Data.CURRY;
        if (twice) totalprice += Data.TWICE;
        if (takeout) totalprice += Data.TAKEOUT;
        this.isPay = false;
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

    public boolean isTakeout() {
        return takeout;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public boolean isPay() {
        return isPay;
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

    public void setTakeout(boolean takeout) {
        this.takeout = takeout;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public JSONObject toJson() {
        Log.d("ordermenu toJson", getId()+"");
        JSONObject jo = new JSONObject();
        try {
            jo.put("id", getId());
            jo.put("menu_id", getMenu().getId());
            jo.put("order_id", getOrder().getId());
            jo.put("pay", getPay());
            jo.put("curry", isCurry());
            jo.put("twice", isTwice());
            jo.put("takeout", isTakeout());
            jo.put("totalprice", getTotalprice());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jo;
    }

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

    public void setTakeout() {
        OrderMenu.this.setTakeout(true);
        setTotalprice(getTotalprice() + Data.TAKEOUT);
        getOrder().setTotalprice(getOrder().getTotalprice() + Data.TAKEOUT);
    }

    public void setPay() {
        isPay = true;
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

    public void resetTakeout() {
        OrderMenu.this.setTakeout(false);
        setTotalprice(getTotalprice() - Data.TAKEOUT);
        getOrder().setTotalprice(getOrder().getTotalprice() - Data.TAKEOUT);
    }

    public void resetPay() {
        isPay = false;
    }

    public void delete() {
        Data.dbOrderMenu.delete(this.getId());
    }
}
