package org.jaram.ds.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.activities.BaseActivity;
import org.jaram.ds.managers.MenuManager;
import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.models.result.SimpleApiResult;
import org.jaram.ds.networks.Api;
import org.jaram.ds.util.RxUtils;
import org.jaram.ds.util.SLog;
import org.jaram.ds.views.widgets.EditPriceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class MenuInfoDialog extends AppCompatDialogFragment {

    @BindView(R.id.name) EditText nameView;
    @BindView(R.id.price) EditPriceView priceView;
    @BindView(R.id.category) Spinner categoryView;

    private Action0 confirmListener;

    private Menu menu;

    public static MenuInfoDialog newInstance(Menu menu) {
        return new MenuInfoDialog().setMenu(menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_info_menu, container);
        ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (menu == null) {
            menu = new Menu();
            menu.setId(-1);
        }

        categoryView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Category.values()));

        setViewByMenu(menu);

        return view;
    }

    @Override
    @OnClick(R.id.cancel)
    public void dismiss() {
        super.dismiss();
    }

    public MenuInfoDialog setMenu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public void setOnConfirmListener(Action0 listener) {
        this.confirmListener = listener;
    }

    @OnClick(R.id.confirm)
    protected void save() {
        showProgress();
        menu.setName(nameView.getText().toString());
        menu.setPrice(priceView.getNumber());
        menu.setCategory((Category) categoryView.getSelectedItem());
        getApplyObservable()
                .retryWhen(RxUtils::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        onSuccessApplyMenu();
                    } else {
                        onFailureApplyMenu();
                    }
                }, e -> {
                    SLog.e(e);
                    onFailureApplyMenu();
                });

        if (confirmListener != null) {
            confirmListener.call();
        }
    }

    protected void setViewByMenu(Menu menu) {
        nameView.setText(menu.getName() == null ? "" : menu.getName());
        priceView.setNumber(menu.getPrice());
        categoryView.setSelection(menu.getCategory() == null ? 0 : menu.getCategory().ordinal());
    }

    protected void onSuccessApplyMenu() {
        MenuManager.getInstance(getActivity()).refresh();
        hideProgress();
        dismiss();
    }

    protected void onFailureApplyMenu() {
        Toast.makeText(getActivity(), R.string.message_failure_apply_menu, Toast.LENGTH_SHORT).show();
        hideProgress();
    }

    private Observable<SimpleApiResult> getApplyObservable() {
        return this.menu.getId() == -1
                ? Api.with(getActivity()).addMenu(menu)
                : Api.with(getActivity()).modifyMenu(menu);
    }

    private void showProgress() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showProgress();
        }
    }

    private void hideProgress() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideProgress();
        }
    }
}