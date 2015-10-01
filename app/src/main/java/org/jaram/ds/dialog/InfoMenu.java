package org.jaram.ds.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Category;
import org.jaram.ds.data.struct.Menu;

import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class InfoMenu extends Dialog {

    Menu menu = null;

    public InfoMenu(Context context, Menu menu) {
        super(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        this.menu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_info_menu);

        EditText nameForm = (EditText)findViewById(R.id.menu_nameContent);
        EditText priceForm = (EditText)findViewById(R.id.menu_priceContent);
        Spinner categoryForm = (Spinner)findViewById(R.id.menu_categoryContent);

        ArrayList<Category> categories = new ArrayList<Category>();
        for (Object category : Data.categories.values().toArray()) {
            categories.add((Category)category);
        }

        ArrayList<String> category_names = new ArrayList<String>();
        for (Category category : categories) {
            category_names.add(category.getName());
        }

        categoryForm.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, category_names.toArray()));
        categoryForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO: set Menu
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (menu != null) {
            nameForm.setText(menu.getName());
            priceForm.setText(menu.getName() + "");
            categoryForm.setSelection(categories.indexOf(menu.getCategory()));
        }

        ((Button)findViewById(R.id.cancelBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((Button)findViewById(R.id.confirmBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: save
                dismiss();
            }
        });
    }
}
