package org.jaram.ds.views.widgets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.jaram.ds.R;
import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.GridSpaceItemDecoration;
import org.jaram.ds.views.adapters.CollapseMenuAdapter;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 5. 12..
 */
public class MenuCollapseListView extends LinearLayout implements CollapseMenuAdapter.OnClickMenuListener {

    @Bind(R.id.cutlet) Button cutletButton;
    @Bind(R.id.rice) Button riceButton;
    @Bind(R.id.noodle) Button noodleButton;
    @Bind(R.id.etc) Button etcButton;
    @Bind(R.id.cutletList) BaseRecyclerView cutletView;
    @Bind(R.id.riceList) BaseRecyclerView riceView;
    @Bind(R.id.noodleList) BaseRecyclerView noodleView;
    @Bind(R.id.etcList) BaseRecyclerView etcView;

    @BindDimen(R.dimen.collapse_menu_item_spacing) int itemSpacing;

    private CollapseMenuAdapter cutletAdapter;
    private CollapseMenuAdapter riceAdapter;
    private CollapseMenuAdapter noodleAdapter;
    private CollapseMenuAdapter etcAdapter;

    private CollapseMenuAdapter.OnClickMenuListener listener;

    public MenuCollapseListView(Context context) {
        this(context, null);
    }

    public MenuCollapseListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuCollapseListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MenuCollapseListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.view_menu_collapse_list, this);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onClick(Menu menu) {
        if (listener != null) {
            listener.onClick(menu);
        }
    }

    @OnClick(R.id.cutlet)
    public void openCutlet() {
        if (cutletView.getVisibility() == View.VISIBLE) {
            closeAll();
        } else {
            closeAll();
            cutletView.setVisibility(View.VISIBLE);
            cutletButton.setSelected(true);
        }
    }

    @OnClick(R.id.rice)
    public void openRice() {
        if (riceView.getVisibility() == View.VISIBLE) {
            closeAll();
        } else {
            closeAll();
            riceView.setVisibility(View.VISIBLE);
            riceButton.setSelected(true);
        }
    }

    @OnClick(R.id.noodle)
    public void openNoodle() {
        if (noodleView.getVisibility() == View.VISIBLE) {
            closeAll();
        } else {
            closeAll();
            noodleView.setVisibility(View.VISIBLE);
            noodleButton.setSelected(true);
        }
    }

    @OnClick(R.id.etc)
    public void openEtc() {
        if (etcView.getVisibility() == View.VISIBLE) {
            closeAll();
        } else {
            closeAll();
            etcView.setVisibility(View.VISIBLE);
            etcButton.setSelected(true);
        }
    }

    public void setOnClickMenuListener(CollapseMenuAdapter.OnClickMenuListener listener) {
        this.listener = listener;
    }

    public void refresh() {
        Api.with(getContext()).getAllMenus()
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMenus, SLog::e);
    }

    public void setMenus(List<Menu> menus) {
        for (Menu menu : menus) {
            switch(menu.getCategoryId()) {
                case Category.CUTLET:
                    cutletAdapter.add(menu);
                    break;
                case Category.RICE:
                    riceAdapter.add(menu);
                    break;
                case Category.NOODLE:
                    noodleAdapter.add(menu);
                    break;
                case Category.ETC:
                    etcAdapter.add(menu);
                    break;
            }
        }

        notifyAllDataSetChanged();
    }

    public void notifyAllDataSetChanged() {
        cutletAdapter.notifyDataSetChanged();
        riceAdapter.notifyDataSetChanged();
        noodleAdapter.notifyDataSetChanged();
        etcAdapter.notifyDataSetChanged();
    }

    public void closeAll() {
        cutletButton.setSelected(false);
        riceButton.setSelected(false);
        noodleButton.setSelected(false);
        etcButton.setSelected(false);

        cutletView.setVisibility(View.GONE);
        riceView.setVisibility(View.GONE);
        noodleView.setVisibility(View.GONE);
        etcView.setVisibility(View.GONE);
    }

    public void setAccentMenuList(Set<Menu> menus) {
        cutletAdapter.setAccentList(menus);
        riceAdapter.setAccentList(menus);
        noodleAdapter.setAccentList(menus);
        etcAdapter.setAccentList(menus);
    }

    protected void init() {
        cutletAdapter = new CollapseMenuAdapter();
        riceAdapter = new CollapseMenuAdapter();
        noodleAdapter = new CollapseMenuAdapter();
        etcAdapter = new CollapseMenuAdapter();

        cutletAdapter.setOnClickMenuListener(this);
        riceAdapter.setOnClickMenuListener(this);
        noodleAdapter.setOnClickMenuListener(this);
        etcAdapter.setOnClickMenuListener(this);

        cutletView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        riceView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        noodleView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        etcView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        cutletView.setAdapter(cutletAdapter);
        riceView.setAdapter(riceAdapter);
        noodleView.setAdapter(noodleAdapter);
        etcView.setAdapter(etcAdapter);

        cutletView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, false));
        riceView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, false));
        noodleView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, false));
        etcView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, false));

        refresh();
    }
}
