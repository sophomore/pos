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
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.DateUtil;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.adapters.PaginationAdapter;
import org.jaram.ds.views.widgets.OrderDetailView;
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
    @BindView(R.id.orderDetail) OrderDetailView orderDetailView;
    @BindView(R.id.order2) View largeOrderButton;

    @BindDimen(R.dimen.order_list_item_spacing) int itemSpacing;

    private OrderAdapter orderAdapter;

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

        orderListView.setAdapter(orderAdapter);
        orderListView.setLoader(this::loadOrder);
        orderListView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacing));

        orderAdapter.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(order -> {
                    if (order == null) {
                        largeOrderButton.setVisibility(View.VISIBLE);
                        orderDetailView.setVisibility(View.GONE);
                    } else {
                        largeOrderButton.setVisibility(View.GONE);
                        orderDetailView.setVisibility(View.VISIBLE);
                    }
                    return true;
                })
                .subscribe(orderDetailView::setOrder);

        orderDetailView.setOnDeleteListener(order -> {
            int position = getItemPosition(order);
            if (position != -1) {
                orderAdapter.notifyItemRemoved(position);
            }
        });
        orderDetailView.setOnModifyListener(order -> {
            int position = getItemPosition(order);
            if (position != -1) {
                orderAdapter.notifyItemChanged(position);
            }
        });

        //TODO: 주문 검색 custom view
    }

    @OnClick({R.id.order, R.id.order2})
    protected void onClickOrderButton() {
        startActivity(new Intent(getActivity(), OrderActivity.class));
    }

    protected Observable<PaginationData<Order>> loadOrder(int page) {
        Observable<List<Order>> observable = orderAdapter.getItemCount() == 0
                ? Api.with(getActivity()).getOrder()
                : Api.with(getActivity()).getMoreOrders(orderAdapter.getItem(orderAdapter.getItemCount() - 1).getDate());

        return observable
                .map(this::setupOrderMenu)
                .map(this::addDateHeaderItem)
                .map(this::convertPaginationData);
    }

    protected List<Order> setupOrderMenu(List<Order> data) {
        for (Order order : data) {
            for (OrderMenu orderMenu : order.getOrderMenus()) {
                orderMenu.setOrder(order);
            }
        }
        return data;
    }

    protected List<Order> addDateHeaderItem(List<Order> data) {
        PaginationAdapter<Order> adapter = orderAdapter;
        Calendar lastDate = Calendar.getInstance();
        if (adapter.getListSize() > 0) {
            lastDate.setTime(adapter.getItem(adapter.getListSize() - 1).getDate());
        } else {
            lastDate.add(Calendar.YEAR, 1);
        }
        DateUtil.dropTime(lastDate);
        for (int i = 0; i < data.size(); i++) {
            Calendar date = Calendar.getInstance();
            Date receiveDate = data.get(i).getDate();
            if (receiveDate == null) {
                continue;
            }
            date.setTime(receiveDate);
            DateUtil.dropTime(date);
            if (lastDate.after(date)) {
                data.add(i, createHeaderItem(date));
            }
            lastDate = date;
        }
        return data;
    }

    protected Order createHeaderItem(Calendar date) {
        Order order = new Order();
        order.setId(OrderAdapter.VIEW_TYPE_HEADER);
        order.setDate(date.getTime());
        return order;
    }

    protected PaginationData<Order> convertPaginationData(List<Order> data) {
        PaginationData<Order> paginationData = new PaginationData<>(data);
        paginationData.setmNext(data.size() > 0 ? "hasNext" : "");
        return paginationData;
    }

    private void refresh() {
        orderAdapter.clear();
        orderListView.refresh(true);
    }

    private int getItemPosition(Order order) {
        for (int i = 0; i<orderAdapter.getListSize(); i++) {
            Order each = orderAdapter.getItem(i);
            if (each.equals(order)) {
                return i;
            }
        }
        return -1;
    }
}
