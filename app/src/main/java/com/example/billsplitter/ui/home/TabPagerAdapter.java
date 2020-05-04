package com.example.billsplitter.ui.home;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

//class for tab pager adapter
public class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FriendsTab();
                case 1:
                    return new GroupsTab();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
}
