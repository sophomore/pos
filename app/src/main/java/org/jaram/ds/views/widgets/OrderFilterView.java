package org.jaram.ds.views.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.jaram.ds.R;
import org.jaram.ds.managers.OrderManager;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.Pay;
import org.jaram.ds.views.adapters.CollapseMenuAdapter;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import rx.functions.Action0;

/**
 * Created by jdekim43 on 2016. 5. 19..
 */
public class OrderFilterView extends LinearLayout {

    @BindView(R.id.price) EditPriceView priceView;
    @BindView(R.id.priceFilterCriteria) Spinner priceCriteriaView;
    @BindViews({R.id.cash, R.id.card, R.id.service, R.id.credit}) List<CheckBox> payMethodViews;
    @BindView(R.id.date) MaterialCalendarView dateView;
    @BindView(R.id.menuList) MenuCollapseListView menuListView;

    private OrderManager manager;

    private Action0 applyListener;
    private Action0 resetListener;

    public OrderFilterView(Context context) {
        this(context, null);
    }

    public OrderFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OrderFilterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.view_order_filter, this);
        ButterKnife.bind(this);
        manager = OrderManager.getInstance(getContext());
        setDateAtToday();
        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && priceView.isFocused()) {
            Rect outRect = new Rect();
            priceView.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                priceView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @OnClick(R.id.apply)
    public void apply() {
        if (applyListener != null) {
            applyListener.call();
        }
    }

    @OnClick(R.id.reset)
    public void reset() {
        manager.resetFilter();
        priceView.setNumber(0);
        priceCriteriaView.setSelection(0);
        ButterKnife.apply(payMethodViews, ((view, index) -> view.setChecked(false)));
        setDateAtToday();
        menuListView.notifyAllDataSetChanged();

        if (resetListener != null) {
            resetListener.call();
        }
    }

    public void setOnApplyListener(Action0 listener) {
        this.applyListener = listener;
    }

    public void setOnResetListener(Action0 listener) {
        this.resetListener = listener;
    }

    @OnTextChanged(R.id.price)
    protected void onChangedPrice() {
        manager.setPrice(priceView.getNumber());
    }

    @OnItemSelected(R.id.priceFilterCriteria)
    protected void onSelectedPriceCriteria(AdapterView<?> parent, View view, int position, long id) {
        manager.setPriceCriteria(OrderManager.PriceFilterCriteria.values()[position]);
    }

    @OnCheckedChanged({R.id.cash, R.id.card, R.id.service, R.id.credit})
    protected void onCheckedChangedPayMethod(CompoundButton buttonView, boolean isChecked) {
        Pay pay = null;
        switch (buttonView.getId()) {
            case R.id.cash:
                pay = Pay.CASH;
                break;
            case R.id.card:
                pay = Pay.CARD;
                break;
            case R.id.service:
                pay = Pay.SERVICE;
                break;
            case R.id.credit:
                pay = Pay.CREDIT;
                break;
        }

        if (pay != null) {
            if (isChecked) {
                manager.addPayMethod(pay);
            } else {
                manager.removePayMethod(pay);
            }
        }
    }

    @OnClick(R.id.today)
    protected void setDateAtToday() {
        dateView.setCurrentDate(new Date());
        dateView.setSelectedDate(new Date());
    }

    protected void init() {
        priceCriteriaView.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                OrderManager.PriceFilterCriteria.values()));
        dateView.setOnDateChangedListener((widget, date, selected) ->
                manager.setDate(date.getDate()));
        menuListView.setOnClickMenuListener(menu -> {
            if (manager.containsMenu(menu)) {
                manager.removeMenu(menu);
            } else {
                manager.addMenu(menu);
            }
        });
        menuListView.setAccentMenuList(manager.getMenus());
    }
}