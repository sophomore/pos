package org.jaram.ds.managers;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
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
                    SLog.e(e);
                });
    }

    public Observable<PaginationData<Order>> getOrders() {
        return Api.with(context).getOrder()
                .retryWhen(RxUtils::exponentialBackoff)
                .map(orders -> {
                    orders.addAll(0, getSavedOrders());
                    return orders;
                })
                .onErrorReturn(e -> getSavedOrders())
                .map(this::setupOrderMenu)
                .map(this::convertPaginationData);
    }

    public Observable<PaginationData<Order>> getMoreOrders(Date date) {
        return Api.with(context).getMoreOrders(date)
                .retryWhen(RxUtils::exponentialBackoff)
                .onErrorReturn(e -> new ArrayList<>())
                .map(this::setupOrderMenu)
                .map(this::convertPaginationData);
    }

    protected List<Order> setupOrderMenu(List<Order> data) {
        for (Order order : data) {
            for (OrderMenu orderMenu : order.getOrderMenus()) {
                orderMenu.setOrder(order);
            }
        }
        return data;
    }

    protected PaginationData<Order> convertPaginationData(List<Order> data) {
        PaginationData<Order> paginationData = new PaginationData<>(data);
        paginationData.setmNext(data.size() > 0 ? "hasNext" : "");
        return paginationData;
    }

    private List<Order> getSavedOrders() {
        Realm db = Realm.getDefaultInstance();
        List<Order> result = new ArrayList<>();
        for (Order order : db.where(Order.class).findAllSorted("date", Sort.DESCENDING)) {
            result.add(order.copyNewInstance());
        }
        return result;
    }

    private void errorOnAddOrder(Order order) {
        Realm db = Realm.getDefaultInstance();
        order.setId((int) db.where(Order.class).count());
        order.setDate(new Date());
        db.beginTransaction();
        db.copyToRealm(order);
        db.commitTransaction();
        db.close();
    }
}
