package org.jaram.ds.models.result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class SimpleApiResult {

    @SerializedName("success") boolean success;
    @SerializedName("message") String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
