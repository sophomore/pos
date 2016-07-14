package org.jaram.ds.fragment;

import android.support.v7.app.AlertDialog;
import android.view.View;

import org.jaram.ds.R;
import org.jaram.ds.dialogs.MenuInfoDialog;
import org.jaram.ds.managers.MenuManager;
import org.jaram.ds.models.Menu;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.widgets.MenuListView;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class MenuManageFragment extends BaseFragment {

    private static final String DIALOG_MENU_INFO = "dialog.menu_info";

    @BindView(R.id.menuList) MenuListView menuListView;

    public static MenuManageFragment newInstance() {
        return new MenuManageFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_menu_manage;
    }

    @Override
    protected void setupLayout(View view) {
        menuListView.setOnItemClickListener(this::showMenuInfoDialog);
        menuListView.setOnItemLongClickListener(this::showMenuDeleteDialog);
    }

    @OnClick(R.id.add)
    protected void addMenu() {
        showMenuInfoDialog(null);
    }

    protected void showMenuInfoDialog(Menu menu) {
        MenuInfoDialog dialog = MenuInfoDialog.newInstance(menu);
        dialog.setOnConfirmListener(menuListView::notifyAllDataSetChanged);
        dialog.show(getFragmentManager(), DIALOG_MENU_INFO);
    }

    protected void showMenuDeleteDialog(Menu menu) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.format_delete_menu, menu.getName()))
                .setPositiveButton(R.string.label_yes, (dialog, which) -> deleteMenu(menu))
                .setNegativeButton(R.string.label_no, null)
                .show();
    }

    private void deleteMenu(Menu menu) {
        showProgress();
        addSubscription(Api.with(getActivity()).deleteMenu(menu)
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(this::hideProgress)
                .subscribe(RxUtils::doNothing, SLog::e, () ->
                        MenuManager.getInstance(getActivity()).refresh()));
    }
}
