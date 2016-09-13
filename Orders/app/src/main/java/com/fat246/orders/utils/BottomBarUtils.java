package com.fat246.orders.utils;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.fat246.orders.R;
import com.fat246.orders.fragment.AllApplysFragment;
import com.fat246.orders.fragment.AllOrdersFragment;

public class BottomBarUtils {

    //Fragments
    public static Fragment[] mFragments = new Fragment[5];

    @Nullable
    public static Fragment getFragmentByMenuItemId(int menuItemId) {

        switch (menuItemId) {

            case R.id.tab_all_orders:

                return getFragmentInstanceByIndex(0);

            case R.id.tab_orders:

                return getFragmentInstanceByIndex(1);

            case R.id.tab_all_applys:

                return getFragmentInstanceByIndex(2);

            case R.id.tab_applys:

                return getFragmentInstanceByIndex(3);
        }

        return null;
    }

    private static Fragment getFragmentInstanceByIndex(int index) {

        if (mFragments[index] == null) {

            switch (index) {

                case 0:
                    mFragments[index] = AllOrdersFragment.newInstance(true);
                    break;

                case 1:
                    mFragments[index] = AllOrdersFragment.newInstance(false);
                    break;

                case 2:
                    mFragments[index] = AllApplysFragment.newInstance(true);
                    break;

                case 3:
                    mFragments[index] = AllApplysFragment.newInstance(false);
                    break;

                default:
                    break;
            }

        }

        return mFragments[index];
    }
}
