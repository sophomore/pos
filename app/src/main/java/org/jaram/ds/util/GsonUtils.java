package org.jaram.ds.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Pay;
import org.jaram.ds.networks.serializers.CategorySerializer;
import org.jaram.ds.networks.serializers.DateSerializer;
import org.jaram.ds.networks.serializers.MenuSerializer;
import org.jaram.ds.networks.serializers.PaySerializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmObject;

public class GsonUtils {

    public static Gson getGsonObject() {
        GsonBuilder builder = new GsonBuilder();
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
