package org.jaram.ds.fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.models.*;
import org.jaram.ds.models.Order;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.widgets.BaseRecyclerView;
import org.jaram.ds.views.widgets.MenuListView;
import org.jaram.ds.views.SwipeTouchHelper;
import org.jaram.ds.views.VerticalSpaceItemDecoration;
import org.jaram.ds.views.adapters.OrderMenuAdapter;

import java.util.Date;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class OrderFragment extends BaseFragment {

    @BindView(R.id.totalpriceLabel) TextView totalpriceLabelView;
    @BindView(R.id.totalprice) TextView totalpriceView;
    @BindView(R.id.list_empty) View emptyView;
    @BindView(R.id.orderMenuList) BaseRecyclerView orderMenuListView;
    @BindView(R.id.end) Button endButton;
    @BindView(R.id.pay_cash) Button payCashButton;
    @BindView(R.id.pay_card) Button payCardButton;
    @BindView(R.id.pay_service) Button payServiceButton;
    @BindView(R.id.pay_credit) Button payCreditButton;
    @BindView(R.id.pay_confirmBox) View payConfirmContainer;
    @BindView(R.id.pay_confirm) TextView payConfirmView;
    @BindView(R.id.menuList) MenuListView menuList;

    @BindDimen(R.dimen.button_line_stroke) int itemSpacing;

    private OrderMenuAdapter adapter;

    private Pay payType = Pay.CREDIT;

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_order;
    }

    @Override
    protected void setupLayout(View view) {
        adapter = new OrderMenuAdapter();
        orderMenuListView.setAdapter(adapter);
        orderMenuListView.setEmptyView(emptyView);
        orderMenuListView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacing));

        menuList.setOnItemClickListener(this::addOrderMenu);

        new ItemTouchHelper(new SwipeTouchHelper(adapter)).attachToRecyclerView(orderMenuListView);

        adapter.registerAdapterDataObserver(new AdapterObserver());
    }

    @OnClick({R.id.pay_cash, R.id.pay_card, R.id.pay_service, R.id.pay_credit})
    void onClickPayButton(View v) {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(getActivity(), R.string.message_alert_first_select_ordermenu, Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter.getSelectedOrderMenus().isEmpty()) {
            for (OrderMenu orderMenu : adapter.getAll()) {
                if (!orderMenu.isPay()) {
                    adapter.addSelectedOrderMenu(orderMenu);
                }
            }
            adapter.notifyDataSetChanged();
        }

        switch (v.getId()) {
            case R.id.pay_cash:
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay),
                        getString(R.string.label_cash)));
                payType = Pay.CASH;
                break;
            case R.id.pay_card:
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay),
                        getString(R.string.label_card)));
                payType = Pay.CARD;
                break;
            case R.id.pay_service:
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay),
                        getString(R.string.label_service)));
                payType = Pay.SERVICE;
                break;
            case R.id.pay_credit:
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay),
                        getString(R.string.label_credit)));
                payType = Pay.CREDIT;
                break;
        }
        payConfirmContainer.setVisibility(View.VISIBLE);
        endButton.setText(R.string.label_cancel);
    }

    @OnClick(R.id.pay_confirmBox)
    void onClickConfirmView() {
        for (OrderMenu orderMenu : adapter.getSelectedOrderMenus()) {
            orderMenu.setPay(true);
            orderMenu.setPay(payType);
        }
        payConfirmContainer.setVisibility(View.GONE);
        endButton.setText(R.string.label_end_order);
        adapter.resetSelectedMenu();
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.end)
    void onClickEndButton(View view) {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(getActivity(), R.string.message_alert_first_select_ordermenu,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (isAllPayed()) {
            endOrder();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.message_alert_not_paid)
                    .setPositiveButton(R.string.label_yes, (dialog, which) -> endOrder())
                    .setNegativeButton(R.string.label_no, null)
                    .show();
        }
    }

    private void addOrderMenu(Menu menu) {
        OrderMenu orderMenu = new OrderMenu();
        orderMenu.setPay(Pay.CREDIT);
        orderMenu.setPay(false);
        orderMenu.setTakeout(false);
        orderMenu.setTwice(false);
        orderMenu.setCurry(false);
        orderMenu.setMenu(menu);
        adapter.add(orderMenu);
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    private boolean isAllPayed() {
        for (OrderMenu orderMenu : adapter.getAll()) {
            if (!orderMenu.isPay()) {
                return false;
            }
        }
        return true;
    }

    private static int getTotalPrice(List<OrderMenu> orderMenus) {
        int price = 0;
        for (OrderMenu orderMenu : orderMenus) {
            price += orderMenu.getTotalPrice();
        }
        return price;
    }

    private void endOrder() {
        org.jaram.ds.models.Order order = new Order();
        order.setDate(new Date());
        order.getOrderMenus().clear();
        order.getOrderMenus().addAll(adapter.getAll());
        order.setTotalPrice(getTotalPrice(order.getOrderMenus()));
        org.jaram.ds.managers.OrderManager.getInstance(getActivity()).addOrder(order);
        getActivity().finish();
    }

    private void priceRefresh() {
        if (adapter.getSelectedOrderMenus().size() > 0) {
            totalpriceLabelView.setText(R.string.label_total_select);
            totalpriceView.setText(getString(R.string.format_money,
                    getTotalPrice(adapter.getSelectedOrderMenus())));
        } else {
            totalpriceLabelView.setText(R.string.label_total_all);
            totalpriceView.setText(getString(R.string.format_money,
                    getTotalPrice(adapter.getAll())));
        }
    }

    private class AdapterObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            priceRefresh();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            priceRefresh();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            priceRefresh();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            priceRefresh();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            priceRefresh();
        }
    }
}
