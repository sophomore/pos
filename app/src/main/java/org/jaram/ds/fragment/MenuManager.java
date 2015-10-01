package org.jaram.ds.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import org.jaram.ds.R;
import org.jaram.ds.adapter.MenuListAdapter;
import org.jaram.ds.data.Data;
import org.jaram.ds.data.struct.Menu;
import org.jaram.ds.dialog.InfoMenu;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class MenuManager extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_manager, container, false);

        ListView cutletView = (ListView)view.findViewById(R.id.cutlet_list);
        ListView riceView = (ListView)view.findViewById(R.id.rice_list);
        ListView noodleView = (ListView)view.findViewById(R.id.noodle_list);
        ListView etcView = (ListView)view.findViewById(R.id.etc_list);

        MenuListAdapter.MenuClickListener menuListener = new MenuListAdapter.MenuClickListener() {
            @Override
            public void onClick(Menu menu) {
                //TODO: show information and modify;
                new InfoMenu(getActivity(), menu).show();
            }
        };

        cutletView.setAdapter(new MenuListAdapter(Data.categories.get(1).getMenus(), menuListener));
        riceView.setAdapter(new MenuListAdapter(Data.categories.get(2).getMenus(), menuListener));
        noodleView.setAdapter(new MenuListAdapter(Data.categories.get(3).getMenus(), menuListener));
        etcView.setAdapter(new MenuListAdapter(Data.categories.get(4).getMenus(), menuListener));

        ImageButton addBtn = (ImageButton)view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InfoMenu(getActivity(), null).show();
            }
        });

        return view;
    }
}