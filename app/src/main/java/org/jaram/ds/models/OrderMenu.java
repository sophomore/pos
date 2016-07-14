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
    @SerializedName("menu") private Menu menu;
    private Order order;
    private int payInt;
    @Ignore
    @SerializedName("pay") private Pay pay;
    @Nullable
    @SerializedName("attributes") private RealmList<MenuAttribute> attributes;
    @Ignore private boolean isPay = false;

    public OrderMenu copyNewInstance() {
        OrderMenu orderMenu = new OrderMenu();
        orderMenu.setId(getId());
        orderMenu.setMenu(getMenu().copyNewInstance());
        orderMenu.setOrder(getOrder());
        orderMenu.setPayInt(getPayInt());
        if (getAttributes() != null) {
            RealmList<MenuAttribute> attributes = new RealmList<>();
            for (MenuAttribute attr : getAttributes()) {
                attributes.add(attr.copyNewInstance());
            }
            orderMenu.setAttributes(attributes);
        }
        orderMenu.setPay(isPay());
        return orderMenu;
    }

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

    @Nullable
    public RealmList<MenuAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(@Nullable RealmList<MenuAttribute> attributes) {
        this.attributes = attributes;
    }

    public int getTotalPrice() {
        int totalPrice = getMenu().getPrice();
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
}
