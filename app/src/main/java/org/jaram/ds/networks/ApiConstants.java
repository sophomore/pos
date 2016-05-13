package org.jaram.ds.networks;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class ApiConstants {

//    public static String BASE_URL = "http://192.168.0.101/";
    public static String BASE_URL = "http://dearsong.jadekim.kr/";

    /**
     * HTTP 연결이 6초 이상 완료되지 않을 경우 타임아웃
     */
    public static final int HTTP_CONNECT_TIMEOUT = 6000;

    /**
     * HTTP 데이터를 읽는 시간이 10초 이상 완료되지 않을 경우 타임아웃
     */
    public static final int HTTP_READ_TIMEOUT = 10000;

    public static void setBaseUrl(String url) {
        BASE_URL = "http://"+url;
    }
}
