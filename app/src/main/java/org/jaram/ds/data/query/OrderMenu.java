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

/**
 * Created by kjydiary on 15. 10. 4..
 */
public class OrderMenu extends DBQuery {

    public OrderMenu(Context context) {
        super(context);
    }

    public void insert(org.jaram.ds.data.struct.Menu menu, org.jaram.ds.data.struct.Order order, int pay, boolean curry, boolean twice, boolean takeout, int totalprice) {
        ContentValues values = new ContentValues();
        values.put("menu_id", menu.getId());
        values.put("order_id", order.getId());
        values.put("pay", pay);
        values.put("curry", curry?1:0);
        values.put("twice", twice?1:0);
        values.put("takeout", takeout?1:0);
        values.put("totalprice", totalprice);
        writeDB().insert("ordermenu", null, values);
    }

    public void update(int id, org.jaram.ds.data.struct.Menu menu, org.jaram.ds.data.struct.Order order, int pay, boolean curry, boolean twice, boolean takeout) {
        ContentValues values = new ContentValues();
        values.put("menu_id", menu.getId());
        values.put("order_id", order.getId());
        values.put("pay", pay);
        values.put("curry", curry?1:0);
        values.put("twice", twice?1:0);
        values.put("takeout", takeout ? 1 : 0);
        writeDB().update("ordermenu", values, "_id=?", new String[]{Integer.toString(id)});
    }

    public void delete(int id) {
        writeDB().delete("ordermenu", "_id=?", new String[]{Integer.toString(id)});
    }

    public void clear() {
        writeDB().delete("ordermenu", null, null);
    }

    public org.jaram.ds.data.struct.OrderMenu get(int id) {
        Cursor c = readDB().query("ordermenu", null, "_id=?", new String[]{Integer.toString(id)}, null, null, null);
        c.moveToFirst();
        org.jaram.ds.data.struct.OrderMenu result = new org.jaram.ds.data.struct.OrderMenu(c.getInt(c.getColumnIndex("_id")),
                Data.menus.get(c.getInt(c.getColumnIndex("menu_id"))),
                c.getInt(c.getColumnIndex("pay")),
                c.getInt(c.getColumnIndex("curry")) == 1,
                c.getInt(c.getColumnIndex("twice")) == 1,
                c.getInt(c.getColumnIndex("takeout")) == 1);
        c.close();
        return result;
    }

    public ArrayList<org.jaram.ds.data.struct.OrderMenu> getAll(org.jaram.ds.data.struct.Order order) {
        Cursor c = readDB().query("ordermenu", null, "order_id=?", new String[]{Integer.toString(order.getId())}, null, null, null);
        ArrayList<org.jaram.ds.data.struct.OrderMenu> result = new ArrayList<>();
        while (c.moveToNext()) {
            result.add(new org.jaram.ds.data.struct.OrderMenu(c.getInt(c.getColumnIndex("_id")),
                    Data.menus.get(c.getInt(c.getColumnIndex("menu_id"))),
                    order,
                    c.getInt(c.getColumnIndex("pay")),
                    c.getInt(c.getColumnIndex("curry")) == 1,
                    c.getInt(c.getColumnIndex("twice")) == 1,
                    c.getInt(c.getColumnIndex("takeout")) == 1));
        }
        c.close();
        return result;
    }

    public ArrayList<org.jaram.ds.data.struct.OrderMenu> getAll(int id) {
        Cursor c = readDB().query("ordermenu", null, "order_id=?", new String[]{Integer.toString(id)}, null, null, null);
        ArrayList<org.jaram.ds.data.struct.OrderMenu> result = new ArrayList<>();
        while (c.moveToNext()) {
            result.add(new org.jaram.ds.data.struct.OrderMenu(c.getInt(c.getColumnIndex("_id")),
                    Data.menus.get(c.getInt(c.getColumnIndex("menu_id"))),
                    c.getInt(c.getColumnIndex("pay")),
                    c.getInt(c.getColumnIndex("curry")) == 1,
                    c.getInt(c.getColumnIndex("twice")) == 1,
                    c.getInt(c.getColumnIndex("takeout")) == 1));
        }
        c.close();
        return result;
    }

    public ArrayList<org.jaram.ds.data.struct.OrderMenu> getAll() {
        Cursor c = readDB().query("ordermenu", null, null, null, null, null, null);
        ArrayList<org.jaram.ds.data.struct.OrderMenu> result = new ArrayList<>();
        while (c.moveToNext()) {
            result.add(new org.jaram.ds.data.struct.OrderMenu(c.getInt(c.getColumnIndex("_id")),
                    Data.menus.get(c.getInt(c.getColumnIndex("menu_id"))),
                    c.getInt(c.getColumnIndex("pay")),
                    c.getInt(c.getColumnIndex("curry")) == 1,
                    c.getInt(c.getColumnIndex("twice")) == 1,
                    c.getInt(c.getColumnIndex("takeout")) == 1));
        }
        c.close();
        return result;
    }
}