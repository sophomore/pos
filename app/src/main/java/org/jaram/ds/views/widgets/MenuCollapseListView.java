package org.jaram.ds.views.widgets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.jaram.ds.R;
import org.jaram.ds.managers.MenuManager;
import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.GridSpaceItemDecoration;
import org.jaram.ds.views.adapters.CollapseMenuAdapter;

import java.util.List;
import java.util.Set;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 5. 12..
 */
public class MenuCollapseListView extends LinearLayout implements CollapseMenuAdapter.OnClickMenuListener {

    @BindView(R.id.cutlet) Button cutletButton;
    @BindView(R.id.rice) Button riceButton;
    @BindView(R.id.noodle) Button noodleButton;
    @BindView(R.id.etc) Button etcButton;
    @BindView(R.id.cutletList) BaseRecyclerView cutletView;
    @BindView(R.id.riceList) BaseRecyclerView riceView;
    @BindView(R.id.noodleList) BaseRecyclerView noodleView;
    @BindView(R.id.etcList) BaseRecyclerView etcView;

    @BindDimen(R.dimen.collapse_menu_item_spacing) int itemSpacing;

    private CollapseMenuAdapter cutletAdapter;
    private CollapseMenuAdapter riceAdapter;
    private CollapseMenuAdapter noodleAdapter;
    private CollapseMenuAdapter etcAdapter;

    private CollapseMenuAdapter.OnClickMenuListener listener;

    private MenuManager manager;

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
        manager = MenuManager.getInstance(context);
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

    public void refreshMenuList() {
        cutletAdapter.clear();
        cutletAdapter.addAll(manager.getMenusByCategory(Category.CUTLET));
        riceAdapter.clear();
        riceAdapter.addAll(manager.getMenusByCategory(Category.RICE));
        noodleAdapter.clear();
        noodleAdapter.addAll(manager.getMenusByCategory(Category.NOODLE));
        etcAdapter.clear();
        etcAdapter.addAll(manager.getMenusByCategory(Category.ETC));

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

        cutletView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, true));
        riceView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, true));
        noodleView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, true));
        etcView.addItemDecoration(new GridSpaceItemDecoration(3, itemSpacing, true));

        manager.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(menus -> refreshMenuList());

        refreshMenuList();
    }
}
