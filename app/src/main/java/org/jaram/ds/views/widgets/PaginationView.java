package org.jaram.ds.views.widgets;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.jaram.ds.R;
import org.jaram.ds.views.adapters.HeaderViewRecyclerAdapter;
import org.jaram.ds.models.PaginationData;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.adapters.PaginationAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chulwoo on 16. 4. 19..
 */
public class PaginationView<T> extends BaseRecyclerView implements PaginationAdapter.Loader {

    public interface Loader<T> {
        Observable<PaginationData<T>> load(int page);
    }

    public interface Tracker {
        void onPrepareRefresh();

        void onRefresh();

        void onFinishRefresh(boolean success);

        void onPrepareLoadMore();

        void onLoadMore();

        void onFinishLoadMore(boolean success);
    }

    @BindDimen(R.dimen.spacing_normal) int LOAD_MORE_PADDING;
    @BindDimen(R.dimen.progress_size_small) int PROGRESS_SIZE;
    @BindColor(R.color.accent) int PROGRESS_COLOR;

    private View refreshProgressView;
    private View emptyView;
    private View loadMoreProgressView;

    private PaginationAdapter<T> adapter;
    private HeaderViewRecyclerAdapter headerAdapter;

    private Subscription loadingSubscription;
    private Loader<T> loader;
    private ArrayList<Tracker> trackers;

    private int currentPage = 1;
    private int totalItemCount = 0;
    private boolean hasNext = false;
    boolean addedEmptyView = false;

    public PaginationView(Context context) {
        this(context, null);
    }

    public PaginationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaginationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        ButterKnife.bind(this);
        trackers = new ArrayList<>();
        setLayoutManager(new LinearLayoutManager(context));
        refreshProgressView = createRefreshProgressView(context);
        emptyView = createEmptyView(context);
        loadMoreProgressView = createLoadMoreProgressView(context);
        setClipToPadding(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
        ((DefaultItemAnimator) getItemAnimator()).setSupportsChangeAnimations(true);
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setLoader(Loader<T> loader) {
        this.loader = loader;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof PaginationAdapter)) {
            throw new IllegalArgumentException("PaginationView's adapter must be has PaginationAdapter");
        }

