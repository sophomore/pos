package org.jaram.ds.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import org.jaram.ds.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kjydiary on 15. 9. 30..
 */
public class OrderSearch extends Dialog {

    public OrderSearch(Context context) {
        super(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_order);

        final Date date = new Date();

        final Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);

        final Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);

        final Button startDate = (Button)findViewById(R.id.startDate);
        final Button endDate = (Button)findViewById(R.id.endDate);
        final Button menuSelectBtn = (Button)findViewById(R.id.menuSelectBtn);

        startDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(date));
        endDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(date));

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startCalendar.set(year, monthOfYear, dayOfMonth);
                                startDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(startCalendar.getTime()));
                            }
                        },
                        startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DATE)
                ).show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endCalendar.set(year, monthOfYear, dayOfMonth);
                                endDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(endCalendar.getTime()));
                            }
                        },
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DATE)
                ).show();
            }
        });
        menuSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}