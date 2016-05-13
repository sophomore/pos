package org.jaram.ds.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.jaram.ds.Config;

/**
 * Created by chulwoo on 16. 3. 24..
 */
public class SLog {

    public static void e(Object obj, Throwable e, String message) {
        SLog.e(e);
        SLog.e(obj, message);
    }

    public static void e(Object obj, String message) {
        SLog.e(obj.getClass(), message);
    }

    public static void e(Class cls, String message) {
        if (Config.DEBUG) {
            Log.e(cls.getSimpleName(), message);
        }
    }

    public static void e(Throwable e) {
        if (Config.DEBUG) {
            e.printStackTrace();
        }

        Crashlytics.logException(e);
    }

    public static void w(Object obj, String message) {
        SLog.w(obj.getClass(), message);
    }

    public static void w(Class cls, String message) {
        if (Config.DEBUG) {
            Log.w(cls.getSimpleName(), message);
        }
    }

    public static void i(Object obj, String message) {
        SLog.i(obj.getClass(), message);
    }

    public static void i(Class cls, String message) {
        if (Config.DEBUG) {
            Log.i(cls.getSimpleName(), message);
        }
    }

    public static void d(Object obj, String message) {
        SLog.d(obj.getClass(), message);
    }

    public static void d(Class cls, String message) {
        if (Config.DEBUG) {
            Log.d(cls.getSimpleName(), message);
        }
    }

    public static void v(Object obj, String message) {
        SLog.v(obj.getClass(), message);
    }

    public static void v(Class cls, String message) {
        if (Config.DEBUG) {
            Log.v(cls.getSimpleName(), message);
        }
    }
}