package org.jaram.ds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.data.struct.Menu;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by kjydiary on 15. 9. 20..
 */
public class MenuListAdapter extends BaseAdapter {

//    ArrayList<Menu> menus = null;
    RealmList<Menu> menus = null;
    MenuClickListener listener = null;
//    public MenuListAdapter(ArrayList<Menu> menus, MenuClickListener listener) {
    public MenuListAdapter(RealmList<Menu> menus, MenuClickListener listener) {
        this.menus = menus;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Menu getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return menus.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int i = position;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundResource(R.color.point);
                    }
                    else {
                        v.setBackgroundResource(R.color.dark);
                    }
                    return false;
                }
            });
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.name.setText(menus.get(position).getName());
        holder.price.setText(menus.get(position).getPrice()+"원");
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(menus.get(i));
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView price;
    }

    public interface MenuClickListener {
        void onClick(Menu menu);
    }
}
