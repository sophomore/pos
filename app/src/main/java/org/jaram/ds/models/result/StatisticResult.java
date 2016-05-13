package org.jaram.ds.models.result;

import org.jaram.ds.managers.StatisticManager;
import org.json.JSONObject;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class StatisticResult {

    private StatisticManager.Unit unit;
    private StatisticManager.Type type;
    private JSONObject result;

    public StatisticResult(StatisticManager.Unit unit, StatisticManager.Type type, JSONObject result) {
        this.unit = unit;
        this.type = type;
        this.result = result;
    }

    public StatisticManager.Unit getUnit() {
        return unit;
    }

    public StatisticManager.Type getType() {
        return type;
    }

    public JSONObject getResult() {
        return result;
    }
}
