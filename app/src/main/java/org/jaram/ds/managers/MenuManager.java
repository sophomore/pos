package org.jaram.ds.managers;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import org.jaram.ds.models.Menu;
import org.jaram.ds.networks.Api;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class MenuManager {

    private static volatile MenuManager instance;

    private Context context;

    private List<OnMenuListChangedObserver> observers = new ArrayList<>();

    private List<Menu> menus = new ArrayList<>();

    public MenuManager(Context context) {
        this.context = context;
    }

    public static MenuManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MenuManager.class) {
                if (instance == null) {
                    instance = new MenuManager(context);
                }
            }
        }
        instance.context = context;
        return instance;
    }

    public void addOnMenuListChangedObserver(OnMenuListChangedObserver observer) {
        observers.add(observer);
    }

    public void removeOnMenuListChangedObserver(OnMenuListChangedObserver observer) {
        observers.remove(observer);
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void refresh() {
        Api.with(context).getMenus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMenus, Crashlytics::logException);
    }

    public void setMenus(List<Menu> menus) {
        this.menus.clear();
        this.menus.addAll(menus);
        notifyMenuListChanged();
    }

    public void notifyMenuListChanged() {
        for (OnMenuListChangedObserver observer : observers) {
            observer.onChanged();
        }
    }

    public interface OnMenuListChangedObserver {
        void onChanged();
    }
}
