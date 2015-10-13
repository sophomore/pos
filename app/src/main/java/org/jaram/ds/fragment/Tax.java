package org.jaram.ds.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.util.Http;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by kjydiary on 15. 10. 5..
 */
public class Tax extends Fragment {

    Callbacks callbacks;
    Calendar startCal;
    Calendar endCal;
    BarChart chart;

    private static Tax view;
    public static Tax getInstance() {
        if (view == null) {
            view = new Tax();
        }
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tax, container, false);

        RelativeLayout actionbaritem = (RelativeLayout)inflater.inflate(R.layout.statistic_date, null, false);

        final Button startDate = (Button)actionbaritem.findViewById(R.id.startDate);
        final Button endDate = (Button) actionbaritem.findViewById(R.id.endDate);
        ImageButton analyticsBtn = (ImageButton)actionbaritem.findViewById(R.id.analyticsBtn);
        callbacks.addViewAtActionBar(actionbaritem, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        startCal.setTime(new Date());
        endCal.setTime(new Date());

        startDate.setText(Data.onlyDateFormat.format(startCal.getTime()));
        endDate.setText(Data.onlyDateFormat.format(startCal.getTime()));

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), R.style.Base_V21_Theme_AppCompat_Light_Dialog,
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
                new DatePickerDialog(getActivity(), R.style.Base_V21_Theme_AppCompat_Light_Dialog,
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
                new GetAnalyticsData().execute();
            }
        });

        final LinearLayout infoBox = (LinearLayout)view.findViewById(R.id.infoBox);
        final ImageButton infoBtn = (ImageButton)view.findViewById(R.id.infoBtn);
        final GridLayout infoView = (GridLayout)view.findViewById(R.id.infoView);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infoView.getVisibility() != View.VISIBLE) {
                    infoView.setVisibility(View.VISIBLE);
                    infoBtn.setImageResource(R.drawable.ic_expand_more_white_48dp);
                }
                else {
                    infoView.setVisibility(View.GONE);
                    infoBtn.setImageResource(R.drawable.ic_expand_less_white_48dp);
                }
            }
        });

        chart = (BarChart)view.findViewById(R.id.tax_barChart);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setDescription(null);
        return view;
    }

    private void setBarData(BarData bardata) {
        chart.setData(bardata);
        chart.invalidate();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    public interface Callbacks {
        void addViewAtActionBar(View view, ViewGroup.LayoutParams params);
    }

    private class GetAnalyticsData extends AsyncTask<Void, Void, BarData> {

        @Override
        protected BarData doInBackground(Void... params) {
            HashMap<String, Object> param = new HashMap<String, Object>();
            param.put("startDate", Data.onlyDateFormat.format(startCal.getTime()));
            param.put("endDate", Data.onlyDateFormat.format(startCal.getTime()));
            param.put("unit", 4);
            param.put("menus", "[]");
            BarData barData = null;
            try {
                JSONObject yearJsn = new JSONObject(Http.post(Data.SERVER_URL + "statistic/unit_menu_sum", param));
                ArrayList<String> xVals = new ArrayList<>();
                ArrayList<BarEntry> yVals = new ArrayList<>();

                Iterator<String> yearKeysIter = yearJsn.keys();
                ArrayList<Integer> yearKeys = new ArrayList<>();
                while(yearKeysIter.hasNext()) {
                    yearKeys.add(Integer.parseInt(yearKeysIter.next()));
                }
                Collections.sort(yearKeys);
                int n=0;
                for (int y=0; y<yearKeys.size(); y++) {
                    String yearKey = Integer.toString(yearKeys.get(y));
                    JSONObject monthJsn = yearJsn.getJSONObject(yearKey);
                    Iterator<String> monthKeysIter = monthJsn.keys();
                    ArrayList<Integer> monthKeys = new ArrayList<>();
                    while (monthKeysIter.hasNext()) {
                        monthKeys.add(Integer.parseInt(monthKeysIter.next()));
                    }
                    Collections.sort(monthKeys);

                    for (int m=0; m<monthKeys.size(); m++) {
                        String monthKey = Integer.toString(monthKeys.get(m));
                        JSONObject dataJsn = monthJsn.getJSONObject(monthKey);
                        float[] vals = {(float)dataJsn.getInt("cashtotal"),
                                (float)dataJsn.getInt("cardtotal"),
                                (float)(dataJsn.getInt("total")-dataJsn.getInt("cardtotal")-dataJsn.getInt("cashtotal"))};
                        BarEntry yVal = new BarEntry(vals, n);
                        yVals.add(yVal);
                        xVals.add(yearKey+"."+monthKey);
                        n++;
                    }
                }
                BarDataSet dataSet = new BarDataSet(yVals, "판매금액");
                dataSet.setHighLightColor(getResources().getColor(R.color.dark));
                dataSet.setColors(new int[]{Color.parseColor("#FFB14A"), Color.parseColor("#FE7E39"), Color.parseColor("#E5404C")});
                dataSet.setValueTextColor(getResources().getColor(R.color.dark));
                dataSet.setValueTextSize(16.0f);
                dataSet.setStackLabels(new String[] {"현금", "카드", "기타"});
                ArrayList<BarDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet);
                barData = new BarData(xVals, dataSets);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return barData;
        }

        @Override
        protected void onPostExecute(BarData result) {
            setBarData(result);
        }
    }
}
