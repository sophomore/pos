package org.jaram.ds.factories;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.jaram.ds.R;
import org.jaram.ds.Data;
import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.util.SLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class StatisticChartFactory {

    public interface CalculateResultListener {
        void onCalculated(int cash, int card, int total);
    }

    private static final int[] colors = {Color.parseColor("#f44336"),
            Color.parseColor("#e91e63"),
            Color.parseColor("#9c27b0"),
            Color.parseColor("#673ab7"),
            Color.parseColor("#3f51b5"),
            Color.parseColor("#2196f3"),
            Color.parseColor("#03a9f4"),
            Color.parseColor("#00bcd4"),
            Color.parseColor("#009688"),
            Color.parseColor("#4caf50"),
            Color.parseColor("#8bc34a"),
            Color.parseColor("#cddc39"),
            Color.parseColor("#ffeb3b"),
            Color.parseColor("#ffc107"),
            Color.parseColor("#ff9800"),
            Color.parseColor("#ff5722"),
            Color.parseColor("#795548"),
            Color.parseColor("#9e9e9e"),
            Color.parseColor("#607d8b")};

    public static LineData convertLineData(JSONObject data, Calendar startDate, StatisticManager.Unit unit, StatisticManager.Type type) {
        switch(unit) {
            case HOUR:
                return convertCountHourLineData(data, StatisticManager.Type.SALE.equals(type));
            case DATE:
                return convertCountDateLineData(data, StatisticManager.Type.SALE.equals(type), startDate);
            case DAY:
                return convertCountDayLineData(data, StatisticManager.Type.SALE.equals(type));
            case MONTH:
                return convertCountMonthLineData(data, StatisticManager.Type.SALE.equals(type), startDate);
            case QUARTER:
                return convertCountQuarterLineData(data, StatisticManager.Type.SALE.equals(type), startDate);
            case YEAR:
                return convertCountYearLineData(data, StatisticManager.Type.SALE.equals(type), startDate);
        }
        return null;
    }

    public static BarData convertBarData(Context context, JSONArray data, Calendar startDate) {
        return convertBarData(context, data, startDate, null);
    }

    public static BarData convertBarData(Context context, JSONArray data, Calendar startDate, CalculateResultListener listener) {
        int rangeCash = 0;
        int rangeCard = 0;
        int rangeTotal = 0;

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();

        Calendar current = Calendar.getInstance();
        current.setTime(startDate.getTime());

        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject dataJsn = data.getJSONObject(i);
                float[] vals = {(float) dataJsn.getInt("cashtotal"),
                        (float) dataJsn.getInt("cardtotal"),
                        (float) (dataJsn.getInt("totalprice") - dataJsn.getInt("cardtotal") - dataJsn.getInt("cashtotal"))};
                yVals.add(new BarEntry(vals, i));
                xVals.add(current.get(Calendar.YEAR) + "년 " + (current.get(Calendar.MONTH) + 1) + "월");
                current.add(Calendar.MONTH, 1);
                rangeCash += dataJsn.getInt("cashtotal");
                rangeCard += dataJsn.getInt("cardtotal");
                rangeTotal += dataJsn.getInt("totalprice");
            }

            BarDataSet dataSet = new BarDataSet(yVals, "판매금액");
            dataSet.setColors(new int[]{Color.parseColor("#FFB14A"), Color.parseColor("#FE7E39"), Color.parseColor("#E5404C")});
            dataSet.setValueTextColor(ContextCompat.getColor(context, R.color.dark));
            dataSet.setHighLightAlpha(0);
            dataSet.setValueTextSize(16.0f);
            dataSet.setStackLabels(new String[]{"현금", "카드", "기타"});
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);
            if (listener != null) {
                listener.onCalculated(rangeCash, rangeCard, rangeTotal);
            }
            return new BarData(xVals, dataSets);
        } catch (JSONException e) {
            SLog.e(e);
        }
        return null;
    }

    protected static LineData convertCountHourLineData(JSONObject receiveJson, boolean isSale) {
        Random random = new Random();

        HashMap<String, LineDataSet> menuCountData = new HashMap<>();
        ArrayList<String> xVals = new ArrayList<>();
        try {
            Iterator<String> keyIterator = receiveJson.keys();
            boolean isAddedLabel = false;
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                JSONArray dataJsn = receiveJson.getJSONArray(key);
                for (int j = 0; j < dataJsn.length(); j++) {
                    if (menuCountData.containsKey(key)) {
                        LineDataSet dataSet = menuCountData.get(key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                    } else {
                        LineDataSet dataSet = new LineDataSet(new ArrayList<>(), key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                        dataSet.setColor(colors[random.nextInt(colors.length)]);
                        menuCountData.put(key, dataSet);
                    }
                    if (!isAddedLabel) xVals.add(j + "시");
                }
                isAddedLabel = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ILineDataSet> countData = new ArrayList<>();
        for (LineDataSet dataSet : menuCountData.values()) {
            countData.add(dataSet);
        }
        return new LineData(xVals, countData);
    }

    protected static LineData convertCountDateLineData(JSONObject receiveJson, boolean isSale, Calendar startDate) {
        Random random = new Random();

        HashMap<String, LineDataSet> menuCountData = new HashMap<>();
        ArrayList<String> xVals = new ArrayList<>();
        try {
            Iterator<String> keyIterator = receiveJson.keys();
            boolean isAddedLabel = false;
            Calendar date = Calendar.getInstance();
            date.setTime(startDate.getTime());
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                JSONArray dataJsn = receiveJson.getJSONArray(key);
                for (int j = 0; j < dataJsn.length(); j++) {
                    if (menuCountData.containsKey(key)) {
                        LineDataSet dataSet = menuCountData.get(key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                    } else {
                        LineDataSet dataSet = new LineDataSet(new ArrayList<>(), key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                        dataSet.setColor(colors[random.nextInt(colors.length)]);
                        menuCountData.put(key, dataSet);
                    }
                    if (!isAddedLabel) {
                        xVals.add(Data.onlyDateFormat.format(date.getTime()));
                        date.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
                isAddedLabel = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ILineDataSet> countData = new ArrayList<>();
        for (LineDataSet dataSet : menuCountData.values()) {
            countData.add(dataSet);
        }
        return new LineData(xVals, countData);
    }

    protected static LineData convertCountDayLineData(JSONObject receiveJson, boolean isSale) {
        Random random = new Random();

        HashMap<String, LineDataSet> menuCountData = new HashMap<>();
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("월요일");
        xVals.add("화요일");
        xVals.add("수요일");
        xVals.add("목요일");
        xVals.add("금요일");
        xVals.add("토요일");
        xVals.add("일요일");

        try {
            Iterator<String> keyIterator = receiveJson.keys();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                JSONArray dataJsn = receiveJson.getJSONArray(key);
                for (int j = 0; j < dataJsn.length(); j++) {
                    if (menuCountData.containsKey(key)) {
                        LineDataSet dataSet = menuCountData.get(key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                    } else {
                        LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                        dataSet.setColor(colors[random.nextInt(colors.length)]);
                        menuCountData.put(key, dataSet);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ILineDataSet> countData = new ArrayList<>();
        for (LineDataSet dataSet : menuCountData.values()) {
            countData.add(dataSet);
        }
        return new LineData(xVals, countData);
    }

    protected static LineData convertCountMonthLineData(JSONObject recieveDataJsn, boolean isSale, Calendar startDate) {
        Random random = new Random();

        HashMap<String, LineDataSet> menuCountData = new HashMap<>();
        ArrayList<String> xVals = new ArrayList<String>();
        try {
            Iterator<String> keyIterator = recieveDataJsn.keys();
            boolean isAddedLabel = false;
            Calendar date = Calendar.getInstance();
            date.setTime(startDate.getTime());
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                for (int j = 0; j < dataJsn.length(); j++) {
                    if (menuCountData.containsKey(key)) {
                        LineDataSet dataSet = menuCountData.get(key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                    } else {
                        LineDataSet dataSet = new LineDataSet(new ArrayList<>(), key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                        dataSet.setColor(colors[random.nextInt(colors.length)]);
                        menuCountData.put(key, dataSet);
                    }
                    if (!isAddedLabel) {
                        xVals.add(Data.onlyDateFormat.format(date.getTime()));
                        date.add(Calendar.MONTH, 1);
                    }
                }
                isAddedLabel = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ILineDataSet> countData = new ArrayList<>();
        for (LineDataSet dataSet : menuCountData.values()) {
            countData.add(dataSet);
        }
        return new LineData(xVals, countData);
    }

    protected static LineData convertCountQuarterLineData(JSONObject receiveJson, boolean isSale, Calendar startDate) {
        Random random = new Random();

        HashMap<String, LineDataSet> menuCountData = new HashMap<>();
        ArrayList<String> xVals = new ArrayList<>();
        try {
            Iterator<String> keyIterator = receiveJson.keys();
            boolean isAddedLabel = false;
            Calendar date = Calendar.getInstance();
            date.setTime(startDate.getTime());
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                JSONArray dataJsn = receiveJson.getJSONArray(key);
                for (int j = 0; j < dataJsn.length(); j++) {
                    if (menuCountData.containsKey(key)) {
                        LineDataSet dataSet = menuCountData.get(key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                    } else {
                        LineDataSet dataSet = new LineDataSet(new ArrayList<>(), key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                        dataSet.setColor(colors[random.nextInt(colors.length)]);
                        menuCountData.put(key, dataSet);
                    }
                    if (!isAddedLabel) {
                        xVals.add(date.get(Calendar.YEAR) + "년 " + (date.get(Calendar.MONTH) / 4 + 1) + "분기");
                        date.add(Calendar.MONTH, 3 - date.get(Calendar.MONTH) % 3);
                    }
                }
                isAddedLabel = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ILineDataSet> countData = new ArrayList<>();
        for (LineDataSet dataSet : menuCountData.values()) {
            countData.add(dataSet);
        }
        return new LineData(xVals, countData);
    }

    protected static LineData convertCountYearLineData(JSONObject recieveDataJsn, boolean isSale, Calendar startDate) {
        Random random = new Random();

        HashMap<String, LineDataSet> menuCountData = new HashMap<>();
        ArrayList<String> xVals = new ArrayList<String>();
        try {
            Iterator<String> keyIterator = recieveDataJsn.keys();
            boolean isAddedLabel = false;
            Calendar date = Calendar.getInstance();
            date.setTime(startDate.getTime());
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                for (int j = 0; j < dataJsn.length(); j++) {
                    if (menuCountData.containsKey(key)) {
                        LineDataSet dataSet = menuCountData.get(key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                    } else {
                        LineDataSet dataSet = new LineDataSet(new ArrayList<>(), key);
                        dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt(isSale ? "price" : "count"), j));
                        dataSet.setColor(colors[random.nextInt(colors.length)]);
                        menuCountData.put(key, dataSet);
                    }
                    if (!isAddedLabel) {
                        xVals.add(date.get(Calendar.YEAR) + "년 ");
                        date.add(Calendar.YEAR, 1);
                    }
                }
                isAddedLabel = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ILineDataSet> countData = new ArrayList<>();
        for (LineDataSet dataSet : menuCountData.values()) {
            countData.add(dataSet);
        }
        return new LineData(xVals, countData);
    }
}
