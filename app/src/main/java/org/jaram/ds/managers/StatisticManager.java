package org.jaram.ds.managers;

import android.content.Context;

import org.jaram.ds.Data;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.result.SimpleStatisticResult;
import org.jaram.ds.models.result.StatisticResult;
import org.jaram.ds.networks.ApiConstants;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdekim43 on 2016. 5. 12..
 */
public class StatisticManager {

    public enum Type {
        SALE(1),
        COUNT(2);

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
        HOUR(1),
        DATE(2),
        DAY(3),
        MONTH(4),
        QUARTER(5),
        YEAR(6);

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
        Observable<StatisticResult> observable = Observable.create(subscriber -> {
            HashMap<String, Object> requestParams = new HashMap<>();
            requestParams.put("startDate", Data.onlyDateFormat.format(start.getTime()));
            requestParams.put("endDate", Data.onlyDateFormat.format(end.getTime()));
            requestParams.put("unit", unit.getValue());
            JSONArray menusArr = new JSONArray();
            for (Menu menu : selectedMenu) {
                menusArr.put(menu.getId());
            }
            requestParams.put("menus", menusArr.toString());

            try {
                subscriber.onNext(new StatisticResult(unit, type,
                        new JSONObject(Http.post(ApiConstants.BASE_URL + "statistic/linechart", requestParams))));
                subscriber.onCompleted();
            } catch (JSONException | IOException e) {
                subscriber.onError(e);
            }
        });
        observable.subscribeOn(Schedulers.io());
        return observable;
    }

    public Observable<SimpleStatisticResult> getSimpleStatisticData() {
        Observable<SimpleStatisticResult> observable = Observable.create(subscriber -> {
            HashMap<String, Object> requestParams = new HashMap<>();
            requestParams.put("startDate", Data.onlyDateFormat.format(start.getTime()));
            requestParams.put("endDate", Data.onlyDateFormat.format(end.getTime()));
            requestParams.put("unit", 4);
            requestParams.put("menus", "[]");

            try {
                subscriber.onNext(new SimpleStatisticResult(new JSONArray(Http.post(ApiConstants.BASE_URL + "statistic/barchart", requestParams))));
                subscriber.onCompleted();
            } catch (JSONException | IOException e) {
                subscriber.onError(e);
            }
        });
        observable.subscribeOn(Schedulers.io());
        return observable;
    }
}
