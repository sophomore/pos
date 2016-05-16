package org.jaram.ds.managers;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 29..
 */
public class OrderManager {

    private static volatile OrderManager instance;

    private Context context;

    public static OrderManager getInstance(Context context) {
        if (instance == null) {
            synchronized (OrderManager.class) {
                if (instance == null) {
                    instance = new OrderManager();
                }
            }
        }
        instance.context = context;
        return instance;
    }

    private OrderManager() {

    }

    public void addOrder(Order order) {
        Api.with(context).addOrder(order)
                .retryWhen(RxUtils::exponentialBackoff)
                .subscribe(result -> {
                    if (!result.isSuccess()) {
                        errorOnAddOrder(order);
                    }
                }, e -> {
                    errorOnAddOrder(order);
                    Crashlytics.logException(e);
                });
    }

    public Observable<List<Order>> getOrders() {
        return Observable.create(subscriber -> Api.with(context).getOrder()
                .retryWhen(RxUtils::exponentialBackoff)
                .subscribe(result -> {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }, Crashlytics::logException));
    }

    public Observable<List<Order>> getMoreOrders(Date date) {
        return Observable.create(subscriber -> Api.with(context).getMoreOrders(date)
                .retryWhen(RxUtils::exponentialBackoff)
                .subscribe(result -> {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }, Crashlytics::logException));
    }

    private void errorOnAddOrder(Order order) {
        Realm db = Realm.getDefaultInstance();
        db.beginTransaction();
        db.copyToRealmOrUpdate(order);
        db.commitTransaction();
        db.close();
    }
}
