package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 1. 14..
 *
 * HTTP status code가 200번대가 아닌 경우 {@code ErrorResponse}와 같은 형태로 response가 전달된다.
 */
public class ErrorResponse {
    @SerializedName("detail") public String detail;
}
