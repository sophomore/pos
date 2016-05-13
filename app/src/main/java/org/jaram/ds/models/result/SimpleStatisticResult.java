package org.jaram.ds.models.result;

import org.json.JSONArray;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class SimpleStatisticResult {

    private JSONArray result;

    public SimpleStatisticResult(JSONArray result) {
        this.result = result;
    }

    public JSONArray getResult() {
        return result;
    }
}
