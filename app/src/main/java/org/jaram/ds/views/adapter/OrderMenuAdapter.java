package org.jaram.ds.views.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.util.SLog;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.BaseRecyclerView;
import org.jaram.ds.views.SwipeTouchHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class OrderMenuAdapter extends BaseRecyclerView.BaseListAdapter<OrderMenu> implements SwipeTouchHelper.SwipeListener {

    private List<OrderMenu> selectedOrderMenus;
    private Context context;

    public OrderMenuAdapter() {
        selectedOrderMenus = new ArrayList<>();
    }

    @Override
    public BaseRecyclerView.BaseViewHolder<OrderMenu> onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new OrderMenuItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ordermenu, parent, false));
    }

    @Override
    public void onSwiped(int position) {
        if (getItem(position).isPay()) {
            Toast.makeText(context, "이미 결제한 메뉴는 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    public List<OrderMenu> getSelectedOrderMenus() {
        return selectedOrderMenus;
    }

    public void addSelectedOrderMenu(OrderMenu orderMenu) {
        selectedOrderMenus.add(orderMenu);
    }

    public void removeSelectedOrderMenu(OrderMenu orderMenu) {
        selectedOrderMenus.remove(orderMenu);
    }

    public boolean isContainSelectedOrderMenus(OrderMenu orderMenu) {
        return selectedOrderMenus.contains(orderMenu);
    }

    public void resetSelectedMenu() {
        selectedOrderMenus.clear();
    }

    protected class OrderMenuItemViewHolder extends BaseRecyclerView.BaseViewHolder<OrderMenu> {

        @Bind(R.id.menu_name) public TextView nameView;
        @Bind(R.id.menu_price) public TextView priceView;
        @Bind(R.id.curryBtn) public Button curryButton;
        @Bind(R.id.twiceBtn) public Button twiceButton;
        @Bind(R.id.takeoutBtn) public Button takeoutButton;

        public OrderMenuItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind() {
            nameView.setText(data.getMenu().getName());
            priceView.setText(StringUtils.format("%d원", data.getTotalPrice()));

            if (data.isPay()) {
                itemView.setAlpha(0.6f);
                curryButton.setClickable(false);
                twiceButton.setClickable(false);
                takeoutButton.setClickable(false);
            } else {
                itemView.setAlpha(1.0f);
                curryButton.setClickable(true);
                twiceButton.setClickable(true);
                takeoutButton.setClickable(true);
            }

            setSelectedView(curryButton, data.isCurry());
            setSelectedView(twiceButton, data.isTwice());
            setSelectedView(takeoutButton, data.isTakeout());

            if (selectedOrderMenus.contains(data)) {
                nameView.setTextColor(Color.WHITE);
                itemView.setBackgroundResource(R.color.point);
            } else {
                nameView.setTextColor(ContextCompat.getColor(context, R.color.dark));
                itemView.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        protected void onClick(View v) {
            if (getItem(position).isPay()) {
                Toast.makeText(context, "이미 결제된 메뉴입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isContainSelectedOrderMenus(getItem(position))) {
                removeSelectedOrderMenu(getItem(position));
            } else {
                addSelectedOrderMenu(getItem(position));
            }
            notifyItemChanged(position);
        }

        @OnClick(R.id.curryBtn)
        protected void onClickCurryButton(View view) {
            if (!(view instanceof TextView)) {
                return;
            }
            data.setCurry(!data.isCurry());
            setSelectedView((TextView) view, data.isCurry());
        }

        @OnClick(R.id.twiceBtn)
        protected void onClickTwiceButton(View view) {
            if (!(view instanceof TextView)) {
                return;
            }
            data.setTwice(!data.isTwice());
            setSelectedView((TextView) view, data.isTakeout());
        }

        @OnClick(R.id.takeoutBtn)
        protected void onClickTakeoutButton(View view) {
            if (!(view instanceof TextView)) {
                return;
            }
            data.setTakeout(!data.isTakeout());
            setSelectedView((TextView) view, data.isTakeout());
        }

        private void setSelectedView(TextView view, boolean isSelected) {
            if (isSelected) {
                view.setSelected(true);
                view.setTextColor(Color.WHITE);
            } else {
                view.setSelected(false);
                view.setTextColor(ContextCompat.getColor(context, R.color.point));
            }
        }
    }

}
