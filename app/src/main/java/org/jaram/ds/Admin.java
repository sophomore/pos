package org.jaram.ds;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jaram.ds.data.Closing;
import org.jaram.ds.fragment.MenuManager;
import org.jaram.ds.fragment.OrderManager;
import org.jaram.ds.fragment.Statistic;
import org.jaram.ds.fragment.Tax;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class Admin extends Base implements Statistic.Callbacks, Tax.Callbacks {

    FragmentManager fm;

    int current;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: 마감 작업

        Intent intent = getIntent();
        current = intent.getIntExtra("view", Base.MANAGE_ORDER);

        fm = getSupportFragmentManager();

        replace(current);
    }

    public void replace(int viewId) {
        resetLeftDrawer();
        switch(viewId) {
            case Base.STATISTIC:
                addSwapBtn();
                closingAfterReplace(Statistic.getInstance());
                break;
            case Base.TAX:
                addSwapBtn();
                closingAfterReplace(Tax.getInstance());
                break;
            case Base.MANAGE_MENU:
                closingAfterReplace(MenuManager.getInstance());
                break;
            case Base.MANAGE_ORDER:
                replace(OrderManager.getInstance());
                break;
        }
        current = viewId;
        super.doneAttatch();
    }

    public void closingAfterReplace(final Fragment view) {
        Log.d("admin", "do closing");
        new Closing(Admin.this, new Closing.Listener() {
            @Override
            public void endClosing(boolean isSuccess) {
                Log.d("admin", "done closing");
                if (isSuccess) replace(view);
                else {
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("오류")
                            .setMessage("오류가 발생했습니다. 앱을 종료합니다.")
                            .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }
        }, new ProgressDialog(Admin.this));
    }

    private void replace(Fragment view) {
        FragmentTransaction ft = fm.beginTransaction();
        if (fm.getFragments() != null) {
            for (int i=0; i<fm.getFragments().size(); i++) {
                ft.remove(fm.getFragments().get(i));
            }
        }
        for (int i=2; i<actionbar.getChildCount(); i++) {
            actionbar.removeViewAt(i);
        }
        ft.replace(R.id.main_view, view).commit();
        System.gc();
    }

    private boolean isAlreadyAdded = false;
    public void addSwapBtn() {
        if (!isAlreadyAdded) {
            ImageButton swapBtn = new ImageButton(Admin.this);
            swapBtn.setImageResource(R.drawable.ic_swap_horiz_white_36dp);
            swapBtn.setBackgroundResource(R.drawable.default_btn);
            swapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current == Base.STATISTIC) {
//                    startActivity(new Intent(Admin.this, Admin.class).putExtra("view", Base.TAX));
                        replace(Base.TAX);
                    } else if (current == Base.TAX) {
//                    startActivity(new Intent(Admin.this, Admin.class).putExtra("view", Base.STATISTIC));
                        replace(Base.STATISTIC);
                    }
//                finish();
                }
            });
            addButtonAtActionBar(swapBtn);
            isAlreadyAdded = true;
        }
    }

    @Override
    int getCurrent() {
        return current;
    }

    @Override
    public void setDrawer(FrameLayout drawer) {
        super.setLeftDrawer(drawer);
    }

    @Override
    public void addViewAtActionBar(View view, ViewGroup.LayoutParams params) {
        super.addViewAtActionBar(view, params);
    }

    @Override
    public void closeLeftDrawer() {
        base_container.closeDrawer(Gravity.LEFT);
    }

    boolean isExitProgress = false;
    @Override
    public void onBackPressed() {
        if (isExitProgress) {
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "앱을 종료하시려면 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show();
            isExitProgress = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isExitProgress = false;
                }
            }, 1500);
        }
    }
}
