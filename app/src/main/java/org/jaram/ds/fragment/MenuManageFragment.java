package org.jaram.ds.fragment;

import android.support.v7.app.AlertDialog;
import android.view.View;

import org.jaram.ds.R;
import org.jaram.ds.dialogs.MenuInfoDialog;
import org.jaram.ds.models.Menu;
import org.jaram.ds.views.widgets.MenuListView;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class MenuManageFragment extends BaseFragment {

    private static final String DIALOG_MENU_INFO = "dialog.menu_info";

    @Bind(R.id.menuList) MenuListView menuListView;

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
        Realm db = Realm.getInstance(getActivity());
        Menu savedMenu = db.where(Menu.class).equalTo("id", menu.getId()).findFirst();
        db.beginTransaction();
        savedMenu.removeFromRealm();
        db.commitTransaction();
        //TODO: delete server
//        try {
//            Http.delete(Data.SERVER_URL + "menu/" + id, null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
