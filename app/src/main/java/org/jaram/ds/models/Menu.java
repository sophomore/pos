package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class Menu extends RealmObject {

    @PrimaryKey
    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("price") private int price;
    private int categoryId;
    @Ignore
    @SerializedName("category_id") private Category category;
    @SerializedName("available") private boolean available;

    public static void saveWithCopy(Menu menu) {
        Realm db = Realm.getDefaultInstance();
        db.beginTransaction();
        saveWithCopy(db, menu);
        db.commitTransaction();
        db.close();
    }

    public static Menu saveWithCopy(Realm db, Menu menu) {
        Menu savedMenu = db.where(Menu.class).equalTo("id", menu.getId()).findFirst();
        if (savedMenu == null) {
            savedMenu = db.createObject(Menu.class);
        }
        savedMenu.setId(menu.getId());
        savedMenu.setName(menu.getName());
        savedMenu.setPrice(menu.getPrice());
        savedMenu.setAvailable(menu.isAvailable());
        savedMenu.setCategory(menu.getCategory());
        return savedMenu;
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

    public int getCategoryId() {
        return category == null ? categoryId : category.getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        this.category = Category.getById(categoryId);
    }

    public Category getCategory() {
        return category == null ? Category.getById(getCategoryId()) : category;
    }

    public void setCategory(Category category) {
        this.category = category;
        setCategoryId(category.getCategoryId());
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
