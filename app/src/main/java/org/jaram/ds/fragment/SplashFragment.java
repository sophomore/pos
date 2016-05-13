package org.jaram.ds.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.jaram.ds.R;
import org.jaram.ds.activities.OrderManageActivity;
import org.jaram.ds.data.Data;
import org.jaram.ds.models.*;
import org.jaram.ds.networks.Api;
import org.jaram.ds.networks.ApiConstants;
import org.jaram.ds.util.EasySharedPreferences;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.util.StringUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
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

    @Bind(R.id.notice) TextView noticeView;

    private static final int MINIMUM_DISPLAY_TIME = 2000;

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

        String currentVersion = "";

        try {
            currentVersion = getActivity().getPackageManager().getPackageInfo("org.jaram.ds", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(getActivity()).build());

        Observable.zip(splashTimer(), checkForUpdate(currentVersion), ((aLong, aBoolean) -> aBoolean))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::init, SLog::e);
    }

    private Observable<Long> splashTimer() {
        return Observable.timer(MINIMUM_DISPLAY_TIME, TimeUnit.MILLISECONDS);
    }

    private Observable<Boolean> checkForUpdate(String currentVersion) {
        noticeView.setText("최신 버전을 확인하고 있습니다.");
        return Observable.create(subscriber -> Observable.just(currentVersion)
                .map(this::checkIsNewVersion)
                .retryWhen(RxUtils::exponentialBackoff)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(requireUpdate -> {
                            if (requireUpdate) {
                                showUpdateDialog()
                                        .setOnDismissListener(dialog -> {
                                            subscriber.onNext(true);
                                            subscriber.onCompleted();
                                        });
                            } else {
                                subscriber.onNext(false);
                                subscriber.onCompleted();
                            }
                        }, subscriber::onError,
                        () -> noticeView.setText("앱 실행을 위해 준비하고 있습니다.")));
    }

    private Boolean checkIsNewVersion(String currentVersion) {
//        try {
//            String latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=org.jaram.ds&hl=en")
//                    .timeout(6000)
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                    .referrer("http://www.google.com")
//                    .get()
//                    .select("div[itemprop=softwareVersion]")
//                    .first()
//                    .ownText();
//            return latestVersion.equals(compareVersion(currentVersion, latestVersion));
//        } catch (IOException e) {
//            SLog.e(e);
//        }
        return false;
    }

    private String compareVersion(String version1, String version2) {
        char[] plainVersion1Array = StringUtils.plainVersion(version1).toCharArray();
        char[] plainVersion2Array = StringUtils.plainVersion(version2).toCharArray();
        int length = plainVersion1Array.length<=plainVersion2Array.length
                ? plainVersion1Array.length
                : plainVersion2Array.length;
        for (int i=0; i<length; i++) {
            if (plainVersion1Array[i] < plainVersion2Array[i]) {
                return version2;
            }
        }
        return version1;
    }

    private AlertDialog showUpdateDialog() {
        return new AlertDialog.Builder(getActivity()).setTitle("업데이트 알림")
                .setMessage("업데이트가 있습니다. 업데이트를 하시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {})
                .setNegativeButton("아니오", null)
                .show();
    }

    private void init(Boolean requireUpdate) {
        checkForClosing();

        noticeView.setText("메뉴 정보를 갱신 중 입니다.");
        Api.with(getActivity()).getMenus()
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMenus, SLog::e, () -> {
                    noticeView.setText("환영합니다!");
                    Observable.just(null)
                            .observeOn(AndroidSchedulers.mainThread())
                            .delay(800, TimeUnit.MILLISECONDS)
                            .finallyDo(() -> getActivity().finish())
                            .subscribe(t -> startActivity(new Intent(getActivity(), OrderManageActivity.class)));
                });
    }

    private void checkForClosing() {
        RealmQuery<org.jaram.ds.models.Order> savedOrders = Realm.getInstance(getActivity())
                .where(org.jaram.ds.models.Order.class);
        String today = Data.onlyDateFormat.format(new Date());
        if (savedOrders.count() == 0
                || EasySharedPreferences.with(getActivity()).getString("latestClosingDate", today).equals(today)) {
            return;
        }

        noticeView.setText("서버로 저장된 주문을 전송합니다.");
        for (org.jaram.ds.models.Order order : savedOrders.findAll()) {
            BlockingObservable.from(
                    Api.with(getActivity()).addOrder(order)
                            .retryWhen(RxUtils::exponentialBackoff))
                    .subscribe(result -> {}, Crashlytics::logException);
        }
    }

    private void setMenus(List<Menu> menus) {
        if (menus == null || menus.size() == 0) {
            noticeView.setText("메뉴정보를 불러오는데 실패하여 저장된 정보를 사용합니다.");
            return;
        }

        Realm db = Realm.getInstance(getActivity());

        db.beginTransaction();
        RealmQuery<Category> categoryRealmQuery = db.where(Category.class);
        if (categoryRealmQuery.count() < 4) {
            db.clear(Category.class);
            Category.create(db, 1, getString(R.string.cutlet));
            Category.create(db, 2, getString(R.string.rice));
            Category.create(db, 3, getString(R.string.noodle));
            Category.create(db, 4, getString(R.string.etc));
        }
        db.commitTransaction();

        db.beginTransaction();
        db.clear(Menu.class);
        for (Menu menu : menus) {
            int categoryId = menu.getCategoryId();
            if (categoryId < 1 || 4 < categoryId) {
                continue;
            }
            db.where(Category.class).equalTo("id", categoryId).findFirst().getMenus().add(db.copyToRealm(menu));
        }
        db.commitTransaction();
    }
}
