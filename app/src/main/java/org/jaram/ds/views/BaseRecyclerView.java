package org.jaram.ds.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import org.jaram.ds.adapter.HeaderViewRecyclerAdapter;
import org.jaram.ds.util.SLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by chulwoo on 15. 7. 30..
 */
public class BaseRecyclerView extends RecyclerView {

    private View mEmptyView;

    final private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            invalidate();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            invalidate();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            invalidate();
        }
    };

    public BaseRecyclerView(Context context) {
        this(context, null);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(context));
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        invalidate();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }

        invalidate();
    }

    public void invalidate() {
        Adapter adapter = getAdapter();
        if (mEmptyView != null && adapter != null) {
            int count = adapter.getItemCount();
            if (adapter instanceof HeaderViewRecyclerAdapter) {
                count = ((HeaderViewRecyclerAdapter) adapter).getWrappedItemCount();
            }
            final boolean emptyViewVisible = count == 0;
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
//            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    public static abstract class BaseListAdapter<Data> extends RecyclerView.Adapter<BaseViewHolder<Data>> {

        private List<Data> mItems;

        public BaseListAdapter() {
            super();
            mItems = new ArrayList<>();
        }

        @Override
        public void onBindViewHolder(BaseViewHolder<Data> holder, int position) {
            holder.bind(getItem(position), position);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void clear() {
            mItems.clear();
        }

        public void remove(int index) {
            mItems.remove(index);
        }

        public void remove(Data item) {
            mItems.remove(item);
        }

        public void addAll(Collection<? extends Data> items) {
            mItems.addAll(items);
        }

        public void add(Data item) {
            mItems.add(item);
        }

        public void add(int position, Data item) {
            mItems.add(position, item);
        }

        public boolean isEmpty() {
            return mItems.isEmpty();
        }

        public Data getItem(int index) {
            return mItems.get(index);
        }

        public List<Data> getAll() {
            return mItems;
        }
    }

    public static abstract class BaseViewHolder<Data> extends ViewHolder {

        protected abstract void bind();

        protected Context context;
        protected Data data;
        protected int position;

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this::onClick);
        }

        public void bind(Data data, int position) {
            this.data = data;
            this.position = position;
            bind();
        }

        protected void onClick(View v) {

        }
    }
}