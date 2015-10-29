package org.jaram.ds.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Menu;
import org.jaram.ds.fragment.OrderManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by kjydiary on 15. 9. 30..
 */
public class OrderSearch extends Dialog {

    private Callbacks callbacks;

    HashMap<Integer, Menu> menus;
    Calendar startDate;
    Calendar endDate;
    ArrayList<Menu> selectedMenus;
    boolean cash = false;
    boolean card = false;
    boolean service = false;
    boolean credit = false;

    public OrderSearch(OrderManager om, HashMap<Integer, Menu> menus) {
        super(om.getActivity(), android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        callbacks = (Callbacks)om;
        this.menus = menus;
        selectedMenus = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_order);

        final Date date = new Date();

        startDate = Calendar.getInstance();
        startDate.setTime(date);

        endDate = Calendar.getInstance();
        endDate.setTime(date);

        final Button startDateBtn = (Button)findViewById(R.id.startDate);
        final Button endDateBtn = (Button)findViewById(R.id.endDate);
        final Button menuSelectBtn = (Button)findViewById(R.id.menuSelectBtn);

        startDateBtn.setText(Data.onlyDateFormat.format(date));
        endDateBtn.setText(Data.onlyDateFormat.format(date));

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDate.set(year, monthOfYear, dayOfMonth);
                                startDateBtn.setText(Data.onlyDateFormat.format(startDate.getTime()));
                            }
                        },
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DATE)
                ).show();
            }
        });
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endDate.set(year, monthOfYear, dayOfMonth);
                                endDateBtn.setText(Data.onlyDateFormat.format(endDate.getTime()));
                            }
                        },
                        endDate.get(Calendar.YEAR),
                        endDate.get(Calendar.MONTH),
                        endDate.get(Calendar.DATE)
                ).show();
            }
        });
        menuSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] menuNames = new String[menus.size()];
                final Menu[] menuArr = new Menu[menus.size()];
                final boolean[] menuSelectArr = new boolean[menus.size()];
                int i = 0;
                for (Menu menu : menus.values()) {
                    menuNames[i] = menu.getName();
                    menuArr[i] = menu;
                    if (selectedMenus.contains(menu)) {
                        menuSelectArr[i] = true;
                    }
                    i++;
                }
                new AlertDialog.Builder(getContext(), R.style.Base_V21_Theme_AppCompat_Light_Dialog)
                        .setTitle("메뉴 선택")
                        .setMultiChoiceItems(menuNames, menuSelectArr, new OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    selectedMenus.add(menuArr[which]);
                                } else {
                                    selectedMenus.remove(menuArr[which]);
                                }
                            }
                        })
                        .setPositiveButton("확인", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedMenus.clear();
                            }
                        })
                        .setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (selectedMenus.size() == 0) {
                                    menuSelectBtn.setText("메뉴를 선택하세요.");
                                } else {
                                    String text = "";
                                    for (int i = selectedMenus.size() - 1; i > 0; i--) {
                                        text += selectedMenus.get(i).getName() + ", ";
                                    }
                                    text += selectedMenus.get(0).getName();
                                    menuSelectBtn.setText(text);
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.pay_cashChk:
                        cash = isChecked;
                        break;
                    case R.id.pay_cardChk:
                        card = isChecked;
                        break;
                    case R.id.pay_serviceChk:
                        service = isChecked;
                        break;
                    case R.id.pay_creditChk:
                        credit = isChecked;
                        break;
                }
            }
        };

        ((CheckBox)findViewById(R.id.pay_cashChk)).setOnCheckedChangeListener(listener);
        ((CheckBox)findViewById(R.id.pay_cardChk)).setOnCheckedChangeListener(listener);
        ((CheckBox)findViewById(R.id.pay_serviceChk)).setOnCheckedChangeListener(listener);
        ((CheckBox)findViewById(R.id.pay_creditChk)).setOnCheckedChangeListener(listener);

        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.confirmBtn) callbacks.applySearchResult(startDate, endDate, selectedMenus, cash, card, service, credit);
                dismiss();
            }
        };
        ((Button)findViewById(R.id.confirmBtn)).setOnClickListener(btnListener);
        ((Button)findViewById(R.id.cancelBtn)).setOnClickListener(btnListener);
    }

    public interface Callbacks {
        void applySearchResult(Calendar startDate, Calendar endDate, ArrayList<Menu> menus,
                               boolean cash, boolean card, boolean service, boolean credit);
    }
}