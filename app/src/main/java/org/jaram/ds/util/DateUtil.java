package org.jaram.ds.util;

import android.annotation.SuppressLint;
import android.content.Context;

import org.jaram.ds.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by chulwoo on 15. 11. 26..
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public enum TimestampUnit {
        MINUTE,
        DAY,
        HOUR,
        WEEK
    }

    /**
     * 현재 시스템 시간이 해당 기간 내에 포함되는지 확인한다.
     * 1월 1일부터 1월 10일 내에 포함되는지 확인하려면 다음과 같이 호출한다.
     * <b>isInPeriod(1, 1, 1, 10)</b>
     *
     * @param startMonth 기간 시작 월
     * @param startDay   기간 시작 일
     * @param endMonth   기간 종료 월
     * @param endDay     기간 종료 일
     * @return 기간 내에 포함되는지 여부
     */
    public static boolean isInPeriod(int startMonth, int startDay,
                                     int endMonth, int endDay) {
        Calendar today = Calendar.getInstance();

        Calendar start = Calendar.getInstance();
        start.set(today.get(Calendar.YEAR), startMonth - 1, startDay);
        start.add(Calendar.DAY_OF_MONTH, -1);

        Calendar end = Calendar.getInstance();
        end.set(today.get(Calendar.YEAR), endMonth - 1, endDay);
        end.add(Calendar.DAY_OF_MONTH, 1);

        return today.after(start) && today.before(end);
    }

    public static long diff(SimpleDateFormat formatter, String start, String end) {
        try {
            Date beginDate = formatter.parse(start);
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            return diff / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long diff(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }

    public static String timestamp(Date date) {
        return timestamp(date, new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA));
    }

    public static String timestamp(Date date, SimpleDateFormat dateFormat) {
        return timestamp(date, dateFormat, TimestampUnit.WEEK);
    }

    public static String timestamp(Date date, SimpleDateFormat dateFormat, TimestampUnit unit) {
        Date today = new Date();
        String endString = "전";

        long diff;
        if (today.compareTo(date) <= 0) {
            endString = "후";
            diff = date.getTime() - today.getTime();
        } else {
            diff = today.getTime() - date.getTime();
        }

        int minute = (int) (diff / (60 * 1000));
        if (minute == 0) {
            return "방금";
        } else if (minute < 60 && unit.ordinal() >= TimestampUnit.MINUTE.ordinal()) {
            return minute + "분 " + endString;
        }

        int hour = minute / 60;
        if (hour < 24 && unit.ordinal() >= TimestampUnit.HOUR.ordinal()) {
            return hour + "시간 " + endString;
        }

        int day = hour / 24;
        if (day < 7 && unit.ordinal() >= TimestampUnit.DAY.ordinal()) {
            return day + "일 " + endString;
        }

        int week = day / 7;
        if (week < 4 && unit.ordinal() >= TimestampUnit.WEEK.ordinal()) {
            return week + "주 " + endString;
        }
        return dateFormat.format(date);
    }

    public static String getDayOfWeekName(Context context) {
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        String weekDay = null;
        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = "월";
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = "화";
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = "수";
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = "목";
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = "금";
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = "토";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = "일";
        }

        return context.getString(R.string.format_day_of_week, weekDay);
    }

    public static String format(Date date) {
        return format("yyyy-MM-dd HH:mm:ss", date);
    }

    public static String format(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }
}
