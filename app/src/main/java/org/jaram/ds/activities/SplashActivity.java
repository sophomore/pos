package org.jaram.ds.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;

import org.jaram.ds.Config;
import org.jaram.ds.Data;
import org.jaram.ds.R;
import org.jaram.ds.fragment.SplashFragment;
import org.jaram.ds.managers.MenuManager;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.EasySharedPreferences;
import org.jaram.ds.util.PackageUtil;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.util.StringUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class SplashActivity extends BaseActivity<SplashFragment> {

    private static final int MINIMUM_DISPLAY_TIME = 2000;
    private static final String PREF_LAST_CLOSE_DATE = "pref.last_close_date";

    @Override
    public String getScreenName() {
        return getString(R.string.screen_splash);
    }

    @Override
    protected SplashFragment createFragment() {
        return SplashFragment.newInstance();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.setDefaultConfiguration(createRealmConfiguration());

        getSupportActionBar().hide();
        disableDrawer(GravityCompat.END);

        fragment.setOnCreatedListener(() ->
                addSubscription(Observable.zip(splashTimer(), checkForUpdate(), ((aLong, aBoolean) -> aBoolean))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::init, SLog::e)));
    }

    protected RealmConfiguration createRealmConfiguration() {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(this);
        if (Config.DEBUG) {
            builder.deleteRealmIfMigrationNeeded();
        }
        return builder.build();
    }

    private Observable<Long> splashTimer() {
        return Observable.timer(MINIMUM_DISPLAY_TIME, TimeUnit.MILLISECONDS);
    }

    private Observable<Boolean> checkForUpdate() {
        fragment.setNoticeMessage(R.string.message_loading_new_version);

        return Observable.just(PackageUtil.getVersionName(this))
                .subscribeOn(Schedulers.io())
                .map(this::checkIsNewVersion)
                .doAfterTerminate(() -> fragment.setNoticeMessage(R.string.message_loading_prepare))
                .retryWhen(RxUtils::exponentialBackoff);
    }

    private Boolean checkIsNewVersion(String currentVersion) {
        try {
            String latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=org.jaram.ds&hl=en")
                    .timeout(6000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
            return latestVersion.equals(compareVersion(currentVersion, latestVersion));
        } catch (IOException e) {
            SLog.e(e);
        }
        return false;
    }

    private String compareVersion(String version1, String version2) {
        int[] version1Array = StringUtils.plainVersion(version1);
        int[] version2Array = StringUtils.plainVersion(version2);
        int length = version1Array.length <= version2Array.length
                ? version1Array.length
                : version2Array.length;
        for (int i = 0; i < length; i++) {
            if (version1Array[i] < version2Array[i]) {
                return version2;
            }
        }
        return version1;
    }

    private AlertDialog showUpdateDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.label_alert_update)
                .setMessage(R.string.message_confirm_app_update)
                .setPositiveButton(R.string.label_confirm, (dialog, which) -> {
                    String baseUrl = "http://play.google.com/store/apps/details?id=";
                    if (PackageUtil.isPackageUsable(this, "com.android.vending")) {
                        baseUrl = "market://details?id=";
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "org.jaram.ds")));
                })
                .setNegativeButton(R.string.label_cancel, (dialog1, which1) -> init(false))
                .setCancelable(false)
                .show();
    }

    private void init(Boolean requireUpdate) {
        if (requireUpdate) {
            showUpdateDialog();
            return;
        }

        checkForClosing();

        fragment.setNoticeMessage(R.string.message_loading_menu);
        addSubscription(MenuManager.getInstance(this).asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::endRefreshMenus));
        MenuManager.getInstance(this).refresh();
    }

    private void checkForClosing() {
        Realm db = Realm.getDefaultInstance();
        RealmQuery<Order> savedOrders = db.where(org.jaram.ds.models.Order.class);
        String today = Data.onlyDateFormat.format(new Date());
        if (savedOrders.count() == 0
                || EasySharedPreferences.with(this).getString(PREF_LAST_CLOSE_DATE, today).equals(today)) {
            return;
        }

        fragment.setNoticeMessage(R.string.message_loading_closing);
        for (org.jaram.ds.models.Order order : savedOrders.findAll()) {
            BlockingObservable.from(
                    Api.with(this).addOrder(order)
                            .retryWhen(RxUtils::exponentialBackoff))
                    .subscribe(RxUtils::doNothing, SLog::e);
        }
        db.close();
    }

    private void endRefreshMenus(List<Menu> menus) {
        if (menus == null || menus.size() == 0) {
            fragment.setNoticeMessage(R.string.message_failure_loading_menu);
        } else {
            fragment.setNoticeMessage(R.string.message_welcome);
        }
        addSubscription(Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(15)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong == 3) {
                        fragment.setNoticeMessage(R.string.message_welcome);
                    } else if (aLong == 10) {
                        startActivity(new Intent(this, OrderManageActivity.class));
                        finish();
                    }
                }));
    }
}