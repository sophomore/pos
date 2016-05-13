package org.jaram.ds.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.models.OrderMenu;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.BaseRecyclerView;

import butterknife.Bind;
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

        @Bind(R.id.container) View container;
        @Bind(R.id.name) TextView nameView;
        @Bind(R.id.price) TextView priceView;
        @Bind(R.id.addCurry) TextView addCurryView;
        @Bind(R.id.addTwice) TextView addTwiceView;
        @Bind(R.id.addTakeout) TextView addTakeoutView;
        @Bind(R.id.paySelector) Spinner paySelectView;

        public DetailOrderMenuItemViewHolder(View itemView) {
            super(itemView);
            paySelectView.setAdapter(new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line,
                    new String[]{"현금", "카드", "서비스", "외상"}));
        }

        @Override
        protected void bind() {
            nameView.setText(data.getMenu().getName());
            priceView.setText(StringUtils.format("%d", data.getTotalPrice()));
            addCurryView.setVisibility(data.isCurry() ? View.VISIBLE : View.GONE);
            addTwiceView.setVisibility(data.isTwice() ? View.VISIBLE : View.GONE);
            addTakeoutView.setVisibility(data.isTakeout() ? View.VISIBLE : View.GONE);

            switch (data.getPay()) {
                case Data.PAY_CASH:
                    paySelectView.setSelection(0);
                    container.setBackgroundResource(android.R.color.white);
                    break;
                case Data.PAY_CARD:
                    paySelectView.setSelection(1);
                    container.setBackgroundResource(android.R.color.white);
                    break;
                case Data.PAY_SERVICE:
                    paySelectView.setSelection(2);
                    container.setBackgroundResource(android.R.color.white);
                    break;
                case Data.PAY_CREDIT:
                    paySelectView.setSelection(3);
                    container.setBackgroundResource(R.color.accent);
                    break;
            }
        }

        @OnItemSelected(R.id.paySelector)
        protected void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (data.getPay() == position + 1) {
                return;
            }
            Api.with(context).modifyOrderMenu(data.getId(), position + 1)
                    .retryWhen(RxUtils::exponentialBackoff)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccess()) {
                            data.setPay(position + 1);
                            data.setPay(position < 4);
                            notifyItemChanged(this.position);
                            publishSubject.onNext(data);
                        }
                    });
        }
    }
}