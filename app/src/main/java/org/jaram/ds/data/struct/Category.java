package org.jaram.ds.data.struct;

import android.util.Log;

import org.jaram.ds.data.Data;

import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 9. 21..
 */
public class Category {

    private int id;
    private String name;
    private ArrayList<Menu> menus = new ArrayList<Menu>();
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Menu> getMenus() {
        return menus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setmenus(ArrayList<Menu> menus) {
        this.menus = menus;
    }

    public void regist() {
        Data.categories.put(id, this);
    }
}
