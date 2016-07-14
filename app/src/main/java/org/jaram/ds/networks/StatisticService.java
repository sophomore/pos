package org.jaram.ds.networks;

import org.jaram.ds.models.result.SimpleStatisticResult;
import org.jaram.ds.models.result.StatisticResult;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 5. 10..
 */
public interface StatisticService {

    @GET("/statistic/")
    Observable<StatisticResult> getStatisticData(@Query("start") String startDate,
                                                 @Query("end") String endDate,
                                                 @Query("menus") String menus,
                                                 @Query("unit") int unit);

    @GET("/statistic/simple/")
    Observable<SimpleStatisticResult> getSimpleStatisticData(@Query("start") String startDate,
                                                                   @Query("end") String endDate);
}
