package org.jaram.ds;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Data {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    public static final SimpleDateFormat onlyDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
}
