package org.jaram.ds.views.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wefika.flowlayout.FlowLayout;

import org.jaram.ds.R;
import org.jaram.ds.models.MenuAttribute;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.util.SLog;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.widgets.BaseRecyclerView;
import org.jaram.ds.views.SwipeTouchHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class OrderMenuAdapter extends BaseRecyclerView.BaseListAdapter<OrderMenu> implements SwipeTouchHelper.SwipeListener {

    private List<OrderMenu> selectedOrderMenus;
    private Context context;
    private int openPosition = -1;

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

        @BindView(R.id.container) ViewGroup container;
        @BindView(R.id.name) TextView nameView;
        @BindView(R.id.price) TextView priceView;
        @BindView(R.id.attribute) View attributeButton;
        @BindView(R.id.selectedAttributeContainer) FlowLayout selectedAttributeContainer;
        @BindView(R.id.divider) View divider;
        @BindView(R.id.attributeContainer) FlowLayout attributeContainer;

        @BindDimen(R.dimen.spacing_micro) int attributeItemMargin;
        @BindDimen(R.dimen.spacing_smaller) int attributeItemPadding;
        @BindColor(R.color.color_menu_attribute_view) ColorStateList attributeItemTextColor;

        private Set<MenuAttribute> attributes;

        public OrderMenuItemViewHolder(View itemView) {
            super(itemView);
            attributes = new HashSet<>();

            Realm db = Realm.getDefaultInstance();
            for (MenuAttribute attribute : db.where(MenuAttribute.class).findAll()) {
                attributes.add(attribute.copyNewInstance());
            }
            db.close();
        }

        @Override
        protected void bind() {
            nameView.setText(data.getMenu().getName());
            priceView.setText(context.getString(R.string.format_money, data.getTotalPrice()));

            container.setSelected(selectedOrderMenus.contains(data));

            if (data.isPay()) {
                itemView.setAlpha(0.6f);
                attributeButton.setVisibility(View.GONE);
            } else {
                itemView.setAlpha(1.0f);
                attributeButton.setVisibility(View.VISIBLE);
            }

            if (data.getAttributes() == null) {
                data.setAttributes(new RealmList<>());
            }

            if (openPosition == position) {
                visibleAttributeList();
            } else {
                invisibleAttributeList();
            }

            invalidateAttributeView();

            attributeButton.setVisibility(container.isSelected() ? View.GONE : View.VISIBLE);
        }

        @Override
        protected void onClick(View v) {
            if (getItem(position).isPay()) {
                Toast.makeText(context, "이미 결제된 메뉴입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (openPosition == position) {
                openPosition = -1;
            }
            if (isContainSelectedOrderMenus(getItem(position))) {
                removeSelectedOrderMenu(getItem(position));
            } else {
                addSelectedOrderMenu(getItem(position));
            }
            notifyItemChanged(position);
        }

        @OnClick(R.id.attribute)
        protected void toggleAttributeContainer() {
            if (openPosition == position) {
                openPosition = -1;
                notifyItemChanged(position);
                return;
            }

            int pastOpenPosition = openPosition;
            openPosition = position;
            notifyItemChanged(pastOpenPosition);
            notifyItemChanged(openPosition);
        }

        @SuppressWarnings("ConstantConditions")
        protected void onClickAttributeView(View v) {
            if (!(v.getTag() instanceof MenuAttribute)) {
                return;
            }

            if (data.getAttributes().contains(v.getTag())) {
                data.getAttributes().remove(v.getTag());
            } else {
                data.getAttributes().add((MenuAttribute) v.getTag());
            }
            invalidateAttributeView();
        }

        @SuppressWarnings("ConstantConditions")
        protected void invalidateAttributeView() {
            selectedAttributeContainer.removeAllViews();
            attributeContainer.removeAllViews();

            for (MenuAttribute attr : attributes) {
                if (data.getAttributes().contains(attr)) {
                    addSelectedAttributeView(attr);
                } else {
                    addAttributeView(attr);
                }
            }

            if (selectedAttributeContainer.getChildCount() == 0) {
                selectedAttributeContainer.setVisibility(View.GONE);
            }
        }

        protected void visibleAttributeList() {
            divider.setVisibility(View.VISIBLE);
            attributeContainer.setVisibility(View.VISIBLE);
        }

        protected void invisibleAttributeList() {
            divider.setVisibility(View.GONE);
            attributeContainer.setVisibility(View.GONE);
        }

        private void addSelectedAttributeView(MenuAttribute attribute) {
            TextView view = createAttributeView(attribute);
            view.setSelected(true);
            selectedAttributeContainer.addView(view);
            selectedAttributeContainer.setVisibility(View.VISIBLE);
        }

        private void addAttributeView(MenuAttribute attribute) {
            attributeContainer.addView(createAttributeView(attribute));
        }

        private TextView createAttributeView(MenuAttribute attribute) {
            TextView view = new TextView(context);
            view.setText(attribute.getName());
            view.setGravity(Gravity.CENTER);
            view.setPadding(attributeItemPadding, attributeItemPadding / 2,
                    attributeItemPadding, attributeItemPadding / 2);
            view.setBackgroundResource(R.drawable.bg_menu_attribute_view);
            view.setTextColor(attributeItemTextColor);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            view.setTag(attribute);
            view.setOnClickListener(this::onClickAttributeView);
            view.setActivated(container.isSelected());

            FlowLayout.LayoutParams params =
                    new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(attributeItemMargin, attributeItemMargin,
                    attributeItemMargin, attributeItemMargin);

            view.setLayoutParams(params);
            return view;
        }
    }
}
