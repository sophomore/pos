package org.jaram.ds.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.jaram.ds.Data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class OrderMenu extends RealmObject {

    @PrimaryKey
    @SerializedName("id") private int id;
    @SerializedName("menu_id") private Menu menu;
    @SerializedName("order") private Order order;
    private int payInt;
    @Ignore
    @SerializedName("pay") private Pay pay;
    @SerializedName("curry") private boolean curry;
    @SerializedName("twice") private boolean twice;
    @SerializedName("takeout") private boolean takeout;
    @Nullable
    @SerializedName("attributes") private RealmList<MenuAttribute> attributes;
    @Ignore private boolean isPay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getPayInt() {
        return pay == null ? payInt : pay.getValue();
    }

    public void setPayInt(int payInt) {
        this.payInt = payInt;
        this.pay = Pay.valueOf(payInt);
    }

    public Pay getPay() {
        return pay == null ? Pay.valueOf(getPayInt()) : pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
        setPayInt(pay.getValue());
    }

    public boolean isCurry() {
        return curry;
    }

    public void setCurry(boolean curry) {
        this.curry = curry;
    }

    public boolean isTwice() {
        return twice;
    }

    public void setTwice(boolean twice) {
        this.twice = twice;
    }

    public boolean isTakeout() {
        return takeout;
    }

    public void setTakeout(boolean takeout) {
        this.takeout = takeout;
    }

    @Nullable
    public RealmList<MenuAttribute> getAttributes() {
        return attributes;
    }

    public int getTotalPrice() {
        int totalPrice = getMenu().getPrice();
        totalPrice += isCurry() ? Data.CURRY : 0;
        totalPrice += isTwice() ? Data.TWICE : 0;
        totalPrice += isTakeout() ? Data.TAKEOUT : 0;
        if (attributes != null) {
            for (MenuAttribute attr : attributes) {
                totalPrice += attr.getPrice();
            }
        }
        return totalPrice;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public static int calculateTotalPrice(OrderMenu orderMenu) {
        if (orderMenu.menu == null) {
            return 0;
        }
        int totalPrice = orderMenu.menu.getPrice();
        totalPrice += orderMenu.isCurry() ? Data.CURRY : 0;
        totalPrice += orderMenu.isTwice() ? Data.TWICE : 0;
        totalPrice += orderMenu.isTakeout() ? Data.TAKEOUT : 0;
        return totalPrice;
    }
}
