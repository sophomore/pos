package org.jaram.ds.data;

import org.jaram.ds.data.struct.Category;
import org.jaram.ds.data.struct.Menu;

import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Data {

    public static final int PAY_CASH = 1;
    public static final int PAY_CARD = 2;
    public static final int PAY_SERVICE = 3;
    public static final int PAY_CREDIT = 4;
    public static final String SERVER_URL = "http://61.77.77.20/";
    public static final int CURRY = 2500;
    public static final int TWICE = 2500;
    public static final int TAKEOUT = 500;
    public static final HashMap<Integer, Menu> menus = new HashMap<Integer, Menu>();
    public static final HashMap<Integer, Category> categories = new HashMap<Integer, Category>();
}
