package org.jaram.ds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

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
                replace(Statistic.getInstance());
                break;
            case Base.TAX:
                addSwapBtn();
                replace(Tax.getInstance());
                break;
            case Base.MANAGE_MENU:
                replace(MenuManager.getInstance());
                break;
            case Base.MANAGE_ORDER:
                replace(OrderManager.getInstance());
                break;
        }
        current = viewId;
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
}
