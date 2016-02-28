package edu.team2348.moviesaurus;

/**
 * This adapter allows for the selection of which tabs show which movies
 * @author Thomas
 * @version 1.0
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
public class SwipeAdapter extends FragmentStatePagerAdapter{
    int mNumOfTabs;

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
                return MovieFragment.newInstance(1, url);
            case 1:
                url = "http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/new_releases.json?apikey=yedukp76ffytfuy24zsqk7f5";
                return MovieFragment.newInstance(1, url);
            case 2:
                url = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?apikey=yedukp76ffytfuy24zsqk7f5";
                return MovieFragment.newInstance(1, url);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
