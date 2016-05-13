package org.jaram.ds.networks;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.jaram.ds.Config;
import org.jaram.ds.Data;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.result.SimpleApiResult;
import org.jaram.ds.util.GsonUtils;

import java.util.Date;
import java.util.List;
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

    protected static Context context;

    private static volatile Api instance;

    private GeneralService generalService;
    private MenuService menuService;
    private OrderService orderService;

    public Api(Context context) {
        RestAdapter adapter = buildRestAdapter();
        generalService = adapter.create(GeneralService.class);
        menuService = adapter.create(MenuService.class);
        orderService = adapter.create(OrderService.class);
    }

    public static Api with(Context context) {
        if (instance == null) {
            synchronized (Api.class) {
                if (instance == null) {
                    instance = new Api(context);
                }
            }
        }
        Api.context = context;
        return instance;
    }

    public Observable<List<Menu>> getMenus() {
        return menuService.getMenus();
    }

    public Observable<List<Menu>> getAllMenus() {
        return menuService.getAllMenus();
    }

    public Observable<List<Order>> getOrder() {
        return orderService.getOrder();
    }

    public Observable<List<Order>> getMoreOrders(Date date) {
        return orderService.getMoreOrder(Data.dateFormat.format(date));
    }

    public Observable<SimpleApiResult> addOrder(Order order) {
        return orderService.addOrder(Data.dateFormat.format(order.getDate()), order.getTotalPrice(), new Gson().toJson(order.getOrderMenus()));
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

    private RestAdapter buildRestAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiConstants.BASE_URL)
                .setErrorHandler(new ApiErrorHandler())
                .setClient(getHttpClient())
                .setConverter(new GsonConverter(GsonUtils.getGsonObject()))
                .setLogLevel((Config.DEBUG) ?
                        RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
    }

    private Client getHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(ApiConstants.HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        httpClient.setReadTimeout(ApiConstants.HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS);
        return new OkClient(httpClient);
    }
}
