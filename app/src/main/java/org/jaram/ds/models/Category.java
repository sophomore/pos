package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class Category extends RealmObject {

    public static final int CUTLET = 1;
    public static final int RICE = 2;
    public static final int NOODLE = 3;
    public static final int ETC = 4;

    @PrimaryKey
    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("menus") private RealmList<Menu> menus;

    public static Category create(Realm db, int id, String name) {
        Category category = db.createObject(Category.class);
        category.setId(id);
        category.setName(name);
        return category;
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

    public RealmList<Menu> getMenus() {
        return menus;
    }

    public void setMenus(RealmList<Menu> menus) {
        this.menus = menus;
    }

    @Override
    public String toString() {
        return getName();
    }
}