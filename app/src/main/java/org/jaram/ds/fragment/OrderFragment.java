package org.jaram.ds.fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.models.*;
import org.jaram.ds.models.Order;
import org.jaram.ds.util.SLog;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.BaseRecyclerView;
import org.jaram.ds.views.MenuListView;
import org.jaram.ds.views.SwipeTouchHelper;
import org.jaram.ds.views.VerticalSpaceItemDecoration;
import org.jaram.ds.views.adapter.OrderMenuAdapter;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class OrderFragment extends BaseFragment {

    @Bind(R.id.totalpriceLabel) TextView totalpriceLabelView;
    @Bind(R.id.totalprice) TextView totalpriceView;
    @Bind(R.id.list_empty) View emptyView;
    @Bind(R.id.orderMenuList) BaseRecyclerView orderMenuListView;
    @Bind(R.id.end) Button endButton;
    @Bind(R.id.pay_cash) Button payCashButton;
    @Bind(R.id.pay_card) Button payCardButton;
    @Bind(R.id.pay_service) Button payServiceButton;
    @Bind(R.id.pay_credit) Button payCreditButton;
    @Bind(R.id.pay_confirmBox) View payConfirmContainer;
    @Bind(R.id.pay_confirm) TextView payConfirmView;
    @Bind(R.id.menuList) MenuListView menuList;

    @BindDimen(R.dimen.button_line_stroke) int itemSpacing;

    private OrderMenuAdapter adapter;

    private int payType = Data.PAY_CREDIT;

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
            Toast.makeText(getActivity(), "주문을 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
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
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay), "현금"));
                payType = Data.PAY_CASH;
                break;
            case R.id.pay_card:
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay), "카드"));
                payType = Data.PAY_CARD;
                break;
            case R.id.pay_service:
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay), "서비스"));
                payType = Data.PAY_SERVICE;
                break;
            case R.id.pay_credit:
                payConfirmView.setText(StringUtils.format(getString(R.string.message_confirm_pay), "외상"));
                payType = Data.PAY_CREDIT;
                break;
        }
        payConfirmContainer.setVisibility(View.VISIBLE);
        endButton.setText("취소");
    }

    @OnClick(R.id.pay_confirmBox)
    void onClickConfirmView() {
        for (OrderMenu orderMenu : adapter.getSelectedOrderMenus()) {
            orderMenu.setPay(true);
            orderMenu.setPay(payType);
        }
        payConfirmContainer.setVisibility(View.GONE);
        endButton.setText("주문 완료");
        adapter.resetSelectedMenu();
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.end)
    void onClickEndButton(View view) {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(getActivity(), "주문을 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isAllPayed()) {
            endOrder();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("확인")
                    .setMessage("결제가 되지 않은 상품이 있습니다. 외상으로 처리하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> endOrder())
                    .setNegativeButton("아니오", null)
                    .show();
        }
    }

    private void addOrderMenu(Menu menu) {
        OrderMenu orderMenu = new OrderMenu();
        orderMenu.setPay(Data.PAY_CREDIT);
        orderMenu.setPay(false);
        orderMenu.setTakeout(false);
        orderMenu.setTwice(false);
        orderMenu.setCurry(false);
        orderMenu.setMenu(menu);
        orderMenu.setMenuId(menu.getId());
        orderMenu.setTotalPrice(OrderMenu.calculateTotalPrice(orderMenu));
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
            totalpriceLabelView.setText("선택 합계");
            totalpriceView.setText(StringUtils.format("%d원",
                    getTotalPrice(adapter.getSelectedOrderMenus())));
        } else {
            totalpriceLabelView.setText("전체 합계");
            totalpriceView.setText(StringUtils.format("%d원",
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
