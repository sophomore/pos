package org.jaram.ds;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by kjydiary on 15. 9. 17..
 */
public class Order extends Base {

    @Override
    int getCurrent() {
        return Base.ORDER;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        org.jaram.ds.fragment.Order orderView = new org.jaram.ds.fragment.Order();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.main_view, orderView)
                .commit();
    }
}