package org.jaram.ds.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import org.jaram.ds.R;

public class PackageUtil {

    private static final String PREFERENCES_NAME = "PackageUtils";
    private static final String KEY_VERSION_CODE = "version_code";
    private static final String KEY_VERSION_NAME = "version_name";

    public static boolean isLatestVersion(Context context) {
        return getVersionCode(context) >= getLatestVersionCode(context);
    }

    public static void setLatestVersionCode(Context context, int versionCode) {
        EasySharedPreferences prefs = EasySharedPreferences.with(context, PREFERENCES_NAME);
        prefs.putInt(KEY_VERSION_CODE, versionCode);
    }

    public static int getLatestVersionCode(Context context) {
        EasySharedPreferences prefs = EasySharedPreferences.with(context, PREFERENCES_NAME);
        return prefs.getInt(KEY_VERSION_CODE);
    }

    public static void setLatestVersionName(Context context, String versionName) {
        EasySharedPreferences prefs = EasySharedPreferences.with(context, PREFERENCES_NAME);
        prefs.putString(KEY_VERSION_NAME, versionName);
    }

    public static String getLatestVersionName(Context context) {
        EasySharedPreferences prefs = EasySharedPreferences.with(context, PREFERENCES_NAME);
        return prefs.getString(KEY_VERSION_NAME);
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getString(R.string.package_name), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getString(R.string.package_name), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static boolean isPackageUsable(Context context, String packagename) {
        return context.getPackageManager().getLaunchIntentForPackage(packagename) != null;
    }
}
