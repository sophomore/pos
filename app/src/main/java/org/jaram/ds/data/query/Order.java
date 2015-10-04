package org.jaram.ds.data.query;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jaram.ds.data.DBQuery;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kjydiary on 15. 10. 4..
 */
public class Order extends DBQuery {

    public Order(Context context) {
        super(context);
    }

    public int insert(Date date, int totalprice) {
        ContentValues values = new ContentValues();
        values.put("date", Data.dateFormat.format(date));
        values.put("totalprice", totalprice);
        return (int)writeDB().insert("`order`", null, values);
    }

    public void update(int id, Date date, int totalprice) {
        ContentValues values = new ContentValues();
        values.put("date", Data.dateFormat.format(date));
        values.put("totalprice", totalprice);
        writeDB().update("`order`", values, "_id=", new String[]{Integer.toString(id)});
    }

    public void delete(int id) {
        writeDB().delete("`order`", "_id=?", new String[]{Integer.toString(id)});
    }

    public void clear() {
        writeDB().delete("order", null, null);
    }

    public org.jaram.ds.data.struct.Order get(int id) {
        Cursor c = readDB().query("`order`", null, "_id=?", new String[]{Integer.toString(id)}, null, null, null);
        org.jaram.ds.data.struct.Order result = null;
        c.moveToFirst();
        try {
            result = new org.jaram.ds.data.struct.Order(c.getInt(c.getColumnIndex("_id")),
                    Data.dateFormat.parse(c.getString(c.getColumnIndex("date"))),
                    c.getInt(c.getColumnIndex("totalprice")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.close();
        return result;
    }

    public ArrayList<org.jaram.ds.data.struct.Order> getAll() {
        Cursor c = readDB().query("`order`", null, null, null, null, null, "date DESC");
        ArrayList<org.jaram.ds.data.struct.Order> result = new ArrayList<>();
        while (c.moveToNext()) {
            try {
                result.add(new org.jaram.ds.data.struct.Order(c.getInt(c.getColumnIndex("_id")),
                        Data.dateFormat.parse(c.getString(c.getColumnIndex("date"))),
                        c.getInt(c.getColumnIndex("totalprice"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        c.close();
        return result;
    }
}