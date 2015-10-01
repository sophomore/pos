package org.jaram.ds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.jaram.ds.fragment.MenuManager;
import org.jaram.ds.fragment.OrderManager;
import org.jaram.ds.fragment.Statistic;

/**
 * Created by kjydiary on 15. 9. 23..
 */
public class Admin extends Base {

    FragmentManager fm;

    int current;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: 마감 작업

        Intent intent = getIntent();
        current = intent.getIntExtra("view", Base.MANAGE_ORDER);

        OrderManager orderManager = new OrderManager();
        MenuManager menuManager = new MenuManager();
        Statistic statistic = new Statistic();

        fm = getSupportFragmentManager();

        if (current == Base.STATISTIC) {
            replace(statistic);
        }
        else if (current == Base.MANAGE_MENU) {
            replace(menuManager);
        }
        else {
            replace(orderManager);
        }
    }

    private void replace(Fragment view) {
        fm.beginTransaction().replace(R.id.main_view, view).commit();
    }

    @Override
    int getCurrent() {
        return current;
    }
}
