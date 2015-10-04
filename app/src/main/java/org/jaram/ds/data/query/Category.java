package org.jaram.ds.data.query;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.jaram.ds.data.DBQuery;

import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 10. 4..
 */

public class Category extends DBQuery {

    public Category(Context context) {
        super(context);
    }

    public void insert(String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        writeDB().insert("category", null, values);
    }

    public void update(int id, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        writeDB().update("category", values, "_id=?", new String[]{Integer.toString(id)});
    }

    public void delete(int id) {
        writeDB().delete("category", "_id=?", new String[]{Integer.toString(id)});
    }

    public void clear() {
        writeDB().delete("category", null, null);
    }

    public org.jaram.ds.data.struct.Category get(int id) {
        Cursor c = readDB().query("category", null, "_id=?", new String[]{Integer.toString(id)}, null, null, null);
        c.moveToFirst();
        org.jaram.ds.data.struct.Category result = new org.jaram.ds.data.struct.Category(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name")));
        c.close();
        return result;
    }

    public ArrayList<org.jaram.ds.data.struct.Category> getAll() {
        Cursor c = readDB().query("category", null, null, null, null, null, null);
        ArrayList<org.jaram.ds.data.struct.Category> result = new ArrayList<>();
        while (c.moveToNext()) {
            result.add(new org.jaram.ds.data.struct.Category(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name"))));
        }
        c.close();
        return result;
    }
}