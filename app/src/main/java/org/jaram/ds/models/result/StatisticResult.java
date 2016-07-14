package org.jaram.ds.models.result;

import com.google.gson.annotations.SerializedName;

import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.models.Menu;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class StatisticResult {

    @SerializedName("unit") private StatisticManager.Unit unit;
    @SerializedName("type") private StatisticManager.Type type;
    @SerializedName("start") private Date startDate;
    @SerializedName("end") private Date endDate;
    @SerializedName("result") private List<Item> result;

    public StatisticManager.Unit getUnit() {
        return unit;
    }

    public StatisticManager.Type getType() {
        return type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<Item> getResult() {
        return result;
    }

    public static class Item {

        @SerializedName("key") private String key;
        @SerializedName("value") private List<Value> value;

        public String getKey() {
            return key;
        }

        public List<Value> getValue() {
            return value;
        }
    }

    public static class Value {

        @SerializedName("value") private int value;
        @SerializedName("menu") private Menu menu;

        public int getValue() {
            return value;
        }

        public Menu getMenu() {
            return menu;
        }
    }
}
