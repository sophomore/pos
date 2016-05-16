package org.jaram.ds.networks.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jaram.ds.models.Pay;

import java.lang.reflect.Type;

/**
 * Created by jdekim43 on 2016. 5. 16..
 */
public class PaySerializer implements JsonSerializer<Pay>, JsonDeserializer<Pay> {

    @Override
    public JsonElement serialize(Pay src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue());
    }

    @Override
    public Pay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Pay.valueOf(json.getAsJsonPrimitive().getAsInt());
    }
}