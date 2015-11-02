package org.jaram.ds.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.adapter.MenuListAdapter;
import org.jaram.ds.adapter.OrderMenuListAdapter;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Menu;
import org.jaram.ds.data.struct.OrderMenu;
import org.jaram.ds.util.SwipeDismissListViewTouchListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Order extends Fragment {

    org.jaram.ds.data.struct.Order order = null;
    ArrayList<OrderMenu> ordermenus = null;
    OrderMenuListAdapter orderMenuAdapter = null;

    TextView ordermenuEmpty = null;
    ListView ordermenuView = null;

    TextView totalpriceView = null;
    TextView totalpriceLabelView = null;

    Button endBtn;
    View confirmBox;

    boolean isConfirmView = false;

    private static Order view;
    public static Order getInstance() {
        if (view == null) {
            view = new Order();
        }
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        final MenuClicked menuListener = new MenuClicked();
        final OrderMenuListAction orderMenuListener = new OrderMenuListAction();

        totalpriceView = (TextView)view.findViewById(R.id.totalprice);
        totalpriceLabelView = (TextView)view.findViewById(R.id.totalpriceLabel);

        ordermenuView = (ListView)view.findViewById(R.id.ordermenuList);
        ordermenuEmpty = (TextView)view.findViewById(R.id.list_empty);

        orderMenuAdapter = new OrderMenuListAdapter(orderMenuListener);

        newOrder();

        ordermenuView.setAdapter(orderMenuAdapter);

        final SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        ordermenuView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {

                            @Override
                            public boolean canDismiss(int i) {
//                                if (ordermenus.get(i).getPay() == Data.PAY_CREDIT) return true;
//                                return false;
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    removeOrderMenu(position);
                                }
                                listRefresh();
                            }
                        });
        ordermenuView.setOnTouchListener(touchListener);
        ordermenuView.setOnScrollListener(touchListener.makeScrollListener());

        ordermenuView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!ordermenus.get(position).isPay()) {
                    if (orderMenuAdapter.isSelected(ordermenus.get(position))) {
                        orderMenuAdapter.removeSelectedMenu(ordermenus.get(position));
                    }
                    else {
                        orderMenuAdapter.addSelectedMenu(ordermenus.get(position));
                    }
                }
                else {
                    Toast.makeText(getActivity(), "이미 결제된 메뉴입니다.", Toast.LENGTH_SHORT).show();
                }
                listRefresh();
            }
        });

        ListView cutletView = (ListView)view.findViewById(R.id.cutlet_list);
        ListView riceView = (ListView)view.findViewById(R.id.rice_list);
        ListView noodleView = (ListView)view.findViewById(R.id.noodle_list);
        ListView etcView = (ListView)view.findViewById(R.id.etc_list);

        cutletView.setAdapter(new MenuListAdapter(Data.categories.get(1).getMenus(), menuListener));
        riceView.setAdapter(new MenuListAdapter(Data.categories.get(2).getMenus(), menuListener));
        noodleView.setAdapter(new MenuListAdapter(Data.categories.get(3).getMenus(), menuListener));
        etcView.setAdapter(new MenuListAdapter(Data.categories.get(4).getMenus(), menuListener));

        Button cashBtn = (Button)view.findViewById(R.id.pay_cash);
        Button cardBtn = (Button)view.findViewById(R.id.pay_card);
        Button serviceBtn = (Button)view.findViewById(R.id.pay_service);
        Button creditBtn = (Button)view.findViewById(R.id.pay_credit);
        endBtn = (Button)view.findViewById(R.id.endBtn);
        confirmBox = view.findViewById(R.id.pay_confirmBox);

        PayBtnsClicked payListener = new PayBtnsClicked(confirmBox, endBtn);

        cashBtn.setOnClickListener(payListener);
        cardBtn.setOnClickListener(payListener);
        serviceBtn.setOnClickListener(payListener);
        creditBtn.setOnClickListener(payListener);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ordermenus.size() == 0) {
                    Toast.makeText(getActivity(), "주문을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isConfirmView) {
                    if(chkAllPaied()) {
                        endOrder(true);
                    }
                    else {
                        Log.d("order", Boolean.toString(isConfirmView)+" | "+Boolean.toString(chkAllPaied()));
                        new AlertDialog.Builder(getActivity())
                                .setTitle("결제 확인")
                                .setMessage("미결제된 상품이 있습니다. 외상으로 처리하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        endOrder(true);
                                    }
                                })
                                .setNegativeButton("아니오", null)
                                .show();
                    }
                }
                payRefresh();
            }
        });
        confirmBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<OrderMenu> selectedMenus = orderMenuAdapter.getSelectedMenus();
                for (int i=0; i<selectedMenus.size(); i++) {
                    selectedMenus.get(i).setPay((int)v.getTag());
                    selectedMenus.get(i).setPay();
                }
                confirmBox.setVisibility(View.INVISIBLE);
                isConfirmView = false;
                endBtn.setText(getResources().getString(R.string.orderEnd));
                orderMenuAdapter.resetSelectedMenu();
                listRefresh();
            }
        });

        return view;
    }

    public boolean chkAllPaied() {
        for (int i=0; i<ordermenus.size(); i++) {
            if (!ordermenus.get(i).isPay()) return false;
        }
        return true;
    }

    public void endOrder(boolean isSave) {
        if (isSave) {
            order.setDate(new Date());
            if (Data.pref.getBoolean("network", false)) {
                order.store();
            }
            else {
                order.putDB();
            }
        }
        getActivity().finish();
//        listRefresh();
//        newOrder();
    }

    private void newOrder() {
        order = new org.jaram.ds.data.struct.Order();
        ordermenus = order.getOrdermenus();
        orderMenuAdapter.setOrdermenus(ordermenus);
        listRefresh();
    }

    private void removeOrderMenu(int position) {
        removeOrderMenu(ordermenus.get(position));
    }

    private void removeOrderMenu(OrderMenu ordermenu) {
        order.setTotalprice(order.getTotalprice() - ordermenu.getTotalprice());
        ordermenus.remove(ordermenu);
        if(orderMenuAdapter.getSelectedMenus().contains(ordermenu)) {
            orderMenuAdapter.removeSelectedMenu(ordermenu);
        }
    }

    private void listRefresh() {
        if (ordermenus.size() > 0) {
            ordermenuView.setVisibility(View.VISIBLE);
            ordermenuEmpty.setVisibility(View.INVISIBLE);
        } else {
            ordermenuEmpty.setVisibility(View.VISIBLE);
            ordermenuView.setVisibility(View.INVISIBLE);
        }
        orderMenuAdapter.notifyDataSetChanged();
        priceRefresh();
        Log.d("order", ordermenus.toString());
    }

    private void priceRefresh() {
        if (orderMenuAdapter.getSelectedMenus().size() > 0) {
            totalpriceLabelView.setText("선택 합계");
            totalpriceView.setText(orderMenuAdapter.getSelectedPrice()+"원");
        }
        else {
            totalpriceLabelView.setText("전체 합계");
            totalpriceView.setText(order.getTotalprice()+"원");
        }
    }

    private void addMenu(Menu menu) {
        order.addMenu(menu, Data.PAY_CREDIT, false, false, false);
        listRefresh();
    }

    private void payRefresh() {
        orderMenuAdapter.resetSelectedMenu();
        confirmBox.setVisibility(View.INVISIBLE);
        isConfirmView = false;
        endBtn.setText(getResources().getString(R.string.orderEnd));
        listRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MenuClicked implements MenuListAdapter.MenuClickListener {

        @Override
        public void onClick(Menu menu) {
            payRefresh();
            addMenu(menu);
        }

        @Override
        public void onLongClick(Menu menu) {

        }
    }

    private class PayBtnsClicked implements View.OnClickListener {

        View overlayBox = null;
        Button cancelBtn = null;
        PayBtnsClicked(View overlayBox, Button cancelBtn) {
            this.overlayBox = overlayBox;
            this.cancelBtn = cancelBtn;
        }

        @Override
        public void onClick(View v) {
            if (!(ordermenus.size() > 0) || chkAllPaied()) {
                Toast.makeText(getActivity(), "주문을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (orderMenuAdapter.getSelectedMenus().size() == 0) {
                for (int i=0; i<ordermenus.size(); i++) {
                    if (!ordermenus.get(i).isPay()) {
                        orderMenuAdapter.addSelectedMenu(ordermenus.get(i));
                    }
                }
                listRefresh();
            }
            TextView notice = (TextView)overlayBox.findViewById(R.id.pay_confirm);
            if (v.getId() == R.id.pay_cash) {
                notice.setText("'현금'으로 결제하시려면 다시 한 번 터치하세요.");
                overlayBox.setTag(Data.PAY_CASH);
            }
            else if (v.getId() == R.id.pay_card) {
                notice.setText("'카드'로 결제하시려면 다시 한 번 터치하세요.");
                overlayBox.setTag(Data.PAY_CARD);
            }
            else if (v.getId() == R.id.pay_service) {
                notice.setText("'서비스'로 처리하시려면 다시 한 번 터치하세요.");
                overlayBox.setTag(Data.PAY_SERVICE);
            }
            else if (v.getId() == R.id.pay_credit) {
                notice.setText("'외상'으로 처리하시려면 다시 한 번 터치하세요.");
                overlayBox.setTag(Data.PAY_CREDIT);
            }
            overlayBox.setVisibility(View.VISIBLE);
            cancelBtn.setText("취소");
            isConfirmView = true;
        }
    }

    private class OrderMenuListAction implements OrderMenuListAdapter.OrderMenuListener {

        @Override
        public void curryClicked(OrderMenu ordermenu, Button curryView) {
            if (ordermenu.isCurry()) {
                ordermenu.resetCurry();
//                curryView.setBackgroundResource(android.R.color.transparent);
//                curryView.setTextColor(getResources().getColor(R.color.point));
            } else {
                ordermenu.setCurry();
//                curryView.setBackgroundResource(R.color.point);
//                curryView.setTextColor(Color.WHITE);
            }
            curryView.setSelected(!curryView.isSelected());
            listRefresh();
        }

        @Override
        public void twiceClicked(OrderMenu ordermenu, Button twiceView) {
            if (ordermenu.isTwice()) {
                ordermenu.resetTwice();
//                twiceView.setBackgroundResource(android.R.color.transparent);
//                twiceView.setTextColor(getResources().getColor(R.color.point));
            } else {
                ordermenu.setTwice();
//                twiceView.setBackgroundResource(R.color.point);
//                twiceView.setTextColor(Color.WHITE);
            }
            twiceView.setSelected(!twiceView.isSelected());
            listRefresh();
        }

        @Override
        public void takeoutClicked(OrderMenu ordermenu, Button takeoutView) {
            if (ordermenu.isTakeout()) {
                ordermenu.resetTakeout();
//                twiceView.setBackgroundResource(android.R.color.transparent);
//                twiceView.setTextColor(getResources().getColor(R.color.point));
            } else {
                ordermenu.setTakeout();
//                twiceView.setBackgroundResource(R.color.point);
//                twiceView.setTextColor(Color.WHITE);
            }
            takeoutView.setSelected(!takeoutView.isSelected());
            listRefresh();
        }
    }
}