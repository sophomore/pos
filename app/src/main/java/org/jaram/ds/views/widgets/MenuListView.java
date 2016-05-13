package org.jaram.ds.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.jaram.ds.R;
import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.views.VerticalSpaceItemDecoration;
import org.jaram.ds.views.adapters.MenuAdapter;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by jdekim43 on 2016. 1. 30..
 */
public class MenuListView extends LinearLayout implements MenuAdapter.OnItemClickListener, MenuAdapter.OnItemLongClickListener {

    @Bind(R.id.cutlet) BaseRecyclerView cutletListView;
    @Bind(R.id.rice) BaseRecyclerView riceListView;
    @Bind(R.id.noodle) BaseRecyclerView noodleListView;
    @Bind(R.id.etc) BaseRecyclerView etcListView;

    @BindDimen(R.dimen.spacing_micro) int itemSpacing;

    private MenuAdapter cutletAdapter;
    private MenuAdapter riceAdapter;
    private MenuAdapter noodleAdapter;
    private MenuAdapter etcAdapter;

    private MenuAdapter.OnItemClickListener onItemClickListener;
    private MenuAdapter.OnItemLongClickListener onItemLongClickListener;

    public MenuListView(Context context) {
        this(context, null);
    }

    public MenuListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MenuListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.view_menulist, this);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onClick(Menu menu) {
        if (onItemClickListener != null) {
            onItemClickListener.onClick(menu);
        }
    }

    @Override
    public void onLongClick(Menu menu) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onLongClick(menu);
        }
    }

    public void setOnItemClickListener(MenuAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(MenuAdapter.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void notifyAllDataSetChanged() {
        cutletAdapter.notifyDataSetChanged();
        riceAdapter.notifyDataSetChanged();
        noodleAdapter.notifyDataSetChanged();
        etcAdapter.notifyDataSetChanged();
    }

    private void init() {
        Realm db = Realm.getInstance(getContext());
        cutletAdapter = new MenuAdapter(db.where(Category.class).equalTo("id", 1).findFirst().getMenus());
        riceAdapter = new MenuAdapter(db.where(Category.class).equalTo("id", 2).findFirst().getMenus());
        noodleAdapter = new MenuAdapter(db.where(Category.class).equalTo("id", 3).findFirst().getMenus());
        etcAdapter = new MenuAdapter(db.where(Category.class).equalTo("id", 4).findFirst().getMenus());

        cutletAdapter.setOnItemClickListener(this);
        riceAdapter.setOnItemClickListener(this);
        noodleAdapter.setOnItemClickListener(this);
        etcAdapter.setOnItemClickListener(this);
        cutletAdapter.setOnItemLongClickListener(this);
        riceAdapter.setOnItemLongClickListener(this);
        noodleAdapter.setOnItemLongClickListener(this);
        etcAdapter.setOnItemLongClickListener(this);

        cutletListView.setAdapter(cutletAdapter);
        riceListView.setAdapter(riceAdapter);
        noodleListView.setAdapter(noodleAdapter);
        etcListView.setAdapter(etcAdapter);

        cutletListView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacing));
        riceListView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacing));
        noodleListView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacing));
        etcListView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacing));
    }
}