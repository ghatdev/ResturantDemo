package com.example.restaurantdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.restaurantdemo.fragment.SignInFragment;
import com.example.restaurantdemo.fragment.SignupFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new SignInFragment();
                break;
            case 1:
                fragment = new SignupFragment();

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Sign in";
                break;
            case 1:
                title = "Sign up";
                break;
        }
        return title;
    }
}
