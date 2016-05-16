package org.jaram.ds.networks.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jaram.ds.models.Category;

import java.lang.reflect.Type;

/**
 * Created by jdekim43 on 2016. 5. 16..
 */
public class CategorySerializer implements JsonSerializer<Category>, JsonDeserializer<Category> {

    @Override
    public JsonElement serialize(Category src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getCategoryId());
    }

    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Category.getById(json.getAsJsonPrimitive().getAsInt());
    }
}