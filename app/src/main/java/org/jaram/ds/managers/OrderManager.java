package org.jaram.ds.managers;

import android.content.Context;

import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.models.Pay;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.Sort;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 29..
 */
public class OrderManager {

    public enum PriceFilterCriteria {
        MORE(1, "이상"),
        LESS(2, "이하");

        private int value;
        private String name;

        PriceFilterCriteria(int value, String name) {
            this.value = value;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public static final int NOT_FILTER_PRICE = 0;

    private static volatile OrderManager instance;

    private Context context;

    private int price;
    private PriceFilterCriteria priceCriteria;
    private Set<Pay> payMethods;
    private Date date;
    private Set<Menu> menus;

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
        payMethods = new HashSet<>();
        menus = new HashSet<>();
        resetFilter();
    }

    public void resetFilter() {
        price = NOT_FILTER_PRICE;
        priceCriteria = PriceFilterCriteria.LESS;
        payMethods.clear();
        date = null;
        menus.clear();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public PriceFilterCriteria getPriceCriteria() {
        return priceCriteria;
    }

    public void setPriceCriteria(PriceFilterCriteria priceCriteria) {
        this.priceCriteria = priceCriteria;
    }

    public Set<Pay> getPayMethods() {
        return payMethods;
    }

    public void addPayMethod(Pay pay) {
        payMethods.add(pay);
    }

    public void removePayMethod(Pay pay) {
        payMethods.remove(pay);
    }

    public void clearPayMethod() {
        payMethods.clear();
    }

    public boolean containsPayMethod(Pay pay) {
        return payMethods.contains(pay);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
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
