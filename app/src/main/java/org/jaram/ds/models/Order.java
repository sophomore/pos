package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class Order extends RealmObject {

    @PrimaryKey
    @SerializedName("id") private int id;
    @SerializedName("time") private Date date;
    @SerializedName("orderMenus") private RealmList<OrderMenu> orderMenus;

    public Order copyNewInstance() {
        Order newOrder = new Order();
        newOrder.setId(getId());
        newOrder.setDate(getDate());
        RealmList<OrderMenu> orderMenus = new RealmList<>();
        for (OrderMenu orderMenu : getOrderMenus()) {
            orderMenus.add(orderMenu.copyNewInstance());
        }
        newOrder.setOrderMenus(orderMenus);
        return newOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<OrderMenu> getOrderMenus() {
        return orderMenus;
    }

    public void setOrderMenus(RealmList<OrderMenu> orderMenus) {
        this.orderMenus = orderMenus;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderMenu orderMenu : orderMenus) {
            totalPrice += orderMenu.getTotalPrice();
        }
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Order) && id == ((Order) o).getId();
    }
}
