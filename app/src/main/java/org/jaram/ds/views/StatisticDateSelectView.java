package org.jaram.ds.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.managers.StatisticManager;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 5. 12..
 */
public class StatisticDateSelectView extends LinearLayout {

    @Bind(R.id.startDate) Button startDateButton;
    @Bind(R.id.endDate) Button endDateButton;

    private StatisticManager manager;

    public StatisticDateSelectView(Context context) {
        this(context, null);
    }

    public StatisticDateSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatisticDateSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public StatisticDateSelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.statistic_date, this);
        ButterKnife.bind(this);
        manager = StatisticManager.getInstance(context);
        manager.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setDateFromManager);
        setDateFromManager(manager);
    }

    @OnClick(R.id.startDate)
    protected void showSelectStartDateDialog() {
        Calendar date = manager.getStart();
        new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, monthOfYear, dayOfMonth);
                    if (manager.getEnd().before(selectedDate)) {
                        Toast.makeText(getContext(), "시작 날짜가 종료 날짜보다 이후일 수 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    date.set(year, monthOfYear, dayOfMonth);
                    startDateButton.setText(Data.onlyDateFormat.format(date.getTime()));
                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnClick(R.id.endDate)
    protected void showSelectEndDateDialog() {
        Calendar date = manager.getEnd();
        new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, monthOfYear, dayOfMonth);
                    if (manager.getStart().after(selectedDate)) {
                        Toast.makeText(getContext(), "종료 날짜가 시작 날짜보다 이전일 수 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    date.set(year, monthOfYear, dayOfMonth);
                    endDateButton.setText(Data.onlyDateFormat.format(date.getTime()));
                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @OnClick(R.id.submit)
    protected void submit(View v) {
        StatisticManager.getInstance(getContext()).notifyUpdated();
    }

    private void setDateFromManager(StatisticManager manager) {
        startDateButton.setText(Data.onlyDateFormat.format(manager.getStart().getTime()));
        endDateButton.setText(Data.onlyDateFormat.format(manager.getEnd().getTime()));
    }
}
