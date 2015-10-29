package org.jaram.ds.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import org.jaram.ds.util.Http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class OrderDetailMenuAdapter extends BaseAdapter {

    ArrayList<OrderMenu> ordermenus = null;
    Context context = null;
    public OrderDetailMenuAdapter(ArrayList<OrderMenu> ordermenus, Context context) {
        this.ordermenus = ordermenus;
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
            holder.addTakeout = (TextView) convertView.findViewById(R.id.addTakeout);
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
        holder.addTakeout.setVisibility(ordermenu.isTakeout() ? View.VISIBLE : View.GONE);
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
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                new AsyncTask<Void, Void, Void>() {
                    ProgressDialog dialog;
                    @Override
                    protected void onPreExecute() {
                        dialog = new ProgressDialog(context);
                        dialog.setMessage("정보를 수정하고 있습니다");
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        HashMap<String, Object> param = new HashMap<String, Object>();
                        param.put("pay", position + 1);
                        try {
                            Http.post(Data.SERVER_URL+"order/menu/"+ordermenu.getId(), param);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        ordermenu.setPay(position+1);
                        OrderDetailMenuAdapter.this.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView price;
        TextView addCurry;
        TextView addTwice;
        TextView addTakeout;
        Spinner paySelector;
    }
}
