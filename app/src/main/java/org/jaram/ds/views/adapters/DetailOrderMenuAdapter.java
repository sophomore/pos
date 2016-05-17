package org.jaram.ds.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.models.Pay;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.widgets.BaseRecyclerView;

import butterknife.BindView;
import butterknife.OnItemSelected;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdekim43 on 2016. 1. 29..
 */
public class DetailOrderMenuAdapter extends BaseRecyclerView.BaseListAdapter<OrderMenu> {

    private PublishSubject<OrderMenu> publishSubject = PublishSubject.create();

    @Override
    public BaseRecyclerView.BaseViewHolder<OrderMenu> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DetailOrderMenuItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_ordermenu, parent, false));
    }

    public Observable<OrderMenu> asObservable() {
        return publishSubject;
    }

    protected class DetailOrderMenuItemViewHolder extends BaseRecyclerView.BaseViewHolder<OrderMenu> {

        @BindView(R.id.container) View container;
        @BindView(R.id.name) TextView nameView;
        @BindView(R.id.price) TextView priceView;
        @BindView(R.id.addCurry) TextView addCurryView;
        @BindView(R.id.addTwice) TextView addTwiceView;
        @BindView(R.id.addTakeout) TextView addTakeoutView;
        @BindView(R.id.paySelector) Spinner paySelectView;

        public DetailOrderMenuItemViewHolder(View itemView) {
            super(itemView);
            paySelectView.setAdapter(new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line,
                    new String[]{"현금", "카드", "서비스", "외상"}));
        }

        @Override
        protected void bind() {
            nameView.setText(data.getMenu().getName());
            priceView.setText(context.getString(R.string.format_money, data.getTotalPrice()));
            addCurryView.setVisibility(data.isCurry() ? View.VISIBLE : View.GONE);
            addTwiceView.setVisibility(data.isTwice() ? View.VISIBLE : View.GONE);
            addTakeoutView.setVisibility(data.isTakeout() ? View.VISIBLE : View.GONE);

            switch (data.getPay()) {
                case CASH:
                    paySelectView.setSelection(0);
                    container.setBackgroundResource(android.R.color.white);
                    break;
                case CARD:
                    paySelectView.setSelection(1);
                    container.setBackgroundResource(android.R.color.white);
                    break;
                case SERVICE:
                    paySelectView.setSelection(2);
                    container.setBackgroundResource(android.R.color.white);
                    break;
                case CREDIT:
                    paySelectView.setSelection(3);
                    container.setBackgroundResource(R.color.accent);
                    break;
            }
        }

        @OnItemSelected(R.id.paySelector)
        protected void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (data.getPay().getValue() == position + 1) {
                return;
            }
            Api.with(context).modifyOrderMenu(data.getId(), position + 1)
                    .retryWhen(RxUtils::exponentialBackoff)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccess()) {
                            data.setPay(Pay.valueOf(position + 1));
                            data.setPay(position < 4);
                            notifyItemChanged(this.position);
                            publishSubject.onNext(data);
                        }
                    });
        }
    }
}