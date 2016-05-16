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

import org.jaram.ds.R;
import org.jaram.ds.models.Category;
import org.jaram.ds.models.Menu;
import org.jaram.ds.util.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rx.functions.Action0;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class MenuInfoDialog extends AppCompatDialogFragment {

    @BindView(R.id.name) EditText nameView;
    @BindView(R.id.price) EditText priceView;
    @BindView(R.id.category) Spinner categoryView;

    private Action0 confirmListener;

    private Menu menu;
    private List<Category> categoryList;

    public static MenuInfoDialog newInstance(Menu menu) {
        return new MenuInfoDialog().setMenu(menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_info_menu, container);
        ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        categoryList = Realm.getInstance(getActivity()).where(Category.class).findAll();

        categoryView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, categoryList));
        categoryView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Realm db = Realm.getInstance(getActivity());
                db.beginTransaction();
                menu.setCategory(categoryList.get(position));
                menu.setCategoryId(categoryList.get(position).getId());
                db.commitTransaction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (menu != null) {
            setViewByMenu(menu);
        }

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
        saveMenu(menu);
        if (confirmListener != null) {
            confirmListener.call();
        }
        dismiss();
    }

    protected void setViewByMenu(Menu menu) {
        nameView.setText(menu.getName());
        priceView.setText(StringUtils.format("%d", menu.getPrice()));
    }

    private void saveMenu(Menu menu) {
        Realm db = Realm.getInstance(getActivity());
        db.beginTransaction();
        db.copyToRealmOrUpdate(menu);
        db.commitTransaction();
        //TODO: update server
    }
}
