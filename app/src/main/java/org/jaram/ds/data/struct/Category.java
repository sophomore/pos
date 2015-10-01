package org.jaram.ds.data.struct;

import android.util.Log;

import org.jaram.ds.data.Data;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kjydiary on 15. 9. 21..
 */
public class Category extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private RealmList<Menu> menus = new RealmList<Menu>();
    public Category() {
    }
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        Data.categories.put(id, this);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RealmList<Menu> getMenus() {
        return menus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setmenus(RealmList<Menu> menus) {
        this.menus = menus;
    }
}
