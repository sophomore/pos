package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by jdekim43 on 2016. 5. 17..
 */
public class MenuAttribute extends RealmObject {

    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("price") private int price;
    @SerializedName("available") private boolean available;
    private RealmList<OrderMenu> orderMenus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
