package org.jaram.ds.networks;

import org.jaram.ds.models.result.SimpleApiResult;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 5. 10..
 */
public interface StatisticService {

    @FormUrlEncoded
    @POST("/statistic/linechart/")
    Observable<SimpleApiResult> getLinechartData(@Field("startDate") String startDate,
                                                 @Field("endDate") String endDate,
                                                 @Field("unit") int unit,
                                                 @Field("menus") String menus);
}
