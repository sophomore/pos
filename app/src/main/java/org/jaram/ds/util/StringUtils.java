package org.jaram.ds.util;

import java.util.Locale;

/**
 * Created by jdekim43 on 2016. 1. 13..
 */
public class StringUtils {

    public static int[] plainVersion(String string) {
        string = string.replaceAll("v|V", "");
        String[] plainNumber = string.split(".");
        int[] result = new int[plainNumber.length];
        for (int i=0; i<plainNumber.length; i++) {
            String number = plainNumber[i];
            if (!NumberUtil.isPositiveNumber(number)) {
                throw new RuntimeException("버전 이름은 숫자, v, . 으로만 이루어 질 수 있습니다.");
            }
            result[i] = Integer.parseInt(number);
        }
        return result;
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.KOREA, format, args);
    }
}
