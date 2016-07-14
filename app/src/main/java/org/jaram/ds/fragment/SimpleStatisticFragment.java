package org.jaram.ds.fragment;

import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;

import org.jaram.ds.R;
import org.jaram.ds.factories.StatisticChartFactory;
import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class SimpleStatisticFragment extends BaseFragment {

    @BindView(R.id.chart) BarChart chartView;

    private StatisticManager manager;

    public static SimpleStatisticFragment newInstance() {
        return new SimpleStatisticFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_simple_statistic;
    }

    @Override
    protected void setupLayout(View view) {
        chartView.setDescription(null);
        chartView.setNoDataTextDescription(getString(R.string.message_guide_simple_statistic));
        chartView.setExtraBottomOffset(12);
        chartView.setScaleXEnabled(true);
        chartView.setScaleYEnabled(false);
        setupXAxis(chartView.getXAxis());
        setupYAxis(chartView.getAxisLeft());
        setupYAxis(chartView.getAxisRight());
        setupLegend(chartView.getLegend());

        manager = StatisticManager.getInstance(getActivity());
        addSubscription(manager.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshChartData));
    }

    protected void setupXAxis(XAxis axis) {
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setTextSize(20);
        axis.setYOffset(24);
    }

    protected void setupYAxis(YAxis axis) {
        axis.setTextSize(16);
        axis.setXOffset(12);
        axis.setDrawGridLines(false);
        axis.setAxisMinValue(0);
    }

    protected void setupLegend(Legend legend) {
        legend.setTextSize(16);
        legend.setXEntrySpace(12);
    }

    protected void setChartData(BarData data) {
        if (data == null) {
            SLog.e(this, "chart data is null");
            return;
        }

        chartView.setData(data);
        chartView.setVisibleXRangeMaximum(20);
        chartView.invalidate();
    }

    protected void refreshChartData(StatisticManager manager) {
        addSubscription(manager.getSimpleStatisticData()
                .retryWhen(RxUtils::exponentialBackoff)
                .map(StatisticChartFactory::convertSimpleStatisticData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setChartData, SLog::e));
    }
}
