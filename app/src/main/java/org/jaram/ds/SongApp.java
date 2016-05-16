package org.jaram.ds;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.jaram.ds.util.EasySharedPreferences;

import io.fabric.sdk.android.Fabric;

/**
 * Created by jdekim43 on 2016. 5. 16..
 */
public class SongApp extends Application {

    private static final String SETTING_PREFS_NAME = "Settings";

    private EasySharedPreferences settingPrefs;

    public static SongApp get(Context context) {
        return (SongApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        settingPrefs = EasySharedPreferences.with(this, SETTING_PREFS_NAME);

        setupFabric();
    }

    public void setupFabric() {
        if (BuildConfig.DEBUG) {
            Fabric.with(getApplicationContext(),
                    new Crashlytics.Builder().core(
                            new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        } else {
            Fabric.with(getApplicationContext(), new Crashlytics());
        }

        Crashlytics.setUserIdentifier(String.format("%s", 1));
        Crashlytics.setUserName("송호성 쉐프의 돈까스");
        Crashlytics.setUserEmail("admin@jaram.net");
    }


    /**
     * {@code SharedPreferences}에 저장된 데이터를 모두 제거한다.
     */
    public void clearSettings() {
        settingPrefs.clear();
    }
}
