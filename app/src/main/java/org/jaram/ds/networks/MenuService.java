package org.jaram.ds.networks;

import org.jaram.ds.models.Menu;
import org.jaram.ds.models.result.SimpleApiResult;

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
public interface MenuService {

    @GET("/menu/")
    Observable<List<Menu>> getAllMenus();

    @DELETE("/menu/{id}/")
    Observable<SimpleApiResult> deleteMenu(@Path("id") int menuId);

    @FormUrlEncoded
    @PUT("/menu/{id}/")
    Observable<SimpleApiResult> modifyMenu(@Path("id") int menuId,
                                           @Field("name") String name,
                                           @Field("price") int price,
                                           @Field("category") int categoryId);

    @FormUrlEncoded
    @POST("/menu/")
    Observable<SimpleApiResult> addMenu(@Field("name") String name,
                                        @Field("price") int price,
                                        @Field("category") int categoryId);
}
