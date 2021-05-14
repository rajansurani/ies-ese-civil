package com.upscrks.iesesecivil.Adapter;

import com.upscrks.iesesecivil.Fragment.BooksFragment;
import com.upscrks.iesesecivil.Fragment.HomeFragment;
import com.upscrks.iesesecivil.Fragment.NotesFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomeViewPagerAdapter extends FragmentStateAdapter {

    public HomeViewPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new NotesFragment();
            case 2:
                return new BooksFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}