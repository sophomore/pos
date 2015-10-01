package org.jaram.ds.fragment;

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

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class Order extends Fragment {

    org.jaram.ds.data.struct.Order order = null;
//    ArrayList<OrderMenu> ordermenus = null;
    RealmList<OrderMenu> ordermenus = null;
    OrderMenuListAdapter orderMenuAdapter = null;

    TextView ordermenuEmpty = null;
    ListView ordermenuView = null;

    TextView totalpriceView = null;
    TextView totalpriceLabelView = null;

    boolean isConfirmView = false;

    Realm db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        db = Realm.getInstance(getActivity());

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
                                if (ordermenus.get(i).getPay() == Data.PAY_CREDIT) return true;
                                return false;
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
                if (ordermenus.get(position).getPay() == Data.PAY_CREDIT) {
                    if (orderMenuAdapter.isSelected(ordermenus.get(position))) {
                        orderMenuAdapter.removeSelectedMenu(ordermenus.get(position));
                    }
                    else {
                        orderMenuAdapter.addSelectedMenu(ordermenus.get(position));
                    }
                    listRefresh();
                }
                else {
                    Toast.makeText(getActivity(), "이미 결제된 메뉴입니다.", Toast.LENGTH_SHORT).show();
                }
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
        final Button endBtn = (Button)view.findViewById(R.id.endBtn);
        final View confirmBox = view.findViewById(R.id.pay_confirmBox);

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
                if (!chkAllPaied()) Toast.makeText(getActivity(), "결제를 모두 마쳐주세요.", Toast.LENGTH_SHORT).show();
                if (!isConfirmView) {
                    if(chkAllPaied()) endOrder(true);
                }
                orderMenuAdapter.resetSelectedMenu();
                confirmBox.setVisibility(View.INVISIBLE);
                endBtn.setText("전표 출력");
                listRefresh();
            }
        });
        confirmBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<OrderMenu> selectedMenus = orderMenuAdapter.getSelectedMenus();
                for (int i=0; i<selectedMenus.size(); i++) {
                    selectedMenus.get(i).setPay((int)v.getTag());
                }
                confirmBox.setVisibility(View.INVISIBLE);
                endBtn.setText("전표 출력");
                orderMenuAdapter.resetSelectedMenu();
                Log.d("order method", "before chek");
                if (chkAllPaied()) {
                    endOrder(true);
                    return;
                }
                Log.d("order method", "after chek");
                listRefresh();
            }
        });

        return view;
    }

    public boolean chkAllPaied() {
        for (int i=0; i<ordermenus.size(); i++) {
            if (ordermenus.get(i).getPay() == Data.PAY_CREDIT) return false;
        }
        return true;
    }

    public void endOrder(boolean isSave) {
        if (isSave) db.commitTransaction();
        else db.cancelTransaction();

        //TODO: 전표 출력
        listRefresh();
        newOrder();
    }

    private void newOrder() {
        db.beginTransaction();
        order = db.createObjectFromJson(org.jaram.ds.data.struct.Order.class, "{\"id\":"+org.jaram.ds.data.struct.Order.getNextKey(db)+"}");
        ordermenus = order.getOrdermenus();
        Log.d("order", ordermenus.toString());
        orderMenuAdapter.setOrdermenus(ordermenus);
        listRefresh();
    }

    private void removeOrderMenu(int position) {
        removeOrderMenu(ordermenus.get(position));
    }

    private void removeOrderMenu(OrderMenu ordermenu) {
        order.setTotalprice(order.getTotalprice() - ordermenu.getTotalprice());
        ordermenus.remove(ordermenu); //TODO: 결제 완료 된 것에 대해서 처리
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
        order.getManager().addMenu(db, menu);
        listRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.cancelTransaction();
    }

    private class MenuClicked implements MenuListAdapter.MenuClickListener {

        @Override
        public void onClick(Menu menu) {
            addMenu(menu);
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
            if (!(ordermenus.size() > 0)) {
                Toast.makeText(getActivity(), "주문을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (orderMenuAdapter.getSelectedMenus().size() == 0) {
                for (int i=0; i<ordermenus.size(); i++) {
                    if (ordermenus.get(i).getPay() == Data.PAY_CREDIT) {
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
        }
    }

    private class OrderMenuListAction implements OrderMenuListAdapter.OrderMenuListener {

        @Override
        public void curryClicked(OrderMenu ordermenu, Button curryView) {
            if (ordermenu.isCurry()) {
                ordermenu.getManager().resetCurry();
//                curryView.setBackgroundResource(android.R.color.transparent);
//                curryView.setTextColor(getResources().getColor(R.color.point));
            } else {
                ordermenu.getManager().setCurry();
//                curryView.setBackgroundResource(R.color.point);
//                curryView.setTextColor(Color.WHITE);
            }
            curryView.setSelected(!curryView.isSelected());
            listRefresh();
        }

        @Override
        public void twiceClicked(OrderMenu ordermenu, Button twiceView) {
            if (ordermenu.isTwice()) {
                ordermenu.getManager().resetTwice();
//                twiceView.setBackgroundResource(android.R.color.transparent);
//                twiceView.setTextColor(getResources().getColor(R.color.point));
            } else {
                ordermenu.getManager().setTwice();
//                twiceView.setBackgroundResource(R.color.point);
//                twiceView.setTextColor(Color.WHITE);
            }
            twiceView.setSelected(!twiceView.isSelected());
            listRefresh();
        }
    }
}