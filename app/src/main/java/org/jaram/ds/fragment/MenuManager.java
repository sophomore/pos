package org.jaram.ds.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import org.jaram.ds.util.Http;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class MenuManager extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_manager, container, false);

        final ListView cutletView = (ListView)view.findViewById(R.id.cutlet_list);
        final ListView riceView = (ListView)view.findViewById(R.id.rice_list);
        final ListView noodleView = (ListView)view.findViewById(R.id.noodle_list);
        final ListView etcView = (ListView)view.findViewById(R.id.etc_list);

        MenuListAdapter.MenuClickListener menuListener = new MenuListAdapter.MenuClickListener() {
            @Override
            public void onClick(Menu menu) {
                InfoMenu dialog = new InfoMenu(getActivity(), menu);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((MenuListAdapter)cutletView.getAdapter()).notifyDataSetChanged();
                        ((MenuListAdapter)riceView.getAdapter()).notifyDataSetChanged();
                        ((MenuListAdapter)noodleView.getAdapter()).notifyDataSetChanged();
                        ((MenuListAdapter)etcView.getAdapter()).notifyDataSetChanged();
                    }
                });
                dialog.show();
            }

            @Override
            public void onLongClick(final Menu menu) {
                final int id = menu.getId();
                new AlertDialog.Builder(getActivity(), R.style.Base_V21_Theme_AppCompat_Light_Dialog)
                        .setTitle("확인")
                        .setMessage("'"+menu.getName()+"'을/를 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface alertDialog, int which) {
                                //TODO: remove at db and server
                                new AsyncTask<Void, Void, Void>() {

                                    ProgressDialog dialog;

                                    @Override
                                    protected void onPreExecute() {
                                        dialog = new ProgressDialog(getActivity());
                                        dialog.setMessage("삭제하는 중입니다.");
                                        dialog.show();
                                    }

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        Http.delete(Data.SERVER_URL + "menu/" + id, null);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {
                                        menu.destroy();
                                        dialog.dismiss();
                                        alertDialog.dismiss();
                                        ((MenuListAdapter)cutletView.getAdapter()).notifyDataSetChanged();
                                        ((MenuListAdapter)riceView.getAdapter()).notifyDataSetChanged();
                                        ((MenuListAdapter)noodleView.getAdapter()).notifyDataSetChanged();
                                        ((MenuListAdapter)etcView.getAdapter()).notifyDataSetChanged();
                                    }
                                }.execute();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
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
                InfoMenu dialog = new InfoMenu(getActivity(), null);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((MenuListAdapter)cutletView.getAdapter()).notifyDataSetChanged();
                        ((MenuListAdapter)riceView.getAdapter()).notifyDataSetChanged();
                        ((MenuListAdapter)noodleView.getAdapter()).notifyDataSetChanged();
                        ((MenuListAdapter)etcView.getAdapter()).notifyDataSetChanged();
                    }
                });
                dialog.show();
            }
        });

        return view;
    }
}