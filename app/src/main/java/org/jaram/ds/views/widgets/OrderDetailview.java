package org.jaram.ds.views.widgets;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jaram.ds.Data;
import org.jaram.ds.R;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.result.SimpleApiResult;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.GridSpaceItemDecoration;
import org.jaram.ds.views.adapters.DetailOrderMenuAdapter;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by jdekim43 on 2016. 5. 17..
 */
public class OrderDetailView extends LinearLayout {

    @BindView(R.id.payPrice) TextView priceView;
    @BindView(R.id.orderDate) TextView dateView;
    @BindView(R.id.orderMenuList) BaseRecyclerView listView;

    @BindDimen(R.dimen.order_detail_ordermenu_item_spacing) int itemSpacing;

    private DetailOrderMenuAdapter adapter;

    private Order order;

    private Action1<Order> deleteListener;
    private Action1<Order> modifyListener;

    public OrderDetailView(Context context) {
        this(context, null);
    }

    public OrderDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OrderDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.view_order_detail, this);
        ButterKnife.bind(this);
        init();
    }

    public void setOrder(Order order) {
        this.order = order;
        draw();
    }

    public void clear() {
        priceView.setText("");
        dateView.setText("");
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    public void setOnDeleteListener(Action1<Order> listener) {
        this.deleteListener = listener;
    }

    public void setOnModifyListener(Action1<Order> listener) {
        this.modifyListener = listener;
    }

    @OnClick(R.id.printReceipt)
    protected void printReceipt() {
        Api.with(getContext()).printReceipt(order.getId())
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxUtils::doNothing, SLog::e);
    }

    @OnClick(R.id.printStatement)
    protected void printStatement() {
        Api.with(getContext()).printStatement(order.getId())
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxUtils::doNothing, SLog::e);
    }

    @OnClick(R.id.delete)
    protected void deleteOrder() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.message_confirm_delete_order)
                .setPositiveButton(R.string.label_yes, (dialog, which) -> applyDeleteOrder())
                .setNegativeButton(R.string.label_no, null)
                .show();
    }

    protected void applyDeleteOrder() {
        Api.with(getContext()).deleteOrder(order.getId())
                .retryWhen(RxUtils::exponentialBackoff)
                .filter(SimpleApiResult::isSuccess)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (deleteListener != null) {
                        deleteListener.call(order);
                    }
                    clear();
                }, SLog::e);
    }

    protected void init() {
        adapter = new DetailOrderMenuAdapter();
        listView.setAdapter(adapter);
        listView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        listView.addItemDecoration(new GridSpaceItemDecoration(4, itemSpacing, false));
        adapter.asObservable()
                .filter(orderMenu -> modifyListener != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderMenu -> modifyListener.call(orderMenu.getOrder()));
    }

    protected void draw() {
        clear();
        if (order == null) {
            return;
        }

        priceView.setText(getContext().getString(R.string.format_price_pay, order.getTotalPrice()));
        dateView.setText(getContext().getString(R.string.format_order_date,
                Data.dateFormat.format(order.getDate())));

        adapter.addAll(order.getOrderMenus());
        adapter.notifyDataSetChanged();
    }
}