        this.adapter = (PaginationAdapter<T>) adapter;
        this.adapter.setLoader(this);
        headerAdapter = createHeaderAdapter(adapter);
        super.setAdapter(headerAdapter);
    }

    public void addHeaderView(View view) {
        if (headerAdapter == null) {
            throw new IllegalStateException("should be setAdapter before calling addHeaderView");
        }

        headerAdapter.addHeaderView(view);
    }

    public void setRefreshProgressView(View view) {
        this.refreshProgressView = view;
    }

    @Override
    public void setEmptyView(View view) {
        this.emptyView = view;
    }

    public void setLoadMoreProgressView(View view) {
        this.loadMoreProgressView = view;
    }

    public void addTracker(Tracker tracker) {
        trackers.add(tracker);
    }

    private View createRefreshProgressView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.view_refresh_progress, this, false);
    }

    private View createEmptyView(Context context) {
        View emptyView = LayoutInflater.from(context).inflate(R.layout.view_empty_default, this, false);
        ButterKnife.findById(emptyView, R.id.refresh).setOnClickListener(v -> refresh(true));
        return emptyView;
    }

    protected View createLoadMoreProgressView(Context context) {
        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        container.setPadding(LOAD_MORE_PADDING, LOAD_MORE_PADDING, LOAD_MORE_PADDING, LOAD_MORE_PADDING);

        CircularProgressBar progressView = new CircularProgressBar(context);
        progressView.setLayoutParams(new FrameLayout.LayoutParams(
                PROGRESS_SIZE,
                PROGRESS_SIZE,
                Gravity.CENTER));
        progressView.setIndeterminateDrawable(new CircularProgressDrawable.Builder(context)
                .color(PROGRESS_COLOR)
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .build());
        progressView.setIndeterminate(true);

        container.addView(progressView);
        container.setVisibility(View.GONE);
        return container;
    }

    protected HeaderViewRecyclerAdapter createHeaderAdapter(Adapter adapter) {
        HeaderViewRecyclerAdapter headerAdapter = new HeaderViewRecyclerAdapter(adapter);
        headerAdapter.addFooterView(loadMoreProgressView);
        return headerAdapter;
    }

    protected void prepareRefresh() {
        for (Tracker tracker : trackers) {
            tracker.onPrepareRefresh();
        }
        setEmptyViewVisibility(false);
        showRefreshProgressView();
    }

    public void refresh(boolean force) {
        if (force && loadingSubscription != null) {
            cancelRefresh();
        }

        if (loadingSubscription == null || loadingSubscription.isUnsubscribed()) {
            currentPage = 1;
            prepareRefresh();
            for (Tracker tracker : trackers) {
                tracker.onRefresh();
            }

            loadingSubscription = loadPage(currentPage).subscribe(this::onSuccessRefresh, this::onFailureRefresh);
        }
    }

    public void cancelRefresh() {
        loadingSubscription.unsubscribe();
    }

    protected void onSuccessRefresh(List<T> data) {
        adapter.clear();
        adapter.addAll(data);
        adapter.notifyDataSetChanged();
        onFinishRefresh(true);
    }

    protected void onFailureRefresh(Throwable e) {
        // TODO: empty view 대신 error view가 보여야 함
        SLog.e(e);
        hideRefreshProgressView();
        adapter.clear();
        adapter.notifyDataSetChanged();
        onFinishRefresh(false);
    }

    protected void onFinishRefresh(boolean success) {
        hideRefreshProgressView();
        setEmptyViewVisibility(adapter.getItemCount() == 0);
        for (Tracker tracker : trackers) {
            tracker.onFinishRefresh(success);
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    protected void onPrepareLoadMore() {
        for (Tracker tracker : trackers) {
            tracker.onPrepareLoadMore();
        }
        showLoadMoreProgressView();
    }

    @Override
    public void loadMore() {
        if (loader == null) {
            throw new IllegalStateException("loader not initialized");
        }

        currentPage++;
        onPrepareLoadMore();
        for (Tracker tracker : trackers) {
            tracker.onLoadMore();
        }
        loadPage(currentPage)
                .subscribe(this::onSuccessLoadMore, this::onFailureLoadMore);
    }

    protected void onSuccessLoadMore(List<T> data) {
        int position = adapter.getItemCount();
        adapter.addAll(data);
        adapter.notifyItemRangeInserted(position, data.size());
        onFinishLoadMore(true);
    }

    protected void onFailureLoadMore(Throwable e) {
        SLog.e(e);
        onFinishLoadMore(false);
        // TODO: error view가 보여야 함 || 더보기
    }

    protected void onFinishLoadMore(boolean success) {
        hideLoadMoreProgressView();
        for (Tracker tracker : trackers) {
            tracker.onFinishLoadMore(success);
        }
    }

    protected Observable<List<T>> loadPage(int page) {
        if (loader == null) {
            throw new IllegalStateException("loader not initialized");
        }
        return loader.load(page)
                .map(paginationData -> {
                    hasNext = !TextUtils.isEmpty(paginationData.getNext());
                    totalItemCount = paginationData.getCount();

                    return paginationData;
                })
                .map(PaginationData::getResults)
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected void setEmptyViewVisibility(boolean visible) {
        if (visible && !addedEmptyView) {
            headerAdapter.addFooterView(emptyView);
            addedEmptyView = true;
        } else if (!visible && addedEmptyView) {
            headerAdapter.removeFooterView(emptyView);
            addedEmptyView = false;
        }
    }

    private void showRefreshProgressView() {
        headerAdapter.addHeaderView(refreshProgressView);
        headerAdapter.notifyDataSetChanged();
    }

    private void hideRefreshProgressView() {
        headerAdapter.removeHeaderView(refreshProgressView);
        headerAdapter.notifyDataSetChanged();
    }

    private void showLoadMoreProgressView() {
        loadMoreProgressView.setVisibility(View.VISIBLE);
    }

    private void hideLoadMoreProgressView() {
        loadMoreProgressView.setVisibility(View.GONE);
    }

    public static class SimpleTracker implements Tracker {

        @Override
        public void onPrepareRefresh() {

        }

        @Override
        public void onRefresh() {

        }

        @Override
        public void onFinishRefresh(boolean success) {

        }

        @Override
        public void onPrepareLoadMore() {

        }

        @Override
        public void onLoadMore() {

        }

        @Override
        public void onFinishLoadMore(boolean success) {

        }
    }
}

