package org.jaram.ds.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Pay;
import org.jaram.ds.networks.serializers.CategorySerializer;
import org.jaram.ds.networks.serializers.DateSerializer;
import org.jaram.ds.networks.serializers.MenuSerializer;
import org.jaram.ds.networks.serializers.PaySerializer;

import java.util.Date;

import io.realm.RealmObject;

public class GsonUtils {

    public static Gson getGsonObject() {
        GsonBuilder builder = new GsonBuilder();
//        builder.setDateFormat("yyyy-MM-dd HH:mm:ss Z");
        builder.registerTypeAdapter(Date.class, new DateSerializer());
        builder.registerTypeAdapter(Pay.class, new PaySerializer());
        builder.registerTypeAdapter(Menu.class, new MenuSerializer());
        builder.registerTypeAdapter(Category.class, new CategorySerializer());
        builder.setExclusionStrategies(new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        return builder.create();
    }
}
