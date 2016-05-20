package org.jaram.ds.networks;

import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.models.Pay;
import org.jaram.ds.models.result.SimpleApiResult;

import java.util.Date;
import java.util.List;

import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public interface OrderService {

    @GET("/order")
    Observable<List<Order>> getOrder();

    @FormUrlEncoded
    @PUT("/order")
    Observable<List<Order>> getMoreOrder(@Field("lastDate") Date date);

    @FormUrlEncoded
    @POST("/order/search") //TODO: 현재 앱에 맞게 변경
    Observable<List<Order>> getFilteredOrder(@Field("startDate") String startDate,
                                             @Field("endDate") String endDate,
                                             @Field("menus") String menus,
                                             @Field("pay") String pays);

    @FormUrlEncoded
    @POST("/order")
    Observable<SimpleApiResult> addOrder(Date time, int totalPrice, List<OrderMenu> orderMenus);

    @FormUrlEncoded
    @POST("/order/menu/{id}")
    Observable<SimpleApiResult> modifyOrderMenu(@Path("id") int id, @Field("pay") int pay);

    @GET("/order/{id}/print/receipt")
    Observable<SimpleApiResult> printReceipt(@Path("id") int id);

    @GET("/order/{id}/print/statement")
    Observable<SimpleApiResult> printStatement(@Path("id") int id);

    @DELETE("/order/{id}")
    Observable<SimpleApiResult> deleteOrder(@Path("id") int id);
}
