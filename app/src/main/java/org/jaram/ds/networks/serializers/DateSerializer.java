package org.jaram.ds.networks.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jdekim43 on 2016. 5. 16..
 */
public class DateSerializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String src = json.getAsJsonPrimitive().getAsString();
        int position = src.lastIndexOf(".");
        if (position != -1) {
            src = src.substring(0, position);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
        try {
            return format.parse(src);
        } catch (Exception e) {
            //do nothing
        }
        return null;
    }
}
