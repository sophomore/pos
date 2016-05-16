package org.jaram.ds.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by jdekim43 on 2016. 1. 9..
 */
public class EasySharedPreferences {

    protected SharedPreferences pref;

    private EasySharedPreferences(SharedPreferences pref) {
        this.pref = pref;
    }

    public static EasySharedPreferences with(Context context) {
        return new EasySharedPreferences(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static EasySharedPreferences with(Context context, String key) {
        return new EasySharedPreferences(context.getSharedPreferences(key, Context.MODE_PRIVATE));
    }

    public void putString(String key, String value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putInt(String key, int value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putLong(String key, long value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putFloat(String key, float value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putObject(String key, Object value) {
        if (value == null) {
            throw new NullPointerException("can't put the null references");
        }

        final SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.apply();
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return pref.getString(key, defValue);
    }

    public int getInt(String key) {
        return pref.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return pref.getInt(key, defValue);
    }

    public long getLong(String key) {
        return pref.getLong(key, 0L);
    }

    public long getLong(String key, long defValue) {
        return pref.getLong(key, defValue);
    }

    public float getFloat(String key) {
        return pref.getFloat(key, 0.0f);
    }

    public float getFloat(String key, float defValue) {
        return pref.getFloat(key, defValue);
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    public Object getObject(String key, Class<?> objClass) {
        Gson gson = new Gson();
        String json = pref.getString(key, "");
        return TextUtils.isEmpty(json) ? null : gson.fromJson(json, objClass);
    }

    public void remove(String key) {
        pref.edit().remove(key).apply();
    }

    public void clear() {
        pref.edit().clear().apply();
    }
}
