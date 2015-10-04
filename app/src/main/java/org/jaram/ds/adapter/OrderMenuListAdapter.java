package org.jaram.ds.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.OrderMenu;

import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class OrderMenuListAdapter extends BaseAdapter {

    ArrayList<OrderMenu> ordermenus = null;
    ArrayList<OrderMenu> selectedMenus = null;
    OrderMenuListener listener = null;
    public OrderMenuListAdapter(OrderMenuListener listener) {
        this.listener = listener;
        this.selectedMenus = new ArrayList<OrderMenu>();
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordermenulist_item, parent, false);
            holder.item = (RelativeLayout) convertView.findViewById(R.id.item_container);
            holder.name = (TextView) convertView.findViewById(R.id.menu_name);
            holder.price = (TextView) convertView.findViewById(R.id.menu_price);
            holder.curry = (Button) convertView.findViewById(R.id.curryBtn);
            holder.twice = (Button) convertView.findViewById(R.id.twiceBtn);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.name.setText(ordermenu.getMenu().getName());
        holder.price.setText(ordermenu.getTotalprice() + "원");
        if (ordermenu.getPay() != Data.PAY_CREDIT) {
            holder.item.setAlpha(0.6f);
            holder.curry.setVisibility(View.GONE);
            holder.twice.setVisibility(View.GONE);
        }
        else {
            holder.item.setAlpha(1.0f);
            holder.curry.setVisibility(View.VISIBLE);
            holder.twice.setVisibility(View.VISIBLE);
        }
        if (ordermenu.isCurry()) {
            holder.curry.setSelected(true);
            holder.curry.setTextColor(Color.WHITE);
        }
        else {
            holder.curry.setSelected(false);
            holder.curry.setTextColor(Color.parseColor("#2185C5"));
        }
        if (ordermenu.isTwice()) {
            holder.twice.setSelected(true);
            holder.twice.setTextColor(Color.WHITE);
        }
        else {
            holder.twice.setSelected(false);
            holder.twice.setTextColor(Color.parseColor("#2185C5"));
        }
        holder.curry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.curryClicked(ordermenu, (Button) v);
            }
        });
        holder.twice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.twiceClicked(ordermenu, (Button) v);
            }
        });
        if (selectedMenus.contains(ordermenu)) {
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            holder.item.setBackgroundResource(R.color.point);
        }
        else {
            holder.name.setTextColor(Color.parseColor("#3E454C"));
            holder.item.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        return convertView;
    }

    public ArrayList<OrderMenu> getSelectedMenus() {
        return selectedMenus;
    }

    public boolean isSelected(OrderMenu ordermenu) {
        return selectedMenus.contains(ordermenu);
    }

    public int getSelectedPrice() {
        int price = 0;
        for (int i=0; i<selectedMenus.size(); i++) {
            price += selectedMenus.get(i).getTotalprice();
        }
        return price;
    }

    public void addSelectedMenu(OrderMenu ordermenu) {
        selectedMenus.add(ordermenu);
    }

    public void removeSelectedMenu(OrderMenu ordermenu) {
        selectedMenus.remove(ordermenu);
    }

    public void resetSelectedMenu() {
        selectedMenus.clear();
    }

    public void setOrdermenus(ArrayList<OrderMenu> ordermenus) {
        this.ordermenus = ordermenus;
    }

    private class ViewHolder {
        RelativeLayout item;
        TextView name;
        TextView price;
        Button curry;
        Button twice;
    }

    public interface OrderMenuListener {
        void curryClicked(OrderMenu ordermenu, Button curryView);
        void twiceClicked(OrderMenu ordermenu, Button twiceView);
    }
}