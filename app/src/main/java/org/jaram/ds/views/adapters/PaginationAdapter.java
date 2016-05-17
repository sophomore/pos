package org.jaram.ds.views.adapters;

import org.jaram.ds.views.widgets.BaseRecyclerView;
import org.jaram.ds.views.widgets.PaginationView;

/**
 * Created by chulwoo on 16. 3. 11..
 * Updated by chulwoo on 16. 4. 19..
 * {@link PaginationView} 구현하면서 이름 변경, {@link PaginationAdapter.Loader}를 나중에 지정할 수 있도록 변경함
 */

public abstract class PaginationAdapter<Data> extends BaseRecyclerView.BaseListAdapter<Data> {

    public interface Loader {
        boolean hasNext();

        void loadMore();
    }

    protected Loader loader;
    protected int loadingOffset;

    public PaginationAdapter() {
        this(null);
    }

    public PaginationAdapter(Loader loader) {
        this(loader, 1);
    }

    public PaginationAdapter(Loader loader, int loadingOffset) {
        this.loader = loader;
        this.loadingOffset = loadingOffset;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public void setLoadingOffset(int loadingOffset) {
        this.loadingOffset = loadingOffset;
    }

    @Override
    final public void onBindViewHolder(BaseRecyclerView.BaseViewHolder<Data> holder, int position) {
        if (loader.hasNext() && (position >= getItemCount() - loadingOffset)) {
            loader.loadMore();
        }

        super.onBindViewHolder(holder, position);
    }
}