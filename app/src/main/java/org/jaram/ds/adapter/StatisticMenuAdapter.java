package org.jaram.ds.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.jaram.ds.R;
import org.jaram.ds.data.struct.Menu;

import java.util.ArrayList;

/**
 * Created by kjydiary on 15. 10. 7..
 */
public class StatisticMenuAdapter extends BaseAdapter {

    ArrayList<Menu> menuList;
    ArrayList<Menu> selectedList;
    public StatisticMenuAdapter(ArrayList<Menu> menuList) {
        this.menuList = menuList;
        this.selectedList = new ArrayList<>();
    }

    public ArrayList<Menu> getSelectedList() {
        return selectedList;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Menu getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return menuList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_menu_item, parent, false);
        }
        ((Button)convertView).setText(menuList.get(position).getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.contains(menuList.get(position))) {
                    selectedList.remove(menuList.get(position));
                    v.setBackgroundResource(R.drawable.white_btn);
                    ((Button)v).setTextColor(Color.parseColor("#3E454C"));
                } else {
                    selectedList.add(menuList.get(position));
                    v.setBackgroundResource(R.drawable.point_btn);
                    ((Button)v).setTextColor(Color.WHITE);
                }
            }
        });
        return convertView;
    }
}
