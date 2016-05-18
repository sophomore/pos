package org.jaram.ds.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.jaram.ds.Config;
import org.jaram.ds.R;
import org.jaram.ds.activities.OrderManageActivity;
import org.jaram.ds.Data;
import org.jaram.ds.managers.MenuManager;
import org.jaram.ds.models.*;
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

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class SplashFragment extends BaseFragment {

    @BindView(R.id.notice) TextView noticeView;

    private static final int MINIMUM_DISPLAY_TIME = 2000;
    private static final String PREF_LAST_CLOSE_DATE = "pref.last_close_date";

    private Realm db;

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_intro;
    }

    @Override
    protected void setupLayout(View view) {
//        ApiConstants.setBaseUrl(EasySharedPreferences.with(getActivity()).getString("url", "192.168.0.101:80")); TODO:

        Realm.setDefaultConfiguration(createRealmConfiguration());

        Observable.zip(splashTimer(), checkForUpdate(), ((aLong, aBoolean) -> aBoolean))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::init, SLog::e);
    }

    protected RealmConfiguration createRealmConfiguration() {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(getActivity());
        if (Config.DEBUG) {
            builder.deleteRealmIfMigrationNeeded();
        }
        return builder.build();
    }

    private Observable<Long> splashTimer() {
        return Observable.timer(MINIMUM_DISPLAY_TIME, TimeUnit.MILLISECONDS);
    }

    private Observable<Boolean> checkForUpdate() {
        noticeView.setText(R.string.message_loading_new_version);

        return Observable.just(PackageUtil.getVersionName(getActivity()))
                .subscribeOn(Schedulers.io())
                .map(this::checkIsNewVersion)
                .doAfterTerminate(() -> noticeView.setText(R.string.message_loading_prepare))
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
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.label_alert_update)
                .setMessage(R.string.message_confirm_app_update)
                .setPositiveButton(R.string.label_confirm, (dialog, which) -> {
                    String baseUrl = "http://play.google.com/store/apps/details?id=";
                    if (PackageUtil.isPackageUsable(getActivity(), "com.android.vending")) {
                        baseUrl = "market://details?id=";
                    }
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "org.jaram.ds")));
                })
                .setNegativeButton(R.string.label_cancel, null)
                .show();
    }

    private void init(Boolean requireUpdate) {
        checkForClosing();

        noticeView.setText(R.string.message_loading_menu);
        MenuManager.getInstance(getActivity()).asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::endRefreshMenus);
        MenuManager.getInstance(getActivity()).refresh();
    }

    private void checkForClosing() {
        Realm db = Realm.getDefaultInstance();
        RealmQuery<org.jaram.ds.models.Order> savedOrders = db.where(org.jaram.ds.models.Order.class);
        String today = Data.onlyDateFormat.format(new Date());
        if (savedOrders.count() == 0
                || EasySharedPreferences.with(getActivity()).getString(PREF_LAST_CLOSE_DATE, today).equals(today)) {
            return;
        }

        noticeView.setText(R.string.message_loading_closing);
        for (org.jaram.ds.models.Order order : savedOrders.findAll()) {
            BlockingObservable.from(
                    Api.with(getActivity()).addOrder(order)
                            .retryWhen(RxUtils::exponentialBackoff))
                    .subscribe(RxUtils::doNothing, SLog::e);
        }
        db.close();
    }

    private void endRefreshMenus(List<Menu> menus) {
        if (menus == null || menus.size() == 0) {
            noticeView.setText(R.string.message_failure_loading_menu);
        } else {
            noticeView.setText(R.string.message_welcome);
        }
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(15)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong == 3) {
                        noticeView.setText(R.string.message_welcome);
                    } else if (aLong == 10) {
                        startActivity(new Intent(getActivity(), OrderManageActivity.class));
                        getActivity().finish();
                    }
                });
    }
}
