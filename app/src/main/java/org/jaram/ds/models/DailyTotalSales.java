package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jdekim43 on 2016. 5. 17..
 */
public class DailyTotalSales {

    @SerializedName("price") int price;
    @SerializedName("date") Date date;

    public int getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }
}
