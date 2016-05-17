package org.jaram.ds.views.adapters;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import org.jaram.ds.R;
import org.jaram.ds.models.MenuAttribute;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.models.Pay;
import org.jaram.ds.models.result.SimpleApiResult;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.views.widgets.BaseRecyclerView;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnItemSelected;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdekim43 on 2016. 5. 17..
 */
public class DetailOrderMenuAdapter extends BaseRecyclerView.BaseListAdapter<OrderMenu> {

    private PublishSubject<OrderMenu> publishSubject = PublishSubject.create();

    @Override
    public BaseRecyclerView.BaseViewHolder<OrderMenu> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderMenuViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_ordermenu, parent, false));
    }

    public Observable<OrderMenu> asObservable() {
        return publishSubject;
    }

    protected class OrderMenuViewHolder extends BaseRecyclerView.BaseViewHolder<OrderMenu> {

        @BindView(R.id.container) ViewGroup container;
        @BindView(R.id.name) TextView nameView;
        @BindView(R.id.price) TextView priceView;
        @BindView(R.id.pay) Spinner payView;
        @BindView(R.id.attributeContainer) FlowLayout attributeContainer;

        @BindDimen(R.dimen.spacing_micro) int attributeItemMargin;
        @BindDimen(R.dimen.spacing_smaller) int attributeItemPadding;
        @BindColor(R.color.point) int attributeItemTextColor;

        private boolean binding = false;

        public OrderMenuViewHolder(View itemView) {
            super(itemView);
            payView.setAdapter(new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line, Pay.values()));
        }

        @Override
        protected void bind() {
            binding = true;
            nameView.setText(data.getMenu().getName());
            priceView.setText(context.getString(R.string.format_money, data.getTotalPrice()));

            payView.setSelection(data.getPay().ordinal());
            switch (data.getPay()) {
                case CREDIT:
                    container.setBackgroundResource(R.color.accent);
                    break;
                default:
                    container.setBackgroundResource(android.R.color.white);
                    break;
            }

            attributeContainer.removeAllViews();

            if (data.isCurry()) {
                addAttributeView(createTempAttribute(-1, "카레 추가"));
            }
            if (data.isTakeout()) {
                addAttributeView(createTempAttribute(-2, "포장"));
            }
            if (data.isTwice()) {
                addAttributeView(createTempAttribute(-3, "곱배기"));
            }

            if (data.getAttributes() != null) {
                for (MenuAttribute attr : data.getAttributes()) {
                    addAttributeView(attr);
                }
            }

            binding = false;
        }

        @OnItemSelected(R.id.pay)
        protected void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (binding || data.getPay().getValue() == position + 1) {
                return;
            }
            Api.with(context).modifyOrderMenu(data.getId(), position + 1)
                    .retryWhen(RxUtils::exponentialBackoff)
                    .filter(SimpleApiResult::isSuccess)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        data.setPay(Pay.valueOf(position + 1));
                        notifyItemChanged(this.position);
                        publishSubject.onNext(data);
                    });
        }

        private void addAttributeView(MenuAttribute attribute) {
            TextView view = new TextView(context);
            view.setText(attribute.getName());
            view.setGravity(Gravity.CENTER);
            view.setPadding(attributeItemPadding, attributeItemPadding / 2,
                    attributeItemPadding, attributeItemPadding / 2);
            view.setBackgroundResource(R.drawable.bg_border_point);
            view.setTextColor(attributeItemTextColor);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            FlowLayout.LayoutParams params =
                    new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(attributeItemMargin, attributeItemMargin,
                    attributeItemMargin, attributeItemMargin);

            attributeContainer.addView(view, params);
        }

        private MenuAttribute createTempAttribute(int id, String name) {
            MenuAttribute attribute = new MenuAttribute();
            attribute.setId(id);
            attribute.setName(name);
            attribute.setAvailable(true);
            return attribute;
        }
    }
}
