package org.jaram.ds.models.result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class SimpleApiResult {

    @SerializedName("result") String result;

    public boolean isSuccess() {
        return "success".equals(result);
    }
}
