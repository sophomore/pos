package org.jaram.ds.managers;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class MenuManager {

    private static volatile MenuManager instance;

    private Context context;
    private PublishSubject<List<Menu>> publishSubject = PublishSubject.create();

    public static MenuManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MenuManager.class) {
                if (instance == null) {
                    instance = new MenuManager();
                }
            }
        }
        instance.context = context;
        return instance;
    }

    private MenuManager() {
    }

    public List<Menu> getMenus() {
        Realm db = Realm.getDefaultInstance();
        List<Menu> result = new ArrayList<>();
        for (Menu menu : getMenus(db)) {
            result.add(menu.copyNewInstance());
        }
        db.close();
        return result;
    }

    public List<Menu> getMenus(Realm db) {
        return db.where(Menu.class).equalTo("available", true).findAllSorted("id", Sort.DESCENDING);
    }

    public List<Menu> getMenusByCategory(int categoryId) {
        Realm db = Realm.getDefaultInstance();
        List<Menu> result = new ArrayList<>();
        for (Menu menu : getMenusByCategory(db, categoryId)) {
            result.add(menu.copyNewInstance());
        }
        db.close();
        return result;
    }

    public List<Menu> getAvailableMenusByCategory(int categoryId) {
        Realm db = Realm.getDefaultInstance();
        List<Menu> result = new ArrayList<>();
        for (Menu menu : getAvailableMenusByCategory(db, categoryId)) {
            result.add(menu.copyNewInstance());
        }
        db.close();
        return result;
    }

    public List<Menu> getMenusByCategory(Realm db, int categoryId) {
        return db.where(Menu.class)
                .equalTo("categoryId", categoryId)
                .findAllSorted("id", Sort.DESCENDING);
    }

    public List<Menu> getAvailableMenusByCategory(Realm db, int categoryId) {
        return db.where(Menu.class)
                .equalTo("available", true)
                .equalTo("categoryId", categoryId)
                .findAllSorted("id", Sort.DESCENDING);
    }

    public List<Menu> getMenusByCategory(Category category) {
        return getMenusByCategory(category.getCategoryId());
    }

    public List<Menu> getAvailableMenusByCategory(Category category) {
        return getAvailableMenusByCategory(category.getCategoryId());
    }

    public List<Menu> getMenusByCategory(Realm db, Category category) {
        return getMenusByCategory(db, category.getCategoryId());
    }

    public List<Menu> getAvailableMenusByCategory(Realm db, Category category) {
        return getAvailableMenusByCategory(db, category.getCategoryId());
    }

    public List<Menu> getAllMenus() {
        Realm db = Realm.getDefaultInstance();
        List<Menu> result = new ArrayList<>();
        for (Menu menu : db.where(Menu.class).findAllSorted("id", Sort.DESCENDING)) {
            result.add(menu.copyNewInstance());
        }
        db.close();
        return result;
    }

    public List<Menu> getAllMenus(Realm db) {
        return db.where(Menu.class).findAllSorted("id", Sort.DESCENDING);
    }

    public void refresh() {
        Api.with(context).getAllMenus()
                .retryWhen(RxUtils::exponentialBackoff)
                .onErrorReturn(e -> getAllMenus())
                .map(this::updateDB)
                .subscribe(publishSubject::onNext, SLog::e);
    }

    public Observable<List<Menu>> asObservable() {
        return publishSubject;
    }

    private List<Menu> updateDB(List<Menu> menus) {
        Realm db = Realm.getDefaultInstance();
        db.beginTransaction();
        for (Menu menu : menus) {
            Menu.saveWithCopy(db, menu);
        }
        db.commitTransaction();
        db.close();
        return menus;
    }
}
