package org.jaram.ds.networks;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import org.jaram.ds.Config;
import org.jaram.ds.Data;
import org.jaram.ds.models.DailyTotalSales;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.Pay;
import org.jaram.ds.models.result.SimpleApiResult;
import org.jaram.ds.util.EasySharedPreferences;
import org.jaram.ds.util.GsonUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class Api {

    private static volatile Api instance;

    protected Context context;

    private GeneralService generalService;
    private MenuService menuService;
    private OrderService orderService;

    public static Api with(Context context) {
        if (instance == null) {
            synchronized (Api.class) {
                if (instance == null) {
                    instance = new Api(context);
                }
            }
        }
        instance.context = context;
        return instance;
    }

    private Api(Context context) {
        RestAdapter adapter = buildRestAdapter(context);
        generalService = adapter.create(GeneralService.class);
        menuService = adapter.create(MenuService.class);
        orderService = adapter.create(OrderService.class);
    }

    public Observable<DailyTotalSales> getDailyTotalSales(Date date) {
        return generalService.getDailyTotalSales(date);
    }

    public Observable<List<Menu>> getMenus() {
        return menuService.getMenus();
    }

    public Observable<List<Menu>> getAllMenus() {
        return menuService.getAllMenus();
    }

    public Observable<SimpleApiResult> deleteMenu(Menu menu) {
        return menuService.deleteMenu(menu.getId());
    }

    public Observable<SimpleApiResult> modifyMenu(Menu menu) {
        return menuService.modifyMenu(menu.getId(), menu.getName(), menu.getPrice(), menu.getCategoryId());
    }

    public Observable<SimpleApiResult> addMenu(Menu menu) {
        return menuService.addMenu(menu.getName(), menu.getPrice(), menu.getCategoryId());
    }

    public Observable<List<Order>> getOrder() {
        return orderService.getOrder();
    }

    public Observable<List<Order>> getMoreOrders(Date date) {
        return orderService.getMoreOrder(Data.onlyDateFormat.format(date));
    }

    public Observable<List<Order>> getFilteredOrder(Date date, Set<Menu> menus, Set<Pay> pays) {
        return orderService.getFilteredOrder(Data.onlyDateFormat.format(date),
                Data.onlyDateFormat.format(date),
                GsonUtils.getGsonObject().toJson(menus),
                GsonUtils.getGsonObject().toJson(pays));
    }

    public Observable<SimpleApiResult> addOrder(Order order) {
        return orderService.addOrder(order.getDate(), order.getTotalPrice(), order.getOrderMenus());
    }

    public Observable<SimpleApiResult> modifyOrderMenu(int id, int pay) {
        return orderService.modifyOrderMenu(id, pay);
    }

    public Observable<SimpleApiResult> printReceipt(int id) {
        return orderService.printReceipt(id);
    }

    public Observable<SimpleApiResult> printStatement(int id) {
        return orderService.printStatement(id);
    }

    public Observable<SimpleApiResult> deleteOrder(int id) {
        return orderService.deleteOrder(id);
    }

    private static RestAdapter buildRestAdapter(Context context) {
        ApiConstants.setBaseUrl(EasySharedPreferences.with(context)
                .getString(ApiConstants.PREF_URL, "192.168.0.101"));
        return new RestAdapter.Builder()
                .setEndpoint(ApiConstants.BASE_URL)
                .setErrorHandler(new ApiErrorHandler())
                .setClient(createHttpClient())
                .setConverter(new GsonConverter(GsonUtils.getGsonObject()))
                .setLogLevel((Config.DEBUG) ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }

    private static Client createHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.setReadTimeout(ApiConstants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);
        return new OkClient(httpClient);
    }
}
