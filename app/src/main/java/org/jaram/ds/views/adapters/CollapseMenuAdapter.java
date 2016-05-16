package org.jaram.ds.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.jaram.ds.R;
import org.jaram.ds.models.Menu;
import org.jaram.ds.views.widgets.BaseRecyclerView;

import java.util.Set;

import butterknife.BindView;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class CollapseMenuAdapter extends BaseRecyclerView.BaseListAdapter<Menu> {

    public interface OnClickMenuListener {
        void onClick(Menu menu);
    }

    private OnClickMenuListener listener;

    private Set<Menu> accentList;

    @Override
    public BaseRecyclerView.BaseViewHolder<Menu> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CollapseMenuViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collapse_menu, parent, false));
    }

    public void setOnClickMenuListener(OnClickMenuListener listener) {
        this.listener = listener;
    }

    public void setAccentList(Set<Menu> list) {
        this.accentList = list;
    }

    protected class CollapseMenuViewHolder extends BaseRecyclerView.BaseViewHolder<Menu> {

        @BindView(R.id.item) Button itemButton;

        public CollapseMenuViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind() {
            itemButton.setText(data.getName());

            itemButton.setSelected(accentList != null && accentList.contains(data));
        }

        @Override
        protected void onClick(View v) {
            super.onClick(v);
            if (listener != null) {
                listener.onClick(data);
            }
            notifyItemChanged(position);
        }
    }
}
