package org.jaram.ds.fragment;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.activities.OrderActivity;
import org.jaram.ds.Data;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.DateUtil;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.adapters.PaginationAdapter;
import org.jaram.ds.views.widgets.PaginationView;
import org.jaram.ds.views.VerticalSpaceItemDecoration;
import org.jaram.ds.views.adapters.OrderAdapter;
import org.jaram.ds.views.adapters.DetailOrderMenuAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class OrderManageFragment extends BaseFragment {

    @BindView(R.id.orderList) PaginationView<Order> orderListView;
    @BindView(R.id.orderDetail) View orderDetailView;
    @BindView(R.id.totalprice) TextView orderTotalPriceView;
    @BindView(R.id.date) TextView orderDateView;
    @BindView(R.id.orderMenuList) RecyclerView orderMenuListView;
    @BindView(R.id.order2) View largeOrderButton;

    @BindDimen(R.dimen.order_list_item_spacing) int itemSpacing;

    private OrderAdapter orderAdapter;
    private DetailOrderMenuAdapter detailOrderMenuAdapter;

    private Order selectedOrder;

    public static OrderManageFragment newInstance() {
        return new OrderManageFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_order_manage;
    }

    @Override
    protected void setupLayout(View view) {
        orderAdapter = new OrderAdapter();
        detailOrderMenuAdapter = new DetailOrderMenuAdapter();

        orderListView.setAdapter(orderAdapter);
        orderListView.setLoader(this::loadOrder);
        orderListView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacing));
        orderMenuListView.setAdapter(detailOrderMenuAdapter);

        orderAdapter.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::selectOrder);
        detailOrderMenuAdapter.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderMenu -> orderAdapter.notifySelectedItemChanged());

        //TODO: 주문 검색 custom view
    }

    @OnClick({R.id.order, R.id.order2})
    protected void onClickOrderButton() {
        startActivity(new Intent(getActivity(), OrderActivity.class));
    }

    @OnClick(R.id.printReceipt)
    protected void printReceipt() {
        Api.with(getActivity()).printReceipt(selectedOrder.getId())
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                }, SLog::e);
    }

    @OnClick(R.id.printStatement)
    protected void printStatement() {
        Api.with(getActivity()).printStatement(selectedOrder.getId())
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                }, SLog::e);
    }

    @OnClick(R.id.delete)
    protected void deleteOrder() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.message_confirm_delete_order)
                .setPositiveButton(R.string.label_yes, ((dialog, which) ->
                        Api.with(getActivity()).deleteOrder(selectedOrder.getId())
                                .retryWhen(RxUtils::exponentialBackoff)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(result -> {
                                    if (result.isSuccess()) {
                                        orderAdapter.remove(selectedOrder);
                                        selectedOrder = null;
                                        refreshOrderDetailView();
                                        orderAdapter.notifyDataSetChanged();
                                    }
                                }, SLog::e)
                ))
                .setNegativeButton(R.string.label_no, null)
                .show();
    }

    protected Observable<PaginationData<Order>> loadOrder(int page) {
        Observable<List<Order>> observable = orderAdapter.getItemCount() == 0
                ? Api.with(getActivity()).getOrder()
                : Api.with(getActivity()).getMoreOrders(orderAdapter.getItem(orderAdapter.getItemCount() - 1).getDate());

        return observable
                .map(result -> {
                    PaginationData<Order> paginationData = new PaginationData<>(result);
                    paginationData.setmNext(result.size() > 0 ? "hasNext" : "");
                    return paginationData;
                })
                .map(data -> {
                    PaginationAdapter<Order> adapter = orderAdapter;
                    Calendar lastDate = Calendar.getInstance();
                    if (adapter.getListSize() > 0) {
                        lastDate.setTime(adapter.getItem(adapter.getListSize() - 1).getDate());
                    } else {
                        lastDate.add(Calendar.YEAR, 1);
                    }
                    DateUtil.dropTime(lastDate);
                    for (int i = 0; i < data.getResults().size(); i++) {
                        Calendar date = Calendar.getInstance();
                        Date receiveDate = data.getResults().get(i).getDate();
                        if (receiveDate == null) {
                            continue;
                        }
                        date.setTime(receiveDate);
                        DateUtil.dropTime(date);
                        if (lastDate.after(date)) {
                            data.getResults().add(i, createHeaderItem(date));
                        }
                        lastDate = date;
                    }
                    return data;
                });
    }

    protected void selectOrder(Order order) {
        selectedOrder = order;
        refreshOrderDetailView();
    }

    protected Order createHeaderItem(Calendar date) {
        Order order = new Order();
        order.setId(OrderAdapter.VIEW_TYPE_HEADER);
        order.setDate(date.getTime());
        return order;
    }

    private void refreshOrderDetailView() {
        invalidateOrderDetailView();

        detailOrderMenuAdapter.clear();
        if (selectedOrder != null) {
            detailOrderMenuAdapter.addAll(selectedOrder.getOrderMenus());
        }

        orderTotalPriceView.setText(selectedOrder == null ? "" : getString(R.string.format_money,
                selectedOrder.getTotalPrice()));
        orderDateView.setText(selectedOrder == null ? "" : Data.dateFormat.format(selectedOrder.getDate()));

        detailOrderMenuAdapter.notifyDataSetChanged();
    }

    private void refresh() {
        orderAdapter.clear();
        orderListView.refresh(true);
    }

    private void invalidateOrderDetailView() {
        if (selectedOrder == null) {
            largeOrderButton.setVisibility(View.VISIBLE);
            orderDetailView.setVisibility(View.GONE);
        } else {
            largeOrderButton.setVisibility(View.GONE);
            orderDetailView.setVisibility(View.VISIBLE);
        }
    }
}
