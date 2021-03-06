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

class SwipeAdapter extends FragmentStatePagerAdapter{
//    private static final String TAG = "SwipeAdapter";
    /**
     * The number of tabs to be accessible by the SwipeAdapter
     */
    private final int mNumOfTabs;
    /**
     * The SparseArray to keep references to the fragments currently made
     */
    private final SparseArray<Fragment> registeredFragments = new SparseArray<>();

    /**
     * Constructor for the SwipeAdapter with parameters
     * @param fm the fragment manager
     * @param numOfTabs number of tabs being used
     */
    public SwipeAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;

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

    /**
     * Gets the reference to the fragment that are currently created given a position
     * @param position the index of the tab
     * @return the fragment reference at the position requested
     */
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }



    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
