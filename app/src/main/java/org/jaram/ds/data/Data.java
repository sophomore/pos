package org.jaram.ds.data;

import android.content.SharedPreferences;

import org.jaram.ds.data.struct.Category;
import org.jaram.ds.data.struct.Menu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Data {
    public static final int PAY_CASH = 1;
    public static final int PAY_CARD = 2;
    public static final int PAY_SERVICE = 3;
    public static final int PAY_CREDIT = 4;
    public static final int CURRY = 2500;
    public static final int TWICE = 2500;
    public static final int TAKEOUT = 500;
    public static final HashMap<Integer, Menu> menus = new HashMap<Integer, Menu>();
    public static final HashMap<Integer, Category> categories = new HashMap<Integer, Category>();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    public static final SimpleDateFormat onlyDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    public static String SERVER_URL = "http://192.168.0.101/";

    public static SharedPreferences pref;
    public static org.jaram.ds.data.query.Category dbCategory;
    public static org.jaram.ds.data.query.Menu dbMenu;
    public static org.jaram.ds.data.query.Order dbOrder;
    public static org.jaram.ds.data.query.OrderMenu dbOrderMenu;
}
