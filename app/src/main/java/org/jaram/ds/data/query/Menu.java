package org.jaram.ds.data.query;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jaram.ds.data.DBQuery;
import org.jaram.ds.data.Data;

import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 10. 4..
 */

public class Menu extends DBQuery {

    public Menu(Context context) {
        super(context);
    }

    public void insert(int id, String name, int price, org.jaram.ds.data.struct.Category category) {
        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("name", name);
        values.put("price", price);
        values.put("category_id", category.getId());
        writeDB().insert("menu", null, values);
    }

    public void update(int id, String name, int price, org.jaram.ds.data.struct.Category category) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("category_id", category.getId());
        writeDB().update("menu", values, "_id=", new String[]{Integer.toString(id)});
    }

    public void delete(int id) {
        writeDB().delete("menu", "_id=?", new String[]{Integer.toString(id)});
    }

    public void clear() {
        writeDB().delete("menu", null, null);
    }

    public org.jaram.ds.data.struct.Menu get(int id) {
        Cursor c = readDB().query("menu", null, "_id=?", new String[]{Integer.toString(id)}, null, null, null);
        c.moveToFirst();
        org.jaram.ds.data.struct.Menu result = new org.jaram.ds.data.struct.Menu(c.getInt(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex("name")), c.getInt(c.getColumnIndex("price")),
                Data.categories.get(c.getInt(c.getColumnIndex("category_id"))));
        c.close();
        return result;
    }

    public ArrayList<org.jaram.ds.data.struct.Menu> getAll() {
        Cursor c = readDB().query("menu", null, null, null, null, null, null);
        ArrayList<org.jaram.ds.data.struct.Menu> result = new ArrayList<>();
        while (c.moveToNext()) {
            result.add(new org.jaram.ds.data.struct.Menu(c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("name")), c.getInt(c.getColumnIndex("price")),
                    Data.categories.get(c.getInt(c.getColumnIndex("category_id")))));
        }
        c.close();
        return result;
    }
}