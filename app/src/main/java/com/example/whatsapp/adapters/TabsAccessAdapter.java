package com.example.whatsapp.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.whatsapp.ChatsFragment;
import com.example.whatsapp.ContactsFragment;
import com.example.whatsapp.GroupsFragment;

public class TabsAccessAdapter extends FragmentPagerAdapter {

    public TabsAccessAdapter(FragmentManager fm) {
        super(fm);
    }


    // return current fraagment
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            default:
                return null;
        }
    }


    // return total page count
    @Override
    public int getCount() {
        return 3;
    }


    // return current pagetitile
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            default:
                return null;
        }
    }
}
