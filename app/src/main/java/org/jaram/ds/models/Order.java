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
    @SerializedName("ordermenus") private RealmList<OrderMenu> orderMenus;
    @SerializedName("totalprice") private int totalPrice;

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
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
