package org.jaram.ds.activities;

import org.jaram.ds.fragment.MenuManageFragment;

/**
 * Created by jdekim43 on 2016. 5. 13..
 */
public class MenuManageActivity extends BaseActivity<MenuManageFragment> {

    @Override
    protected MenuManageFragment createFragment() {
        return MenuManageFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return "메뉴 관리";
    }
}
