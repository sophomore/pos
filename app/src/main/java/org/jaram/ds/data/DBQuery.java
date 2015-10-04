package org.jaram.ds.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.jaram.ds.data.struct.Menu;
import org.jaram.ds.data.struct.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kjydiary on 15. 10. 4..
 */
public class DBQuery {

    private DBHelper dbHelper = null;
    protected Context context = null;
    public DBQuery(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public SQLiteDatabase readDB() {
        return dbHelper.getReadableDatabase();
    }

    public SQLiteDatabase writeDB() {
        return dbHelper.getWritableDatabase();
    }

}
