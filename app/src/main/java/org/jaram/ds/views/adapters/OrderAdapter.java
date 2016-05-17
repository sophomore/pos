package org.jaram.ds.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import org.jaram.ds.R;
import org.jaram.ds.models.Order;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.models.Pay;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.DateUtil;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.widgets.BaseRecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class OrderAdapter extends PaginationAdapter<Order> {

    public static final int VIEW_TYPE_CONTENT = 0;
    public static final int VIEW_TYPE_HEADER = -1;

    private HashMap<Date, Integer> dailyTotalSalesCache = new HashMap<>();

    private PublishSubject<Order> publishSubject = PublishSubject.create();
    private Order selectedOrder;
    private int selectedPosition;

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getId() < 0
                ? getItem(position).getId()
                : VIEW_TYPE_CONTENT;
    }

    @Override
    public BaseRecyclerView.BaseViewHolder<Order> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case VIEW_TYPE_CONTENT:
                return new OrderItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_order, parent, false));
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_order, parent, false));
        }
    }

    public Observable<Order> asObservable() {
        return publishSubject;
    }

    public void notifySelectedItemChanged() {
        notifyItemChanged(selectedPosition);
    }

    private static TextView createSmallOrderMenuView(Context context) {
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        int marginPixel = context.getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
        params.setMargins(0, 0, marginPixel, marginPixel);

        TextView smallOrderMenuView = new TextView(context);
        int paddingHorizontalPixel = context.getResources().getDimensionPixelSize(R.dimen.spacing_small);
        int paddingVerticalPixel = context.getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
        smallOrderMenuView.setPadding(paddingHorizontalPixel, paddingVerticalPixel, paddingHorizontalPixel, paddingVerticalPixel);
        smallOrderMenuView.setBackgroundResource(R.color.point);
        smallOrderMenuView.setTextColor(Color.WHITE);
        smallOrderMenuView.setGravity(Gravity.CENTER);

        smallOrderMenuView.setLayoutParams(params);

        return smallOrderMenuView;
    }

    protected class HeaderViewHolder extends BaseRecyclerView.BaseViewHolder<Order> {

        @BindView(R.id.date) TextView dateView;
        @BindView(R.id.total) TextView totalView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind() {
            dateView.setText(DateUtil.format("yyyy-MM-dd", data.getDate()));
            totalView.setText("로딩중");
            if (dailyTotalSalesCache.containsKey(data.getDate())) {
                totalView.setText(context.getString(R.string.format_money,
                        dailyTotalSalesCache.get(data.getDate())));
                return;
            }

            loadTotalSaveFromServer();
        }

        private void loadTotalSaveFromServer() {
            // TODO: 다른 날도 가져올 수 있도록 하면 날짜 비교 코드 삭제
            Calendar today = Calendar.getInstance();
            Calendar objDay = Calendar.getInstance();
            objDay.setTime(data.getDate());
            DateUtil.dropTime(today);
            DateUtil.dropTime(objDay);
            if (today.compareTo(objDay) != 0) {
                totalView.setText("");
                return;
            }

            Api.with(context).getDailyTotalSales(data.getDate())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        totalView.setText(context.getString(R.string.format_money,
                                result.getPrice()));
                        dailyTotalSalesCache.put(data.getDate(), result.getPrice());
                    }, SLog::e);
        }
    }

    protected class OrderItemViewHolder extends BaseRecyclerView.BaseViewHolder<Order> {

        @BindView(R.id.container) View container;
        @BindView(R.id.orderMenuList) FlowLayout orderMenuContainer;
        @BindView(R.id.price) TextView priceView;
        @BindView(R.id.date) TextView dateView;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind() {
            dateView.setText(new SimpleDateFormat("HH:mm", Locale.KOREA).format(data.getDate()));
            priceView.setText(context.getString(R.string.format_money, data.getTotalPrice()));
            orderMenuContainer.removeAllViews();

            for (OrderMenu orderMenu : data.getOrderMenus()) {
                TextView smallOrderMenuView = createSmallOrderMenuView(context);
                smallOrderMenuView.setText(orderMenu.getMenu().getName());
                if (Pay.CREDIT.equals(orderMenu.getPay())) {
                    smallOrderMenuView.setBackgroundResource(R.color.accent);
                }
                orderMenuContainer.addView(smallOrderMenuView);
            }

            container.setSelected(data.equals(selectedOrder));
        }

        @Override
        protected void onClick(View v) {
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
            selectedOrder = selectedOrder != null && selectedOrder.equals(data) ? null : data;
            publishSubject.onNext(selectedOrder);
            notifyItemChanged(position);
        }
    }
}
