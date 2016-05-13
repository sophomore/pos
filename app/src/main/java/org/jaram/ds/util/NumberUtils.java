package org.jaram.ds.util;

/**
 * Created by chulwoo on 15. 11. 26..
 */
public class NumberUtils {

    public static boolean isPositiveNumber(CharSequence charSequence) {
        try {
            long number = Long.parseLong(charSequence.toString());
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumber(char c) {
        return Character.isDigit(c);
    }

    public static boolean isNumber(CharSequence charSequence) {
        try {
            long number = Long.parseLong(charSequence.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotNumber(CharSequence charSequence) {
        return !isNumber(charSequence);
    }
}
