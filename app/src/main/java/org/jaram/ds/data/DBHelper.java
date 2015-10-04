package org.jaram.ds.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kjydiary on 15. 10. 4..
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "pos.db";
    private static final String CATEGORY_TABLE_NAME="category";
    private static final String MENU_TABLE_NAME="menu";
    private static final String ORDER_TABLE_NAME="order";
    private static final String ORDERMENU_TABLE_NAME="ordermenu";

    private static final String CATEGORY_TABLE_CREATE=
            "CREATE TABLE `"+CATEGORY_TABLE_NAME+"`(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL " +
                    ");";

    private static final String MENU_TABLE_CREATE=
            "CREATE TABLE `"+MENU_TABLE_NAME+"`(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "price INTEGER NOT NULL " +
//                    "FOREIGN KEY(category_id) REFERENCES `"+CATEGORY_TABLE_NAME+"`(_id) "+
                    ");";

    private static final String ORDER_TABLE_CREATE=
            "CREATE TABLE `"+ORDER_TABLE_NAME+"`(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "date DATETIME NOT NULL, " +
                    "totalprice INTEGER NOT NULL " +
                    ");";

    private static final String ORDERMENU_TABLE_CREATE=
            "CREATE TABLE `"+ORDERMENU_TABLE_NAME+"`(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER NOT NULL, " +
                    "menu_id INTEGER NOT NULL, " +
                    "pay INTEGER NOT NULL, " +
                    "curry INTEGER NOT NULL, " +
                    "twice INTEGER NOT NULL, " +
                    "takeout INTEGER NOT NULL, " +
                    "totalprice INTEGER NOT NULL " +
//                    "FOREIGN KEY(order_id) REFERENCES `"+ORDER_TABLE_NAME+"`(_id) "+
//                    "FOREIGN KEY(menu_id) REFERENCES `"+MENU_TABLE_NAME+"`(_id) "+
                    ");";

    private static final String INIT_CATEGORY =
            "INSERT INTO " +
                    CATEGORY_TABLE_NAME +
                    " (name) VALUES " +
                    "('돈까스'), " +
                    "('덮밥'), " +
                    "('면류'), " +
                    "('기타');";

//    private static final String INIT_CATEGORY_2 =
//            "INSERT INTO `"+CATEGORY_TABLE_NAME+"` (name) VALUES (덮밥);";
//
//    private static final String INIT_CATEGORY_3 =
//            "INSERT INTO `"+CATEGORY_TABLE_NAME+"` (name) VALUES (면류);";
//
//    private static final String INIT_CATEGORY_4 =
//            "INSERT INTO `"+CATEGORY_TABLE_NAME+"` (name) VALUES (기타);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CATEGORY_TABLE_CREATE);
        db.execSQL(MENU_TABLE_CREATE);
        db.execSQL(ORDER_TABLE_CREATE);
        db.execSQL(ORDERMENU_TABLE_CREATE);
        db.execSQL(INIT_CATEGORY);
        Log.d("dbhelper", "created");
//        db.execSQL(INIT_CATEGORY_2);
//        db.execSQL(INIT_CATEGORY_3);
//        db.execSQL(INIT_CATEGORY_4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        TODO: DB MIGRATE UPDATE
    }
}
