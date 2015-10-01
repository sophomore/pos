package org.jaram.ds.data.struct;

import org.jaram.ds.data.Data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Menu extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private int price;
    private Category category;
    public Menu() {
        Data.menus.put(id, this);
//        category.getMenus().add(this);
    }
    public Menu(int id, String name, int price, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        Data.menus.put(id, this);
        category.getMenus().add(this);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}