package com.mygamelogic.myplayer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.mygamelogic.myplayer.Fragments.MusicFragment;

import java.util.List;

/**
 * Created by admin on 16/07/18.
 */

public class FragmentPageAdapter extends FragmentStatePagerAdapter {

    private List<String> searchArray;
    public FragmentPageAdapter(FragmentManager fm, List<String> searchArray) {
        super(fm);
        this.searchArray = searchArray;
    }

    public void setDataLoaderNotifier(List<String> searchArray){
        this.searchArray=searchArray;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        MusicFragment fragment1 = new MusicFragment();
        fragment1.setSearcText(searchArray.get(position));
        return fragment1;
    }

    @Override
    public int getCount() {
        if(searchArray!=null){
            return searchArray.size();
        }
        return 0;
    }

}
