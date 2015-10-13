package org.jaram.ds.data;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jaram.ds.R;
import org.jaram.ds.data.struct.Order;
import org.jaram.ds.util.Http;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kjydiary on 15. 10. 13..
 */
public class Closing {

    public static final int VIEW_TOAST = 0;
    public static final int VIEW_TEXTVIEW = 1;
    public static final int VIEW_PROGRESSDIALOG = 2;

    private Context context;
    private Listener listener;

    private int notice_viewType;
    private View notice_view;
    private Dialog notice_dialog;
    public Closing(Context context, Listener listener) {
        this(context, listener, null, VIEW_TOAST);
    }

    public Closing(Context context, Listener listener, Dialog dialog) {
        this.context = context;
        this.listener = listener;
        this.notice_dialog = dialog;
        this.notice_viewType = VIEW_PROGRESSDIALOG;
        doClose();
    }

    public Closing(Context context, Listener listener, View view, int viewType) {
        this.context = context;
        this.listener = listener;
        this.notice_view = view;
        this.notice_viewType = viewType;
        doClose();
    }

    private void notice(String text) {
        switch(notice_viewType) {
            default:
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                break;
            case VIEW_TEXTVIEW:
                ((TextView)notice_view).setText(text);
                break;
            case VIEW_PROGRESSDIALOG:
                if (!notice_dialog.isShowing()) notice_dialog.show();
                ((ProgressDialog)notice_dialog).setMessage(text);
        }
    }

    private void doClose() {
        notice("마감 작업을 준비중입니다.");
        new SendOrder().execute();
    }

    private class SendOrder extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            notice("서버에 로컬에 저장된 주문 정보를 전송하고 있습니다.");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<Order> queryResult = Data.dbOrder.getAll();
            Log.d("closing1", queryResult.toString());
            boolean isAllSuccess = true;
            for (int i=0; i<queryResult.size(); i++) {
                org.jaram.ds.data.struct.Order order = queryResult.get(i);
                order.setOrdermenus(Data.dbOrderMenu.getAll(order));
                HashMap<String, Object> param = new HashMap<>();
                param.put("time", Data.dateFormat.format(order.getDate()));
                param.put("totalprice", order.getTotalprice());
                Log.d("closing2", order.getOrdermenusAtJson().toString());
                param.put("ordermenus", order.getOrdermenusAtJson());
                boolean isSuccess = true;
                try {
                    int count = 0;
                    JSONObject result = null;
                    do {
                        result = new JSONObject(Http.post(Data.SERVER_URL + "order", param));
                        count++;
                        if (count > 5) {
                            isSuccess = false;
                            break;
                        }
                    } while(result.getString("result").equals("error"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess) {
                    order.deleteDB();
                }
                else {
                    isAllSuccess = false;
                }
            }
            return isAllSuccess;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                notice("서버에 데이터를 전송하는데 성공했습니다.");
                listener.endClosing(true);
                if (notice_dialog.isShowing()) notice_dialog.dismiss();
                return;
            }
            new AlertDialog.Builder(context, R.style.Base_V21_Theme_AppCompat_Light_Dialog)
                    .setTitle("오류")
                    .setMessage("서버에 주문 정보를 전송하는 도중 오류가 발생했습니다.\n재시도 하시겠습니까?")
                    .setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new SendOrder().execute();
                        }
                    })
                    .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.endClosing(false);
                            dialog.dismiss();
                            if (!notice_dialog.isShowing()) notice_dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    public interface Listener {
        void endClosing(boolean isSuccess);
    }
}
