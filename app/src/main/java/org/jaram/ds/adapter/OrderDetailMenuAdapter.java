package org.jaram.ds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.OrderMenu;

import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class OrderDetailMenuAdapter extends BaseAdapter {

    ArrayList<OrderMenu> ordermenus = null;
    ArrayList<OrderMenu> modified = null;
    Context context = null;
    public OrderDetailMenuAdapter(ArrayList<OrderMenu> ordermenus, Context context) {
        this.ordermenus = ordermenus;
        this.modified = new ArrayList<OrderMenu>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return ordermenus.size();
    }

    @Override
    public OrderMenu getItem(int position) {
        return ordermenus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ordermenus.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OrderMenu ordermenu = ordermenus.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_menu_item, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.addCurry = (TextView) convertView.findViewById(R.id.addCurry);
            holder.addTwice = (TextView) convertView.findViewById(R.id.addTwice);
            holder.paySelector = (Spinner) convertView.findViewById(R.id.paySelector);
            holder.paySelector.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, new String[]{"현금", "카드", "서비스", "외상"}));
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.name.setText(ordermenu.getMenu().getName());
        holder.price.setText(ordermenu.getTotalprice()+"원");
        holder.addCurry.setVisibility(ordermenu.isCurry() ? View.VISIBLE : View.GONE);
        holder.addTwice.setVisibility(ordermenu.isTwice() ? View.VISIBLE : View.GONE);
        switch(ordermenu.getPay()) {
            case Data.PAY_CASH:
                holder.paySelector.setSelection(0);
                convertView.setBackgroundResource(android.R.color.white);
                break;
            case Data.PAY_CARD:
                holder.paySelector.setSelection(1);
                convertView.setBackgroundResource(android.R.color.white);
                break;
            case Data.PAY_SERVICE:
                holder.paySelector.setSelection(2);
                convertView.setBackgroundResource(android.R.color.white);
                break;
            case Data.PAY_CREDIT:
                holder.paySelector.setSelection(3);
                convertView.setBackgroundResource(R.color.accent);
                break;
        }
        holder.paySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modified.add(ordermenu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return convertView;
    }

    public ArrayList<OrderMenu> getModifyList() {
        return modified;
    }

    private class ViewHolder {
        TextView name;
        TextView price;
        TextView addCurry;
        TextView addTwice;
        Spinner paySelector;
    }
}
