package org.jaram.ds.managers;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.jaram.ds.Data;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.result.SimpleStatisticResult;
import org.jaram.ds.models.result.StatisticResult;
import org.jaram.ds.networks.Api;
import org.jaram.ds.networks.ApiConstants;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdekim43 on 2016. 5. 12..
 */
public class StatisticManager {

    public enum Type {
        @SerializedName("1") SALE(1),
        @SerializedName("2") COUNT(2);

        private int value;

        Type(int value) {
            this.value = value;
        }

        public static Type valueOf(int value) {
            for (Type type : Type.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return null;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Unit {
        @SerializedName("1") HOUR(1),
        @SerializedName("2") DATE(2),
        @SerializedName("3") DAY(3),
        @SerializedName("4") MONTH(4),
        @SerializedName("5") QUARTER(5),
        @SerializedName("6") YEAR(6);

        private int value;

        Unit(int value) {
            this.value = value;
        }

        public static Unit valueOf(int value) {
            for (Unit unit : Unit.values()) {
                if (unit.getValue() == value) {
                    return unit;
                }
            }
            return null;
        }

        public int getValue() {
            return value;
        }
    }

    private static volatile StatisticManager instance;

    private Context context;
    private PublishSubject<StatisticManager> publishSubject = PublishSubject.create();

    private Calendar start;
    private Calendar end;
    private Type type;
    private Unit unit;
    private Set<Menu> selectedMenu;

    public static StatisticManager getInstance(Context context) {
        if (instance == null) {
            synchronized (StatisticManager.class) {
                if (instance == null) {
                    instance = new StatisticManager();
                }
            }
        }
        instance.context = context;
        return instance;
    }

    public StatisticManager() {
        start = Calendar.getInstance();
        end = Calendar.getInstance();
        type = Type.COUNT;
        unit = Unit.DAY;
        selectedMenu = new HashSet<>();
    }

    public Observable<StatisticManager> asObservable() {
        return publishSubject;
    }

    public void notifyUpdated() {
        publishSubject.onNext(this);
    }

    public Set<Menu> getSelectedMenu() {
        return selectedMenu;
    }

    public void addSelectedMenu(Menu menu) {
        selectedMenu.add(menu);
    }

    public void removeSelectedMenu(Menu menu) {
        selectedMenu.remove(menu);
    }

    public boolean isSelectedMenu(Menu menu) {
        return selectedMenu.contains(menu);
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Observable<StatisticResult> getStatisticData() {
        return Api.with(context).getStatistic(start.getTime(), end.getTime(), selectedMenu, unit);
    }

    public Observable<SimpleStatisticResult> getSimpleStatisticData() {
        return Api.with(context).getSimpleStatistic(start.getTime(), end.getTime());
    }
}
