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
import org.jaram.ds.util.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class SimpleStatisticFragment extends BaseFragment {

    @Bind(R.id.chart) BarChart chart;
    @Bind(R.id.infoView) View infoContainer;
    @Bind(R.id.rangeCash) TextView rangeCashView;
    @Bind(R.id.rangeCard) TextView rangeCardView;
    @Bind(R.id.rangeTotal) TextView rangeTotalView;
    @Bind(R.id.monthCash) TextView monthCashView;
    @Bind(R.id.monthCard) TextView monthCardView;
    @Bind(R.id.monthTotal) TextView monthTotalView;

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
        chart.setNoDataTextDescription("기간을 입력하시고 검색버튼을 눌러주세요");
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                float[] values = ((BarEntry) e).getVals();
                monthCashView.setText(StringUtils.format("%,d원", (int) values[0]));
                monthCardView.setText(StringUtils.format("%,d원", (int) values[1]));
                monthTotalView.setText(StringUtils.format("%,d원", (int) (values[0] + values[1] + values[2])));
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
        rangeCashView.setText(StringUtils.format("%,d원", cash));
        rangeCardView.setText(StringUtils.format("%,d원", card));
        rangeTotalView.setText(StringUtils.format("%,d원", total));
    }

    private BarData convertBarDataFromJson(SimpleStatisticResult result) {
        return StatisticChartFactory.convertBarData(getActivity(), result.getResult(),
                manager.getStart(), this::setTotalView);
    }
}
