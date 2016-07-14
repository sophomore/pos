package org.jaram.ds.networks;

import org.jaram.ds.models.Order;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.models.result.SimpleApiResult;

import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public interface OrderService {

    @GET("/order/")
    Observable<PaginationData<Order>> getOrder(@Query("page") int page,
                                               @Query("date") String date,
                                               @Query("menus") String menusJson,
                                               @Query("pay") String paysJson,
                                               @Query("price") int price,
                                               @Query("priceCriteria") int priceCriteria);

    @FormUrlEncoded
    @POST("/order/")
    Observable<SimpleApiResult> addOrder(@Field("time") String time,
                                         @Field("orderMenus") String orderMenusJson);

    @FormUrlEncoded
    @PUT("/order/{order_menu_id}/")
    Observable<SimpleApiResult> modifyOrderMenu(@Path("order_menu_id") int orderMenuId,
                                                @Field("pay") int pay);

    @DELETE("/order/{id}/")
    Observable<SimpleApiResult> deleteOrder(@Path("id") int id);

    @GET("/print_statement/{id}/")
    Observable<SimpleApiResult> printStatement(@Path("id") int id);

    @GET("/print_receipt/{id}/")
    Observable<SimpleApiResult> printReceipt(@Path("id") int id);
}
