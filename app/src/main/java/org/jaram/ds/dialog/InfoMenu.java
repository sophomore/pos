package org.jaram.ds.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import org.jaram.ds.fragment.MenuManager;
import org.jaram.ds.util.Http;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class InfoMenu extends Dialog {

    Menu menu = null;
    Category selectedCategory;

    public InfoMenu(Context context, Menu menu) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.menu = menu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_info_menu);

        final EditText nameForm = (EditText)findViewById(R.id.menu_nameContent);
        final EditText priceForm = (EditText)findViewById(R.id.menu_priceContent);
        final Spinner categoryForm = (Spinner)findViewById(R.id.menu_categoryContent);

        final ArrayList<Category> categories = new ArrayList<Category>();
        for (Object category : Data.categories.values().toArray()) {
            categories.add((Category)category);
        }

        String[] category_names = new String[categories.size()];
        int i=0;
        for (Category category : categories) {
            category_names[i] = category.getName();
            i++;
        }

        categoryForm.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, category_names));
        categoryForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (menu != null) {
            nameForm.setText(menu.getName());
            nameForm.setEnabled(false);
            priceForm.setText(menu.getPrice() + "");
            categoryForm.setSelection(categories.indexOf(menu.getCategory()));
        }

        selectedCategory = categories.get(categoryForm.getSelectedItemPosition());

        ((Button)findViewById(R.id.cancelBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((Button) findViewById(R.id.confirmBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: save
                int menuId = -1;
                if (menu != null) menuId = menu.getId();

                Menu newMenu = new Menu(menuId, nameForm.getText().toString(),
                        Integer.parseInt(priceForm.getText().toString()),
                        Data.categories.get(selectedCategory.getId()));
                new AddMenuTask(getContext()).execute(newMenu.getId() + "", newMenu.getName(), newMenu.getPrice() + "", newMenu.getCategory().getId() + "");
            }
        });
    }

    public class AddMenuTask extends AsyncTask<String, Void, JSONObject> {

        Context context;
        ProgressDialog dialog;
        public AddMenuTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(context, "메뉴 관리", "메뉴 정보를 변경하고 있습니다.", true, false);
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject menuObj;
            HashMap<String, Object> param = new HashMap<>();
            param.put("name", params[1]);
            param.put("price", params[2]);
            param.put("category", params[3]);
            try {
                if (menu != null) {
                    menuObj = new JSONObject(Http.put(Data.SERVER_URL + "menu/" + params[0], param));
                    if (menuObj.getString("result").equals("success")) {
                        return menuObj.getJSONObject("new_menu");
                    }
                }
                else {
                    menuObj = new JSONObject(Http.post(Data.SERVER_URL + "menu", param));
                    if (menuObj.getString("result").equals("success")) {
                        return menuObj.getJSONObject("menu");
                    }
                }
                throw new IOException();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                new Menu(result.getInt("id"), result.getString("name"), result.getInt("price"),
                        Data.categories.get(result.getInt("category_id"))).create();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (menu != null) menu.destroy();
            dialog.dismiss();
            InfoMenu.this.dismiss();
        }
    }
}
