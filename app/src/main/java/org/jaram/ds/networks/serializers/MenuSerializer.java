package org.jaram.ds.networks.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.util.SLog;

import java.lang.reflect.Type;

import io.realm.Realm;

/**
 * Created by jdekim43 on 2016. 5. 16..
 */
public class MenuSerializer implements JsonSerializer<Menu>, JsonDeserializer<Menu> {

    @Override
    public JsonElement serialize(Menu src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getId());
    }

    @Override
    public Menu deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Menu menu = new Menu();
        try {
            Realm db = Realm.getDefaultInstance();
            Menu savedMenu = db.where(Menu.class)
                    .equalTo("id", json.getAsInt())
                    .findFirst();
            menu.setId(savedMenu.getId());
            menu.setName(savedMenu.getName());
            menu.setPrice(savedMenu.getPrice());
            menu.setAvailable(savedMenu.isAvailable());
            menu.setCategory(savedMenu.getCategory());
            db.close();
        } catch (UnsupportedOperationException e) {
            JsonObject object = json.getAsJsonObject();
            menu.setId(object.get("id").getAsInt());
            menu.setName(object.get("name").getAsString());
            menu.setPrice(object.get("price").getAsInt());
            menu.setAvailable(object.get("available").getAsBoolean());
            menu.setCategory(context.deserialize(object.get("category_id"), Category.class));
        }
        return menu;
    }
}