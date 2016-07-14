package org.jaram.ds.networks;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import org.jaram.ds.Config;
import org.jaram.ds.Data;
import org.jaram.ds.managers.OrderManager;
import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.models.DailyTotalSales;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.models.Pay;
import org.jaram.ds.models.result.SimpleApiResult;
import org.jaram.ds.models.result.SimpleStatisticResult;
import org.jaram.ds.models.result.StatisticResult;
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
    private StatisticService statisticService;

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
        statisticService = adapter.create(StatisticService.class);
    }

    public Observable<DailyTotalSales> getDailyTotalSales(Date date) {
        return generalService.getDailyTotalSales(Data.onlyDateFormat.format(date));
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

    public Observable<PaginationData<Order>> getOrder(int page) {
        return getOrder(page, null, null, null, 0, null);
    }

    public Observable<PaginationData<Order>> getOrder(int page, Date date, Set<Menu> menus,
                                                      Set<Pay> pays, int price,
                                                      OrderManager.PriceFilterCriteria priceCriteria) {
        return orderService.getOrder(page, date == null ? null : Data.onlyDateFormat.format(date),
                menus == null ? null : GsonUtils.getGsonObject().toJson(menus),
                pays == null ? null : GsonUtils.getGsonObject().toJson(pays),
                price, priceCriteria == null ? 0 : priceCriteria.getValue());
    }

    public Observable<SimpleApiResult> addOrder(Order order) {
        return orderService.addOrder(Data.dateFormat.format(order.getDate()),
                GsonUtils.getGsonObject().toJson(order.getOrderMenus()));
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

    public Observable<StatisticResult> getStatistic(Date startDate, Date endDate,
                                                    Set<Menu> menus, StatisticManager.Unit unit) {
        return statisticService.getStatisticData(Data.onlyDateFormat.format(startDate),
                Data.onlyDateFormat.format(endDate),
                GsonUtils.getGsonObject().toJson(menus), unit.getValue());
    }

    public Observable<SimpleStatisticResult> getSimpleStatistic(Date startDate, Date endDate) {
        return statisticService.getSimpleStatisticData(Data.onlyDateFormat.format(startDate),
                Data.onlyDateFormat.format(endDate));
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
