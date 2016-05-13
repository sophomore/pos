package org.jaram.ds.fragment;

import android.view.View;
import android.widget.Button;

import org.jaram.ds.R;
import org.jaram.ds.managers.StatisticManager;
import org.jaram.ds.views.MenuCollapseListView;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 5. 10..
 */
public class StatisticSettingDrawerFragment extends BaseFragment {

    @Bind(R.id.typeSale) Button typeSaleButton;
    @Bind(R.id.typeCount) Button typeCountButton;
    @Bind(R.id.unitHour) Button unitHourButton;
    @Bind(R.id.unitDate) Button unitDateButton;
    @Bind(R.id.unitDay) Button unitDayButton;
    @Bind(R.id.unitMonth) Button unitMonthButton;
    @Bind(R.id.unitQuarter) Button unitQuarterButton;
    @Bind(R.id.unitYear) Button unitYearButton;
    @Bind(R.id.menuList) MenuCollapseListView menuListView;

    private StatisticManager manager;

    public static StatisticSettingDrawerFragment newInstance() {
        return new StatisticSettingDrawerFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.drawer_statistic_setting;
    }

    @Override
    protected void setupLayout(View view) {
        manager = StatisticManager.getInstance(getActivity());
        menuListView.setAccentMenuList(manager.getSelectedMenu());
        menuListView.setOnClickMenuListener(manager::addSelectedMenu);

        manager.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setSettingFromManager);
        setSettingFromManager(manager);
    }

    @OnClick({R.id.typeSale, R.id.typeCount})
    protected void setTypeByView(View v) {
        switch (v.getId()) {
            case R.id.typeSale:
                manager.setType(StatisticManager.Type.SALE);
                break;
            case R.id.typeCount:
                manager.setType(StatisticManager.Type.COUNT);
                break;
        }
        unSelectModeAllType();
        v.setSelected(true);
    }

    @OnClick({R.id.unitHour, R.id.unitDate, R.id.unitDay,
            R.id.unitMonth, R.id.unitQuarter, R.id.unitYear})
    protected void setUnitByView(View v) {
        switch (v.getId()) {
            case R.id.unitHour:
                manager.setUnit(StatisticManager.Unit.HOUR);
                break;
            case R.id.unitDate:
                manager.setUnit(StatisticManager.Unit.DATE);
                break;
            case R.id.unitDay:
                manager.setUnit(StatisticManager.Unit.DAY);
                break;
            case R.id.unitMonth:
                manager.setUnit(StatisticManager.Unit.MONTH);
                break;
            case R.id.unitQuarter:
                manager.setUnit(StatisticManager.Unit.QUARTER);
                break;
            case R.id.unitYear:
                manager.setUnit(StatisticManager.Unit.YEAR);
                break;
        }
        unSelectModeAllUnit();
        v.setSelected(true);
    }

    @OnClick(R.id.statistic)
    protected void submit() {
        StatisticManager.getInstance(getActivity()).notifyUpdated();
    }

    protected void setSettingFromManager(StatisticManager manager) {
        switch (manager.getType()) {
            case COUNT:
                setTypeByView(typeCountButton);
                break;
            case SALE:
                setTypeByView(typeSaleButton);
                break;
        }

        switch (manager.getUnit()) {
            case HOUR:
                setUnitByView(unitHourButton);
                break;
            case DATE:
                setUnitByView(unitDateButton);
                break;
            case DAY:
                setUnitByView(unitDayButton);
                break;
            case MONTH:
                setUnitByView(unitMonthButton);
                break;
            case QUARTER:
                setUnitByView(unitQuarterButton);
                break;
            case YEAR:
                setUnitByView(unitYearButton);
                break;
        }

        menuListView.notifyAllDataSetChanged();
    }

    private void unSelectModeAllType() {
        typeSaleButton.setSelected(false);
        typeCountButton.setSelected(false);
    }

    private void unSelectModeAllUnit() {
        unitHourButton.setSelected(false);
        unitDateButton.setSelected(false);
        unitDayButton.setSelected(false);
        unitMonthButton.setSelected(false);
        unitQuarterButton.setSelected(false);
        unitYearButton.setSelected(false);
    }
}
