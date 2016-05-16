package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public enum Category {
    @SerializedName("1") CUTLET(1, "돈까스"),
    @SerializedName("2") RICE(2, "덮밥"),
    @SerializedName("3") NOODLE(3, "면류"),
    @SerializedName("4") ETC(4, "음료 및 스페셜");

    private int categoryId;
    private String categoryName;

    Category(int id, String name) {
        this.categoryId = id;
        this.categoryName = name;
    }

    public static Category getById(int id) {
        for (Category category : values()) {
            if (category.categoryId == id) {
                return category;
            }
        }

        return null;
    }

    public static Category getByName(String name) {
        for (Category category : values()) {
            if (category.categoryName.equals(name)) {
                return category;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}