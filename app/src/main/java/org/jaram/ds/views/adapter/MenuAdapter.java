package org.jaram.ds.views.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.models.Menu;
import org.jaram.ds.util.SLog;
import org.jaram.ds.util.StringUtils;
import org.jaram.ds.views.BaseRecyclerView;

import java.util.List;

import butterknife.Bind;
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

    private MenuAdapter() {
        super();
    }

    public MenuAdapter(List<Menu> menus) {
        super();
        addAll(menus);
    }

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

        @Bind(R.id.name) TextView nameView;
        @Bind(R.id.price) TextView priceView;

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
