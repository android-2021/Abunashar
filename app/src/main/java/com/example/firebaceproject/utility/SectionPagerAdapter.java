package com.example.firebaceproject.utility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.firebaceproject.Main_window.Friend_tab;
import com.example.firebaceproject.Main_window.Request_tab;

public class SectionPagerAdapter extends FragmentPagerAdapter {


    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Request_tab request_tab = new Request_tab();
                return request_tab;
            case 1:
                Friend_tab friend_tab = new Friend_tab();
                return friend_tab;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:return "Request";
            case 1:return "Friend";
            default:return null;

        }

    }
}
