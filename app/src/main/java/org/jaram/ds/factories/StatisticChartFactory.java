package org.jaram.ds.factories;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.jaram.ds.models.result.SimpleStatisticResult;
import org.jaram.ds.models.result.StatisticResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class StatisticChartFactory {

    private static final int[] colors = {
            Color.parseColor("#f44336"),
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
            Color.parseColor("#607d8b")
    };

    public static BarData convertSimpleStatisticData(SimpleStatisticResult result) {
        BarData chartData = new BarData();

        List<BarEntry> cashValues = new ArrayList<>();
        List<BarEntry> cardValues = new ArrayList<>();
        List<BarEntry> serviceValues = new ArrayList<>();
        List<BarEntry> creditValues = new ArrayList<>();
        List<BarEntry> totalValues = new ArrayList<>();

        List<SimpleStatisticResult.Item> values = result.getValues();
        for (int i = 0; i < values.size(); i++) {
            SimpleStatisticResult.Item data = values.get(i);
            chartData.addXValue(data.getKey());
            cashValues.add(new BarEntry(data.getCashTotal(), i));
            cardValues.add(new BarEntry(data.getCardTotal(), i));
            serviceValues.add(new BarEntry(data.getServiceTotal(), i));
            creditValues.add(new BarEntry(data.getCreditTotal(), i));
            totalValues.add(new BarEntry(data.getTotal(), i));
        }

        chartData.addXValue("전체");
        cashValues.add(new BarEntry(result.getCashTotal(), values.size()));
        cardValues.add(new BarEntry(result.getCardTotal(), values.size()));
        serviceValues.add(new BarEntry(result.getServiceTotal(), values.size()));
        creditValues.add(new BarEntry(result.getCreditTotal(), values.size()));
        totalValues.add(new BarEntry(result.getTotal(), values.size()));

        BarDataSet cashDataSet = new BarDataSet(cashValues, "현금");
        cashDataSet.setColor(Color.parseColor("#1abc9c"));
        cashDataSet.setValueTextSize(14);
        cashDataSet.setHighLightAlpha(0);

        BarDataSet cardDataSet = new BarDataSet(cardValues, "카드");
        cardDataSet.setColor(Color.parseColor("#2ecc71"));
        cardDataSet.setValueTextSize(14);
        cardDataSet.setHighLightAlpha(0);

        BarDataSet serviceDataSet = new BarDataSet(serviceValues, "서비스");
        serviceDataSet.setColor(Color.parseColor("#9b59b6"));
        serviceDataSet.setValueTextSize(14);
        serviceDataSet.setHighLightAlpha(0);

        BarDataSet creditDataSet = new BarDataSet(creditValues, "외상");
        creditDataSet.setColor(Color.parseColor("#e67e22"));
        creditDataSet.setValueTextSize(14);
        creditDataSet.setHighLightAlpha(0);

        BarDataSet totalDataSet = new BarDataSet(totalValues, "총액");
        totalDataSet.setColor(Color.parseColor("#95a5a6"));
        totalDataSet.setValueTextSize(14);
        totalDataSet.setHighLightAlpha(0);

        chartData.addDataSet(cashDataSet);
        chartData.addDataSet(cardDataSet);
        chartData.addDataSet(serviceDataSet);
        chartData.addDataSet(creditDataSet);
        chartData.addDataSet(totalDataSet);

        return chartData;
    }

    public static LineData convertStatisticData(StatisticResult result) {
        LineData chartData = new LineData();

        Map<Integer, LineDataSet> chartDataSetStore = new HashMap<>();
        List<StatisticResult.Item> values = result.getResult();
        for (int i = 0; i < values.size(); i++) {
            StatisticResult.Item data = values.get(i);

            chartData.addXValue(data.getKey());

            if (data.getValue() == null) {
                continue;
            }

            for (StatisticResult.Value value : data.getValue()) {
                if (value.getMenu() == null) {
                    continue;
                }

                int storeKey = value.getMenu().getId();
                if (!chartDataSetStore.containsKey(storeKey)) {
                    LineDataSet dataSet = new LineDataSet(new ArrayList<>(), value.getMenu().getName());
                    dataSet.setColor(colors[storeKey % colors.length]);
                    chartDataSetStore.put(storeKey, dataSet);
                }

                LineDataSet dataSet = chartDataSetStore.get(storeKey);
                dataSet.addEntry(new Entry(value.getValue(), i));
            }
        }

        for (LineDataSet dataSet : chartDataSetStore.values()) {
            chartData.addDataSet(dataSet);
        }

        return chartData;
    }
}
