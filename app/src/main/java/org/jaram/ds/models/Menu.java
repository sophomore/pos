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
    @Ignore
    @SerializedName("category_id") private int categoryId;
    private Category category;

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
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        if (category == null) {
            category = Realm.getDefaultInstance().where(Category.class)
                    .equalTo("id", categoryId)
                    .findFirst();
        }
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
