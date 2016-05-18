package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by jdekim43 on 2016. 5. 17..
 */
public class MenuAttribute extends RealmObject {

    public static final MenuAttribute CURRY = new MenuAttribute();
    public static final MenuAttribute TWICE = new MenuAttribute();
    public static final MenuAttribute TAKEOUT = new MenuAttribute();

    static {
        CURRY.setId(-1);
        CURRY.setName("카레추가");
        CURRY.setPrice(2500);
        CURRY.setAvailable(false);
        TWICE.setId(-2);
        TWICE.setName("곱배기");
        TWICE.setPrice(2500);
        TWICE.setAvailable(false);
        TAKEOUT.setId(-3);
        TAKEOUT.setName("포장");
        TAKEOUT.setPrice(500);
        TAKEOUT.setAvailable(false);
    }

    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("price") private int price;
    @SerializedName("available") private boolean available;
    private RealmList<OrderMenu> orderMenus;

    public MenuAttribute copyNewInstance() {
        MenuAttribute attribute = new MenuAttribute();
        attribute.setId(getId());
        attribute.setName(getName());
        attribute.setPrice(getPrice());
        attribute.setAvailable(isAvailable());
        return attribute;
    }

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
