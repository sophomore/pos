package org.jaram.ds.fragment;

import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import org.jaram.ds.R;
import org.jaram.ds.factories.StatisticChartFactory;
import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 10..
 */
public class StatisticFragment extends BaseFragment {

    @BindView(R.id.chart) LineChart chartView;

    private StatisticManager manager;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_statistic;
    }

    @Override
    protected void setupLayout(View view) {
        chartView.setDescription(null);
        chartView.setNoDataTextDescription(getString(R.string.message_guide_statistic));
        chartView.setExtraBottomOffset(12);
        chartView.setScaleEnabled(true);
        setupXAxis(chartView.getXAxis());
        setupYAxis(chartView.getAxisLeft());
        setupYAxis(chartView.getAxisRight());
        setupLegend(chartView.getLegend());

        manager = StatisticManager.getInstance(getActivity());
        addSubscription(manager.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshChart));
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

    protected void refreshChart(StatisticManager manager) {
        addSubscription(manager.getStatisticData()
                .retryWhen(RxUtils::exponentialBackoff)
                .subscribeOn(Schedulers.newThread())
                .map(StatisticChartFactory::convertStatisticData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setChartData, SLog::e));
    }

    protected void setChartData(LineData data) {
        if (data == null) {
            SLog.e(this, "chart data is null");
            return;
        }

        data.setValueTextSize(16.0f);
        chartView.setData(data);
        chartView.setVisibleXRangeMaximum(20);
        chartView.invalidate();
    }
}
