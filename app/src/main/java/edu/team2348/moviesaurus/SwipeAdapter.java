package edu.team2348.moviesaurus;

/**
 * This adapter allows for the selection of which tabs show which movies
 * @author Thomas Lilly
 * @version 1.0
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class SwipeAdapter extends FragmentStatePagerAdapter{
//    private static final String TAG = "SwipeAdapter";

    private int mNumOfTabs;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public SwipeAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {
        String url;
        switch (position) {
            case 0:
                url = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=yedukp76ffytfuy24zsqk7f5";
                return MovieFragment.newInstance(url, null);
            case 1:
                url = "recommendation";
                return MovieFragment.newInstance(url, "rating");
            case 2:
                url = "http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/new_releases.json?apikey=yedukp76ffytfuy24zsqk7f5";
                return MovieFragment.newInstance(url, null);
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }



    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
