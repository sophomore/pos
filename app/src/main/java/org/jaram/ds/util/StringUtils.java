package org.jaram.ds.util;

import java.util.Locale;

/**
 * Created by jdekim43 on 2016. 1. 13..
 */
public class StringUtils {

    public static String plainVersion(String string) {
        String result = "";
        for (char i : string.toCharArray()) {
            if (NumberUtils.isNumber(i) || i == '.') {
                result += i;
            }
        }
        return result;
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.KOREA, format, args);
    }
}
