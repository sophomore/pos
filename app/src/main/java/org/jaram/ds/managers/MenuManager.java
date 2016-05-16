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

    private List<Menu> menus;

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
        menus = new ArrayList<>();
    }

    public List<Menu> getMenus() {
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.isAvailable()) {
                result.add(menu);
            }
        }
        return result;
    }

    public List<Menu> getMenus(Realm db) {
        return db.where(Menu.class).equalTo("available", true).findAll();
    }

    public List<Menu> getMenusByCategory(int categoryId) {
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getCategory().getCategoryId() == categoryId) {
                result.add(menu);
            }
        }
        return result;
    }

    public List<Menu> getAvailableMenusByCategory(int categoryId) {
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.isAvailable() && menu.getCategory().getCategoryId() == categoryId) {
                result.add(menu);
            }
        }
        return result;
    }

    public List<Menu> getMenusByCategory(Realm db, int categoryId) {
        return db.where(Menu.class)
                .equalTo("category_id", categoryId)
                .findAll();
    }

    public List<Menu> getAvailableMenusByCategory(Realm db, int categoryId) {
        return db.where(Menu.class)
                .equalTo("available", true)
                .equalTo("category_id", categoryId)
                .findAll();
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
        return menus;
    }

    public List<Menu> getAllMenus(Realm db) {
        return db.where(Menu.class).findAll();
    }

    public void refresh() {
        Api.with(context).getAllMenus()
                .retryWhen(RxUtils::exponentialBackoff)
                .map(this::updateDB)
                .subscribe(publishSubject::onNext, SLog::e);
    }

    public Observable<List<Menu>> asObservable() {
        return publishSubject;
    }

    private List<Menu> updateDB(List<Menu> menus) {
        this.menus.clear();
        this.menus.addAll(menus);
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
