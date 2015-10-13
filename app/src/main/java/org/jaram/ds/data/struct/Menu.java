package org.jaram.ds.data.struct;

import android.util.Log;

import org.jaram.ds.data.Data;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Menu {

    private int id;
    private String name;
    private int price;
    private Category category;
    public Menu(int id, String name, int price, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
//        Data.menus.put(id, this);
//        category.getMenus().add(this);
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

    public Menu regist() {
        Data.menus.put(id, this);
        category.getMenus().add(this);
        return this;
    }

    public Menu putDB() {
        Data.dbMenu.insert(this.getId(), this.getName(), this.getPrice(), this.getCategory());
        return this;
    }

    public Menu create() {
        Log.d("menu create", id+"");
        regist();
        putDB();
        return this;
    }

    public void destroy() {
        Data.menus.remove(this.getId());
        category.getMenus().remove(this);
        Data.dbMenu.delete(this.getId());
    }

//    public void setManager() {
//        manager = new Manager();
//    }

//    public static void createObj(Realm db, JSONObject menuObj) {
//        try {
//            createObj(db, menuObj.getInt("id"), menuObj.getString("name"), menuObj.getInt("price"),
//                    Data.categories.get(menuObj.getInt("category_id")));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void createObj(Realm db, int id, String name, int price, Category category) {
//        Menu newMenu = db.createObjectFromJson(Menu.class, "{\"id\": "+id+"}");
//        newMenu.setName(name);
//        newMenu.setPrice(price);
//        newMenu.setCategory(category);
//        Data.menus.put(id, newMenu);
//        newMenu.getCategory().getMenus().add(newMenu);
//        Log.d("menu", "Created "+newMenu.toString());
//    }
//
//    public static void removeObj(Realm db, int id) {
//        removeObj(db, Data.menus.get(id));
//    }
//
//    public static void removeObj(Realm db, Menu menu) {
//        Log.d("menu", "remove " + menu.toString());
//        Data.menus.remove(menu.getId());
//        menu.getCategory().getMenus().remove(menu);
//        menu.removeFromRealm();
//    }
//
//    public class Manager {
//
//    }
}