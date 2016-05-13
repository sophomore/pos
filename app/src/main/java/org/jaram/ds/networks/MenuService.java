package org.jaram.ds.networks;

import org.jaram.ds.models.Menu;

import java.util.List;

import retrofit.http.DELETE;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public interface MenuService {

    @GET("/menu")
    Observable<List<Menu>> getMenus();

    @GET("/menu/all")
    Observable<List<Menu>> getAllMenus();
}
