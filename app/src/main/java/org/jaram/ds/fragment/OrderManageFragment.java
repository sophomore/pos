package org.jaram.ds.fragment;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.activities.OrderActivity;
import org.jaram.ds.Data;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.widgets.PaginationView;
import org.jaram.ds.views.VerticalSpaceItemDecoration;
import org.jaram.ds.views.adapters.OrderAdapter;
import org.jaram.ds.views.adapters.DetailOrderMenuAdapter;

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
    @BindView(R.id.totalprice) TextView orderTotalPriceView;
    @BindView(R.id.date) TextView orderDateView;
    @BindView(R.id.orderMenuList) RecyclerView orderMenuListView;

    @BindDimen(R.dimen.button_line_stroke) int itemSpacing;

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

    @OnClick(R.id.order)
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

        return observable.map(result -> {
            PaginationData<Order> paginationData = new PaginationData<>(result);
            paginationData.setmNext(result.size() > 0 ? "hasNext" : "");
            return paginationData;
        });
    }

    protected void selectOrder(Order order) {
        selectedOrder = order;
        refreshOrderDetailView();
    }

    private void refreshOrderDetailView() {
        detailOrderMenuAdapter.clear();
        detailOrderMenuAdapter.addAll(selectedOrder.getOrderMenus());

        orderTotalPriceView.setText(selectedOrder == null ? "" : getString(R.string.format_money,
                selectedOrder.getTotalPrice()));
        orderDateView.setText(selectedOrder == null ? "" : Data.dateFormat.format(selectedOrder.getDate()));

        detailOrderMenuAdapter.notifyDataSetChanged();
    }

    private void refresh() {
        orderAdapter.clear();
        orderListView.refresh(true);
    }
}
