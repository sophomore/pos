package org.jaram.ds.views.adapters;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.models.Menu;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.widgets.BaseRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnTouch;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class MenuAdapter extends BaseRecyclerView.BaseListAdapter<Menu> {

    public interface OnItemClickListener {
        void onClick(Menu menu);
    }

    public interface OnItemLongClickListener {
        void onLongClick(Menu menu);
    }

    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    @Override
    public BaseRecyclerView.BaseViewHolder<Menu> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MenuItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    protected class MenuItemViewHolder extends BaseRecyclerView.BaseViewHolder<Menu> {

        @BindView(R.id.name) TextView nameView;
        @BindView(R.id.price) TextView priceView;

        public MenuItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this::onLongClick);
        }

        @Override
        protected void bind() {
            nameView.setText(data.getName());
            priceView.setText(StringUtils.format("%d", data.getPrice()));
        }

        @Override
        protected void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onClick(data);
            }
        }

        protected boolean onLongClick(View v) {
            if (itemLongClickListener != null) {
                itemLongClickListener.onLongClick(data);
            }
            return false;
        }

        @OnTouch(R.id.container)
        protected boolean onTouch(View view, MotionEvent event) {
            view.setBackgroundResource(event.getAction() == MotionEvent.ACTION_DOWN
                    ? R.color.point
                    : R.color.dark);
            return false;
        }
    }

}
