package com.bubblemusic.appchee.bubblemusic.page;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.bubblemusic.appchee.bubblemusic.MainActivity;
import com.bubblemusic.appchee.bubblemusic.SendClassifiy;

public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Context context;

    public PageAdapter(FragmentManager fm,int NumOfTabs, Context c) {
        super(fm);
        this.mNumOfTabs=NumOfTabs;
        this.context=c;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyListFragment tab0=new MyListFragment();
                ((MainActivity)context).myListClassify();
                Log.d("page","mylist");
                return tab0;
            case 1:
                MainFragment tab1 = new MainFragment();
                return tab1;
            case 2:
                AlbumFragment tab2 = new AlbumFragment();
                ((MainActivity)context).albumClassify();
                Log.d("page","album");
                return tab2;
            case 3:
                ArtistFragment tab3 = new ArtistFragment();
                ((MainActivity)context).artistClassify();
                Log.d("page","artist");
                return tab3;
            case 4:
                GenreFragment tab4 = new GenreFragment();
                ((MainActivity)context).genreClassify();
                Log.d("page","genre");
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
