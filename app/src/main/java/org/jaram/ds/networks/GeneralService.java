package org.jaram.ds.networks;

import org.jaram.ds.models.DailyTotalSales;

import java.util.Date;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public interface GeneralService {

    @GET("/today")// TODO: 하루씩으로 변경
    Observable<DailyTotalSales> getDailyTotalSales(@Query("date") Date date);
}
