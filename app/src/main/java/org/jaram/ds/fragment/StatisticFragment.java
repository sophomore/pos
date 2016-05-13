package org.jaram.ds.fragment;

import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import org.jaram.ds.R;
import org.jaram.ds.factories.StatisticChartFactory;
import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.models.result.StatisticResult;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;

import butterknife.Bind;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 10..
 */
public class StatisticFragment extends BaseFragment {

    @Bind(R.id.chart) LineChart chartView;

    private StatisticManager manager;
    private Subscription subscription;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public void onDetach() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDetach();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_statistic;
    }

    @Override
    protected void setupLayout(View view) {
        chartView.setDoubleTapToZoomEnabled(false);
        chartView.setPinchZoom(false);
        chartView.setDescription(null);
        chartView.setNoDataTextDescription(getString(R.string.message_guide_statistic));

        manager = StatisticManager.getInstance(getActivity());
        subscription = manager.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshChart);
    }

    protected void refreshChart(StatisticManager manager) {
        manager.getStatisticData()
                .retryWhen(RxUtils::exponentialBackoff)
                .subscribeOn(Schedulers.newThread())
                .map(this::convertLineDataFromJson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setChartData, SLog::e);
    }

    protected void setChartData(LineData data) {
        if (data == null) {
            SLog.e(this, "chart data is null");
            return;
        }

        data.setValueTextSize(16.0f);
        chartView.setData(data);
        chartView.invalidate();
    }

    @SuppressWarnings("ConstantConditions")
    private LineData convertLineDataFromJson(StatisticResult result) {
        return StatisticChartFactory.convertLineData(result.getResult(), manager.getStart(),
                result.getUnit(), result.getType());
    }
}
