package org.jaram.ds.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class OrderListAdapter extends BaseAdapter {

    ArrayList<Order> orders = null;
    Context context;
    int currentSelected = -1;
    public OrderListAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Order getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orders.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Order order = orders.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, parent, false);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.ordermenuList = (GridLayout)convertView.findViewById(R.id.ordermenuList);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.date.setText(new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초", Locale.KOREA).format(order.getDate()));
        holder.price.setText(order.getTotalprice() + "원");
        holder.ordermenuList.removeAllViews();
        Log.d("orderlist adapter", order.toJson().toString());
        if (order.getOrdermenus().size() == 0 || order.getOrdermenus() == null) {
            order.setOrdermenus(Data.dbOrderMenu.getAll(order));
        }
        for (int i=0; i<order.getOrdermenus().size(); i++) {
            TextView ordermenuView = (TextView)((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.orderlist_menu_item, holder.ordermenuList, false);
            ordermenuView.setText(order.getOrdermenus().get(i).getMenu().getName());
            if (order.getOrdermenus().get(i).getPay() == Data.PAY_CREDIT) {
                ordermenuView.setBackgroundResource(R.color.accent);
            }
            holder.ordermenuList.addView(ordermenuView);
        }
        if (position == currentSelected) {
            convertView.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        }
        else {
            convertView.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
        return convertView;
    }

    public void setCurrentSelected(int position) {
        currentSelected = position;
    }

    private class ViewHolder {
        TextView date;
        TextView price;
        GridLayout ordermenuList;
    }
}
