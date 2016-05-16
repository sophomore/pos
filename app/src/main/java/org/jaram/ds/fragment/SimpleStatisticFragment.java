package org.jaram.ds.fragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.jaram.ds.R;
import org.jaram.ds.factories.StatisticChartFactory;
import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.models.result.SimpleStatisticResult;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class SimpleStatisticFragment extends BaseFragment {

    @BindView(R.id.chart) BarChart chart;
    @BindView(R.id.infoView) View infoContainer;
    @BindView(R.id.rangeCash) TextView rangeCashView;
    @BindView(R.id.rangeCard) TextView rangeCardView;
    @BindView(R.id.rangeTotal) TextView rangeTotalView;
    @BindView(R.id.monthCash) TextView monthCashView;
    @BindView(R.id.monthCard) TextView monthCardView;
    @BindView(R.id.monthTotal) TextView monthTotalView;

    private StatisticManager manager;
    private Subscription subscription;

    public static SimpleStatisticFragment newInstance() {
        return new SimpleStatisticFragment();
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
        return R.layout.fragment_simple_statistic;
    }

    @Override
    protected void setupLayout(View view) {
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setDescription(null);
        chart.setNoDataTextDescription(getString(R.string.message_guide_simple_statistic));
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                float[] values = ((BarEntry) e).getVals();
                monthCashView.setText(getString(R.string.format_money, (int) values[0]));
                monthCardView.setText(getString(R.string.format_money, (int) values[1]));
                monthTotalView.setText(getString(R.string.format_money,
                        (int) (values[0] + values[1] + values[2])));
            }

            @Override
            public void onNothingSelected() {

            }
        });

        manager = StatisticManager.getInstance(getActivity());
        subscription = manager.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshChartData);
    }

    @OnClick(R.id.toggleInfo)
    protected void toggleInfoBox(ImageButton button) {
        if (infoContainer.getVisibility() == View.VISIBLE) {
            infoContainer.setVisibility(View.GONE);
            button.setImageResource(R.drawable.ic_expand_less_white_48dp);
        } else {
            infoContainer.setVisibility(View.VISIBLE);
            button.setImageResource(R.drawable.ic_expand_more_white_48dp);
        }
    }

    protected void setChartData(BarData data) {
        if (data == null) {
            SLog.e(this, "chart data is null");
            return;
        }

        if (data instanceof StatisticChartFactory.SimpleStatisticBarData) {
            StatisticChartFactory.SimpleStatisticBarData statisticBarData
                    = (StatisticChartFactory.SimpleStatisticBarData) data;
            setTotalView(statisticBarData.getRangeCash(),
                    statisticBarData.getRangeCard(),
                    statisticBarData.getRangeTotal());
        }

        chart.setData(data);
        chart.invalidate();
    }

    protected void refreshChartData(StatisticManager manager) {
        manager.getSimpleStatisticData()
                .retryWhen(RxUtils::exponentialBackoff)
                .subscribeOn(Schedulers.newThread())
                .map(this::convertBarDataFromJson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setChartData, SLog::e);
    }

    protected void setTotalView(int cash, int card, int total) {
        rangeCashView.setText(getString(R.string.format_money, cash));
        rangeCardView.setText(getString(R.string.format_money, card));
        rangeTotalView.setText(getString(R.string.format_money, total));
    }

    private BarData convertBarDataFromJson(SimpleStatisticResult result) {
        return StatisticChartFactory.convertBarData(getActivity(), result.getResult(),
                manager.getStart());
    }
}
