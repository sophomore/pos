package org.jaram.ds.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.jaram.ds.R;
import org.jaram.ds.adapter.StatisticMenuAdapter;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Menu;
import org.jaram.ds.util.Http;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class Statistic extends Fragment {

    public static final int TYPE_SALE = 1;
    public static final int TYPE_COUNT = 2;
    public static final int UNIT_HOUR = 1;
    public static final int UNIT_DATE = 2;
    public static final int UNIT_DAY = 3;
    public static final int UNIT_MONTH = 4;
    public static final int UNIT_QUATER = 5;
    public static final int UNIT_YEAR = 6;

    private Callbacks callbacks;

    private Calendar startCal;
    private Calendar endCal;

    private LineChart lineChart;

    private ArrayList<Menu> cutlets = new ArrayList<Menu>();
    private ArrayList<Menu> rices = new ArrayList<Menu>();
    private ArrayList<Menu> noodles = new ArrayList<Menu>();
    private ArrayList<Menu> etcs = new ArrayList<Menu>();

    private StatisticMenuAdapter cutletAdapter = new StatisticMenuAdapter(cutlets);
    private StatisticMenuAdapter riceAdapter = new StatisticMenuAdapter(rices);
    private StatisticMenuAdapter noodleAdapter = new StatisticMenuAdapter(noodles);
    private StatisticMenuAdapter etcAdapter = new StatisticMenuAdapter(etcs);

    private Button saleBtn;
    private Button countBtn;
    private Button hourBtn;
    private Button dateBtn;
    private Button dayBtn;
    private Button monthBtn;
    private Button quaterBtn;
    private Button yearBtn;
    private Button cutletBtn;
    private Button riceBtn;
    private Button noodleBtn;
    private Button etcBtn;

    private GridView cutletList;
    private GridView riceList;
    private GridView noodleList;
    private GridView etcList;

    private Button statisticBtn;

    private int type = TYPE_SALE;
    private int unit = UNIT_HOUR;

    private static Statistic view;
    public static Statistic getInstance() {
        if (view == null) {
            view = new Statistic();
        }
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        lineChart = (LineChart)view.findViewById(R.id.statistic_lineChart);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDescription(null);

        setActionBar();
        setDrawer();

        return view;
    }

    private void setActionBar() {
        RelativeLayout actionbaritem = (RelativeLayout)LayoutInflater.from(getActivity()).inflate(R.layout.statistic_date, null, false);

        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        startCal.setTime(new Date());
        endCal.setTime(new Date());

        final Button startDate = (Button)actionbaritem.findViewById(R.id.startDate);
        final Button endDate = (Button) actionbaritem.findViewById(R.id.endDate);
        ImageButton analyticsBtn = (ImageButton)actionbaritem.findViewById(R.id.analyticsBtn);

        startDate.setText(Data.onlyDateFormat.format(startCal.getTime()));
        endDate.setText(Data.onlyDateFormat.format(startCal.getTime()));

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startCal.set(year, monthOfYear, dayOfMonth);
                                startDate.setText(Data.onlyDateFormat.format(startCal.getTime()));
                            }
                        },
                        startCal.get(Calendar.YEAR),
                        startCal.get(Calendar.MONTH),
                        startCal.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endCal.set(year, monthOfYear, dayOfMonth);
                                endDate.setText(Data.onlyDateFormat.format(endCal.getTime()));
                            }
                        },
                        endCal.get(Calendar.YEAR),
                        endCal.get(Calendar.MONTH),
                        endCal.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
        analyticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetStatisticInfo().execute();
            }
        });

        callbacks.addViewAtActionBar(actionbaritem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void setDrawer() {
        FrameLayout drawer = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.drawer_statistic_setting, null);

        TypeListener typeListener = new TypeListener();
        saleBtn = (Button)drawer.findViewById(R.id.type_saleBtn);
        countBtn = (Button)drawer.findViewById(R.id.type_countBtn);

        saleBtn.setOnClickListener(typeListener);
        countBtn.setOnClickListener(typeListener);

        UnitListener unitListener = new UnitListener();
        hourBtn = (Button)drawer.findViewById(R.id.unit_hourBtn);
        dateBtn = (Button)drawer.findViewById(R.id.unit_dateBtn);
        dayBtn = (Button)drawer.findViewById(R.id.unit_dayBtn);
        monthBtn = (Button)drawer.findViewById(R.id.unit_monthBtn);
        quaterBtn = (Button)drawer.findViewById(R.id.unit_quaterBtn);
        yearBtn = (Button)drawer.findViewById(R.id.unit_yearBtn);

        hourBtn.setOnClickListener(unitListener);
        dateBtn.setOnClickListener(unitListener);
        dayBtn.setOnClickListener(unitListener);
        monthBtn.setOnClickListener(unitListener);
        quaterBtn.setOnClickListener(unitListener);
        yearBtn.setOnClickListener(unitListener);
        
        cutletList = (GridView)drawer.findViewById(R.id.menu_cutletList);
        riceList = (GridView)drawer.findViewById(R.id.menu_riceList);
        noodleList = (GridView)drawer.findViewById(R.id.menu_noodleList);
        etcList = (GridView)drawer.findViewById(R.id.menu_etcList);

        cutletList.setAdapter(cutletAdapter);
        riceList.setAdapter(riceAdapter);
        noodleList.setAdapter(noodleAdapter);
        etcList.setAdapter(etcAdapter);

        MenuCategoryListener menuCategoryListener = new MenuCategoryListener();
        cutletBtn = (Button)drawer.findViewById(R.id.menu_cutletBtn);
        riceBtn = (Button)drawer.findViewById(R.id.menu_riceBtn);
        noodleBtn = (Button)drawer.findViewById(R.id.menu_noodleBtn);
        etcBtn = (Button)drawer.findViewById(R.id.menu_etcBtn);

        cutletBtn.setOnClickListener(menuCategoryListener);
        riceBtn.setOnClickListener(menuCategoryListener);
        noodleBtn.setOnClickListener(menuCategoryListener);
        etcBtn.setOnClickListener(menuCategoryListener);

        statisticBtn = (Button)drawer.findViewById(R.id.statisticBtn);
        statisticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetStatisticInfo().execute();
                callbacks.closeLeftDrawer();
            }
        });

        callbacks.setDrawer(drawer);
        new GetAllMenuList().execute();
    }

    private void menuCategoryCloseAll() {
        cutletBtn.setBackgroundResource(R.drawable.white_btn);
        riceBtn.setBackgroundResource(R.drawable.white_btn);
        noodleBtn.setBackgroundResource(R.drawable.white_btn);
        etcBtn.setBackgroundResource(R.drawable.white_btn);

        cutletBtn.setTextColor(getResources().getColor(R.color.dark));
        riceBtn.setTextColor(getResources().getColor(R.color.dark));
        noodleBtn.setTextColor(getResources().getColor(R.color.dark));
        etcBtn.setTextColor(getResources().getColor(R.color.dark));

        cutletList.setVisibility(View.GONE);
        riceList.setVisibility(View.GONE);
        noodleList.setVisibility(View.GONE);
        etcList.setVisibility(View.GONE);
    }

    private void menuCategoryOpen(int category_id) {
        menuCategoryCloseAll();
        switch(category_id) {
            case 1:
                setClickedView(cutletBtn);
                cutletList.setVisibility(View.VISIBLE);
                break;
            case 2:
                setClickedView(riceBtn);
                riceList.setVisibility(View.VISIBLE);
                break;
            case 3:
                setClickedView(noodleBtn);
                noodleList.setVisibility(View.VISIBLE);
                break;
            case 4:
                setClickedView(etcBtn);
                etcList.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setClickedView(Button v) {
        v.setBackgroundResource(R.drawable.point_btn);
        v.setTextColor(Color.WHITE);
    }

    private void setUnClickedView(Button v) {
        v.setBackgroundResource(R.drawable.white_btn);
        v.setTextColor(getResources().getColor(R.color.dark));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    public interface Callbacks {
        void setDrawer(FrameLayout drawer);
        void addViewAtActionBar(View view, ViewGroup.LayoutParams params);
        void closeLeftDrawer();
    }

    private class TypeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.type_saleBtn:
                    setUnClickedView(countBtn);
                    type = TYPE_SALE;
                    break;
                case R.id.type_countBtn:
                    setUnClickedView(saleBtn);
                    type = TYPE_COUNT;
                    break;
            }
            setClickedView((Button)v);
        }
    }

    private class UnitListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            setUnClickedView(hourBtn);
            setUnClickedView(dateBtn);
            setUnClickedView(dayBtn);
            setUnClickedView(monthBtn);
            setUnClickedView(quaterBtn);
            setUnClickedView(yearBtn);
            switch (v.getId()) {
                case R.id.unit_hourBtn:
                    setClickedView(hourBtn);
                    unit = UNIT_HOUR;
                    break;
                case R.id.unit_dateBtn:
                    setClickedView(dateBtn);
                    unit = UNIT_DATE;
                    break;
                case R.id.unit_dayBtn:
                    setClickedView(dayBtn);
                    unit = UNIT_DAY;
                    break;
                case R.id.unit_monthBtn:
                    setClickedView(monthBtn);
                    unit = UNIT_MONTH;
                    break;
                case R.id.unit_quaterBtn:
                    setClickedView(quaterBtn);
                    unit = UNIT_QUATER;
                    break;
                case R.id.unit_yearBtn:
                    setClickedView(yearBtn);
                    unit = UNIT_YEAR;
                    break;
            }
        }
    }

    private class MenuCategoryListener implements View.OnClickListener {

        int currentOpen;
        @Override
        public void onClick(View v) {
            if (currentOpen == v.getId()) {
                menuCategoryCloseAll();
                currentOpen = 0;
                return;
            }
            currentOpen = v.getId();
            switch(v.getId()) {
                case R.id.menu_cutletBtn:
                    menuCategoryOpen(1);
                    break;
                case R.id.menu_riceBtn:
                    menuCategoryOpen(2);
                    break;
                case R.id.menu_noodleBtn:
                    menuCategoryOpen(3);
                    break;
                case R.id.menu_etcBtn:
                    menuCategoryOpen(4);
                    break;
            }
        }
    }

    private void setLineChartData(LineData data) {
        lineChart.setData(data);
//        lineChart.notifyDataSetChanged();
        lineChart.getChartBitmap().recycle();
        lineChart.invalidate();
    }

    private class GetStatisticInfo extends AsyncTask<Void, Void, LineData> {

        private final int[] colors = {Color.parseColor("#f44336"),
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

        Random random = new Random();

        private LineData convertCountHourLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                boolean isAddedLabel = false;
                while (keyIter.hasNext()) {
                    String key = keyIter.next();
                    JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                    for (int j=0; j<dataJsn.length(); j++) {
                        if (menuCountData.containsKey(key)) {
                            LineDataSet dataSet = menuCountData.get(key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                            dataSet.setColor(colors[random.nextInt(colors.length)]);
                            menuCountData.put(key, dataSet);
                        }
                        if (!isAddedLabel) xVals.add(j+"시");
                    }
                    isAddedLabel = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertCountDateLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                boolean isAddedLabel = false;
                Calendar date = (Calendar) startCal.clone();
                while (keyIter.hasNext()) {
                    String key = keyIter.next();
                    JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                    for (int j=0; j<dataJsn.length(); j++) {
                        if (menuCountData.containsKey(key)) {
                            LineDataSet dataSet = menuCountData.get(key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
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

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertCountDayLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            xVals.add("월요일");
            xVals.add("화요일");
            xVals.add("수요일");
            xVals.add("목요일");
            xVals.add("금요일");
            xVals.add("토요일");
            xVals.add("일요일");

            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                while (keyIter.hasNext()) {
                    String key = keyIter.next();
                    JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                    for (int j=0; j<dataJsn.length(); j++) {
                        if (menuCountData.containsKey(key)) {
                            LineDataSet dataSet = menuCountData.get(key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                            dataSet.setColor(colors[random.nextInt(colors.length)]);
                            menuCountData.put(key, dataSet);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertCountMonthLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                boolean isAddedLabel = false;
                Calendar date = (Calendar) startCal.clone();
                while (keyIter.hasNext()) {
                    String key = keyIter.next();
                    JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                    for (int j=0; j<dataJsn.length(); j++) {
                        if (menuCountData.containsKey(key)) {
                            LineDataSet dataSet = menuCountData.get(key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
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

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertCountQuaterLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                boolean isAddedLabel = false;
                Calendar date = (Calendar) startCal.clone();
                while (keyIter.hasNext()) {
                    String key = keyIter.next();
                    JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                    for (int j=0; j<dataJsn.length(); j++) {
                        if (menuCountData.containsKey(key)) {
                            LineDataSet dataSet = menuCountData.get(key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                            dataSet.setColor(colors[random.nextInt(colors.length)]);
                            menuCountData.put(key, dataSet);
                        }
                        if (!isAddedLabel) {
                            xVals.add(date.get(Calendar.YEAR)+"년 "+(date.get(Calendar.MONTH)/4+1)+"분기");
                            date.add(Calendar.MONTH, 3-date.get(Calendar.MONTH)%3);
                        }
                    }
                    isAddedLabel = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertCountYearLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                boolean isAddedLabel = false;
                Calendar date = (Calendar) startCal.clone();
                while (keyIter.hasNext()) {
                    String key = keyIter.next();
                    JSONArray dataJsn = recieveDataJsn.getJSONArray(key);
                    for (int j=0; j<dataJsn.length(); j++) {
                        if (menuCountData.containsKey(key)) {
                            LineDataSet dataSet = menuCountData.get(key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), key);
                            dataSet.addEntry(new Entry(dataJsn.getJSONObject(j).getInt("count"), j));
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

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertPriceHourLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                ArrayList<Integer> keys = new ArrayList<>();
                while(keyIter.hasNext()) {
                    keys.add(Integer.parseInt(keyIter.next()));
                }
                Collections.sort(keys);
                for (int i=0; i<keys.size(); i++) {
                    String key = Integer.toString(keys.get(i));
                    JSONObject dataJsn = recieveDataJsn.getJSONObject(key);
                    JSONObject menuJsn = dataJsn.getJSONObject("menu");
                    Iterator<String> menuKeyIter = menuJsn.keys();
                    ArrayList<Integer> menuKeys = new ArrayList<>();
                    while(menuKeyIter.hasNext()) {
                        menuKeys.add(Integer.parseInt(menuKeyIter.next()));
                    }
                    Collections.sort(menuKeys);

                    for (int j=0; j<menuKeys.size(); j++) {
                        if (menuCountData.containsKey(menuKeys.get(j)+"")) {
                            LineDataSet dataSet = menuCountData.get(menuKeys.get(j)+"");
                            dataSet.addEntry(new Entry(menuJsn.getJSONObject(menuKeys.get(j)+"").getInt("price"), i));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), menuKeys.get(j)+""); //TODO: 키 -> 이름
                            dataSet.addEntry(new Entry(menuJsn.getJSONObject(menuKeys.get(j)+"").getInt("price"), i));
                            dataSet.setColor(colors[random.nextInt(colors.length)]);
                            menuCountData.put(menuKeys.get(j)+"", dataSet);
                        }
                    }
                    xVals.add(key+"시");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertPriceDateLineData(JSONObject recieveDataJsn) {
            ArrayList<String> xVals = new ArrayList<String>();
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();

            int n=0;

            Iterator<String> yearKeyIter = recieveDataJsn.keys();
            ArrayList<Integer> yearKeys = new ArrayList<>();
            while(yearKeyIter.hasNext()) {
                yearKeys.add(Integer.parseInt(yearKeyIter.next()));
            }
            Collections.sort(yearKeys);
            for (int y=0; y<yearKeys.size(); y++) {
                String yearKey = yearKeys.get(y)+"";
                try {
                    JSONObject monthJsn = recieveDataJsn.getJSONObject(yearKey);
                    Iterator<String> monthKeyIter = monthJsn.keys();
                    ArrayList<Integer> monthKeys = new ArrayList<>();
                    while(monthKeyIter.hasNext()) {
                        monthKeys.add(Integer.parseInt(monthKeyIter.next()));
                    }
                    Collections.sort(monthKeys);

                    for (int m=0; m<monthKeys.size(); m++) {
                        String monthKey = monthKeys.get(m)+"";
                        JSONObject dateJsn = monthJsn.getJSONObject(monthKey);
                        Iterator<String> dateKeyIter = dateJsn.keys();
                        ArrayList<Integer> dateKeys = new ArrayList<>();
                        while(dateKeyIter.hasNext()) {
                            dateKeys.add(Integer.parseInt(dateKeyIter.next()));
                        }
                        Collections.sort(dateKeys);

                        for (int d=0; d<dateKeys.size(); d++) {
                            String dateKey = dateKeys.get(d)+"";
                            JSONObject dataJsn = dateJsn.getJSONObject(dateKey);
                            xVals.add(yearKey+"."+monthKey+"."+dateKey);

                            Iterator<String> menuKeyIter = dataJsn.getJSONObject("count").keys();
                            ArrayList<Integer> menuKeys = new ArrayList<>();
                            while(menuKeyIter.hasNext()) {
                                menuKeys.add(Integer.parseInt(menuKeyIter.next()));
                            }
                            Collections.sort(menuKeys);

                            for (int i=0; i<menuKeys.size(); i++) {
                                if (menuCountData.containsKey(menuKeys.get(i)+"")) {
                                    LineDataSet dataSet = menuCountData.get(menuKeys.get(i)+"");
                                    dataSet.addEntry(new Entry(dataJsn.getJSONObject("menu").getInt(menuKeys.get(i)+""), n));
                                }
                                else {
                                    LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), menuKeys.get(i)+""); //TODO: 키 -> 이름
                                    dataSet.addEntry(new Entry(dataJsn.getJSONObject("menu").getInt(menuKeys.get(i)+""), n));
                                    dataSet.setColor(colors[random.nextInt(colors.length)]);
                                    menuCountData.put(menuKeys.get(i)+"", dataSet);
                                }
                            }
                            n++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertPriceDayLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> keyIter = recieveDataJsn.keys();
                ArrayList<Integer> keys = new ArrayList<>();
                while(keyIter.hasNext()) {
                    keys.add(Integer.parseInt(keyIter.next()));
                }
                Collections.sort(keys);
                for (int i=0; i<keys.size(); i++) {
                    String key = Integer.toString(keys.get(i));
                    JSONObject dataJsn = recieveDataJsn.getJSONObject(key);
                    JSONObject menuJsn = dataJsn.getJSONObject("menu");
                    Iterator<String> menuKeyIter = menuJsn.keys();
                    ArrayList<Integer> menuKeys = new ArrayList<>();
                    while(menuKeyIter.hasNext()) {
                        menuKeys.add(Integer.parseInt(menuKeyIter.next()));
                    }
                    Collections.sort(menuKeys);

                    for (int j=0; j<menuKeys.size(); j++) {
                        if (menuCountData.containsKey(menuKeys.get(j)+"")) {
                            LineDataSet dataSet = menuCountData.get(menuKeys.get(j)+"");
                            dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), i));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), menuKeys.get(j)+""); //TODO: 키 -> 이름
                            dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), i));
                            dataSet.setColor(colors[random.nextInt(colors.length)]);
                            menuCountData.put(menuKeys.get(j)+"", dataSet);
                        }
                    }
                    if(key.equals("0")) {
                        xVals.add("월요일");
                    }
                    else if (key.equals("1")) {
                        xVals.add("화요일");
                    }
                    else if (key.equals("2")) {
                        xVals.add("수요일");
                    }
                    else if (key.equals("3")) {
                        xVals.add("목요일");
                    }
                    else if (key.equals("4")) {
                        xVals.add("금요일");
                    }
                    else if (key.equals("5")) {
                        xVals.add("토요일");
                    }
                    else if (key.equals("6")) {
                        xVals.add("일요일");
                    }
                    else {
                        xVals.add(key);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertPriceMonthLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> yearKeysIter = recieveDataJsn.keys();
                ArrayList<Integer> yearKeys = new ArrayList<>();
                while(yearKeysIter.hasNext()) {
                    yearKeys.add(Integer.parseInt(yearKeysIter.next()));
                }
                Collections.sort(yearKeys);
                int n=0;
                for (int y=0; y<yearKeys.size(); y++) {
                    String yearKey = Integer.toString(yearKeys.get(y));
                    JSONObject monthJsn = recieveDataJsn.getJSONObject(yearKey);
                    Iterator<String> monthKeysIter = monthJsn.keys();
                    ArrayList<Integer> monthKeys = new ArrayList<>();
                    while (monthKeysIter.hasNext()) {
                        monthKeys.add(Integer.parseInt(monthKeysIter.next()));
                    }
                    Collections.sort(monthKeys);

                    for (int m=0; m<monthKeys.size(); m++) {
                        String monthKey = Integer.toString(monthKeys.get(m));
                        JSONObject dataJsn = monthJsn.getJSONObject(monthKey);
                        JSONObject menuJsn = dataJsn.getJSONObject("menu");
                        Iterator<String> menuKeyIter = menuJsn.keys();
                        ArrayList<Integer> menuKeys = new ArrayList<>();
                        while(menuKeyIter.hasNext()) {
                            menuKeys.add(Integer.parseInt(menuKeyIter.next()));
                        }
                        Collections.sort(menuKeys);

                        for (int j=0; j<menuKeys.size(); j++) {
                            if (menuCountData.containsKey(menuKeys.get(j)+"")) {
                                LineDataSet dataSet = menuCountData.get(menuKeys.get(j)+"");
                                dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), n));
                            }
                            else {
                                LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), menuKeys.get(j)+""); //TODO: 키 -> 이름
                                dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), n));
                                dataSet.setColor(colors[random.nextInt(colors.length)]);
                                menuCountData.put(menuKeys.get(j)+"", dataSet);
                            }
                        }

                        xVals.add(yearKey+"."+monthKey);
                        n++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertPriceQuaterLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> yearKeysIter = recieveDataJsn.keys();
                ArrayList<Integer> yearKeys = new ArrayList<>();
                while(yearKeysIter.hasNext()) {
                    yearKeys.add(Integer.parseInt(yearKeysIter.next()));
                }
                Collections.sort(yearKeys);
                int n=0;
                for (int y=0; y<yearKeys.size(); y++) {
                    String yearKey = Integer.toString(yearKeys.get(y));
                    JSONObject quaterJsn = recieveDataJsn.getJSONObject(yearKey);
                    Iterator<String> quaterKeysIter = quaterJsn.keys();
                    ArrayList<Integer> quaterKeys = new ArrayList<>();
                    while (quaterKeysIter.hasNext()) {
                        quaterKeys.add(Integer.parseInt(quaterKeysIter.next()));
                    }
                    Collections.sort(quaterKeys);

                    for (int m=0; m<quaterKeys.size(); m++) {
                        String quaterKey = Integer.toString(quaterKeys.get(m));
                        JSONObject dataJsn = quaterJsn.getJSONObject(quaterKey);
                        JSONObject menuJsn = dataJsn.getJSONObject("menu");
                        Iterator<String> menuKeyIter = menuJsn.keys();
                        ArrayList<Integer> menuKeys = new ArrayList<>();
                        while(menuKeyIter.hasNext()) {
                            menuKeys.add(Integer.parseInt(menuKeyIter.next()));
                        }
                        Collections.sort(menuKeys);

                        for (int j=0; j<menuKeys.size(); j++) {
                            if (menuCountData.containsKey(menuKeys.get(j)+"")) {
                                LineDataSet dataSet = menuCountData.get(menuKeys.get(j)+"");
                                dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), n));
                            }
                            else {
                                LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), menuKeys.get(j)+""); //TODO: 키 -> 이름
                                dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), n));
                                dataSet.setColor(colors[random.nextInt(colors.length)]);
                                menuCountData.put(menuKeys.get(j)+"", dataSet);
                            }
                        }

                        xVals.add(yearKey+"년 "+quaterKey+"분기");
                        n++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        private LineData convertPriceYearLineData(JSONObject recieveDataJsn) {
            HashMap<String, LineDataSet> menuCountData = new HashMap<>();
            ArrayList<String> xVals = new ArrayList<String>();
            try {
                Iterator<String> yearKeysIter = recieveDataJsn.keys();
                ArrayList<Integer> yearKeys = new ArrayList<>();
                while(yearKeysIter.hasNext()) {
                    yearKeys.add(Integer.parseInt(yearKeysIter.next()));
                }
                Collections.sort(yearKeys);
                int n=0;
                for (int y=0; y<yearKeys.size(); y++) {
                    String yearKey = Integer.toString(yearKeys.get(y));
                    JSONObject dataJsn = recieveDataJsn.getJSONObject(yearKey);
                    JSONObject menuJsn = dataJsn.getJSONObject("menu");
                    Iterator<String> menuKeyIter = menuJsn.keys();
                    ArrayList<Integer> menuKeys = new ArrayList<>();
                    while(menuKeyIter.hasNext()) {
                        menuKeys.add(Integer.parseInt(menuKeyIter.next()));
                    }
                    Collections.sort(menuKeys);

                    for (int j=0; j<menuKeys.size(); j++) {
                        if (menuCountData.containsKey(menuKeys.get(j)+"")) {
                            LineDataSet dataSet = menuCountData.get(menuKeys.get(j)+"");
                            dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), n));
                        }
                        else {
                            LineDataSet dataSet = new LineDataSet(new ArrayList<Entry>(), menuKeys.get(j)+""); //TODO: 키 -> 이름
                            dataSet.addEntry(new Entry(menuJsn.getInt(menuKeys.get(j) + ""), n));
                            dataSet.setColor(colors[random.nextInt(colors.length)]);
                            menuCountData.put(menuKeys.get(j)+"", dataSet);
                        }
                    }

                    xVals.add(yearKey+"년 ");
                    n++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<LineDataSet> countData = new ArrayList<>();
            for (LineDataSet dataSet : menuCountData.values()) {
                countData.add(dataSet);
            }
            return new LineData(xVals, countData);
        }

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.setMessage("서버에서 데이터를 가져오는 중입니다.");
            dialog.show();
        }

        @Override
        protected LineData doInBackground(Void... params) {
            HashMap<String, Object> requestParams = new HashMap<>();
            requestParams.put("startDate", Data.onlyDateFormat.format(startCal.getTime()));
            requestParams.put("endDate", Data.onlyDateFormat.format(endCal.getTime()));
            requestParams.put("unit", Integer.toString(unit));
            ArrayList<Menu> menus = new ArrayList<>();
            menus.addAll(cutletAdapter.getSelectedList());
            menus.addAll(riceAdapter.getSelectedList());
            menus.addAll(noodleAdapter.getSelectedList());
            menus.addAll(etcAdapter.getSelectedList());
            JSONArray menusArr = new JSONArray();
            for (int i=0; i<menus.size(); i++) {
                menusArr.put(menus.get(i).getId());
            }
            requestParams.put("menus", menusArr.toString());

            JSONObject recieveDataJsn = null;
            try {
                recieveDataJsn = new JSONObject(Http.post(Data.SERVER_URL + "statistic/linechart", requestParams));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch(unit) {
                default:
                    return new LineData();
                case 1:
                    return type==Statistic.TYPE_SALE?convertPriceHourLineData(recieveDataJsn) :convertCountHourLineData(recieveDataJsn);
                case 2:
                    return type==Statistic.TYPE_SALE?convertPriceDateLineData(recieveDataJsn) :convertCountDateLineData(recieveDataJsn);
                case 3:
                    return type==Statistic.TYPE_SALE?convertPriceDayLineData(recieveDataJsn) :convertCountDayLineData(recieveDataJsn);
                case 4:
                    return type==Statistic.TYPE_SALE?convertPriceMonthLineData(recieveDataJsn) :convertCountMonthLineData(recieveDataJsn);
                case 5:
                    return type==Statistic.TYPE_SALE?convertPriceQuaterLineData(recieveDataJsn) :convertCountQuaterLineData(recieveDataJsn);
                case 6:
                    return type==Statistic.TYPE_SALE?convertPriceYearLineData(recieveDataJsn) :convertCountYearLineData(recieveDataJsn);
            }
        }

        @Override
        protected void onPostExecute(LineData result) {
            result.setValueTextSize(16.0f);
            setLineChartData(result);
            dialog.dismiss();
        }
    }

    private class GetAllMenuList extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.setMessage("서버에서 메뉴 데이터를 가져오는 중입니다.");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            cutlets.clear();
            rices.clear();
            noodles.clear();
            etcs.clear();
            try {
                JSONArray menuJsn = new JSONArray(Http.get(Data.SERVER_URL+"menu/all", null));
                for (int i=0; i<menuJsn.length(); i++) {
                    JSONObject jo = menuJsn.getJSONObject(i);
                    switch(jo.getInt("category_id")) {
                        case 1:
                            cutlets.add(new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(1)));
                            break;
                        case 2:
                            rices.add(new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(2)));
                            break;
                        case 3:
                            noodles.add(new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(3)));
                            break;
                        case 4:
                            etcs.add(new Menu(jo.getInt("id"), jo.getString("name"), jo.getInt("price"), Data.categories.get(4)));
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            cutletAdapter.notifyDataSetChanged();
            riceAdapter.notifyDataSetChanged();
            noodleAdapter.notifyDataSetChanged();
            etcAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    }
}
