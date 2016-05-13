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
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.widgets.BaseRecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdekim43 on 2016. 1. 28..
 */
public class OrderAdapter extends PaginationAdapter<Order> {

    private PublishSubject<Order> publishSubject = PublishSubject.create();
    private Order selectedOrder;
    private int selectedPosition;

    @Override
    public BaseRecyclerView.BaseViewHolder<Order> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false));
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
        params.setMargins(0, marginPixel, marginPixel, 0);

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

    protected class OrderItemViewHolder extends BaseRecyclerView.BaseViewHolder<Order> {

        @Bind(R.id.container) View container;
        @Bind(R.id.orderMenuList) FlowLayout orderMenuContainer;
        @Bind(R.id.price) TextView priceView;
        @Bind(R.id.date) TextView dateView;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind() {
            dateView.setText(new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초", Locale.KOREA).format(data.getDate()));
            priceView.setText(StringUtils.format("%d원", data.getTotalPrice()));
            orderMenuContainer.removeAllViews();

            for (OrderMenu orderMenu : data.getOrderMenus()) {
                TextView smallOrderMenuView = createSmallOrderMenuView(context);
                smallOrderMenuView.setText(orderMenu.getMenu().getName());
                if (Pay.CREDIT.equals(orderMenu.getPay())) {
                    smallOrderMenuView.setBackgroundResource(R.color.accent);
                }
                orderMenuContainer.addView(smallOrderMenuView);
            }

            container.setSelected(data == selectedOrder);
        }

        @Override
        protected void onClick(View v) {
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
            selectedOrder = data;
            publishSubject.onNext(data);
            notifyItemChanged(position);
        }
    }
}
