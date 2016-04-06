package edu.team2348.moviesaurus;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Home/Main Activity of the application. It allows you to select form the available functionality
 * of the application
 * @author Faizan Virani
 * @author Thomas Lilly
 * @version 1.1
 */
public class MainActivity extends AppCompatActivity implements MovieFragment.OnListFragmentInteractionListener {

    /**
     * Tag to identify MainActivity in Logger
     */
    private static final String TAG = "MainActivity";
    /**
     * Intent to create activity to view profile
     */
    private Intent viewUserProfileIntent;
    /**
     * Intent to take user back to SignIn when logging out
     */
    private Intent signInIntent;
    /**
     * Intent to start the administrator view of the app
     */
    private Intent adminIntent;
    /**
     * The container for fragments in the TabLayout
     */
    private ViewPager viewPager;
    /**
     * The filter button that allows you to filter ratings by major
     */
    private MenuItem filter;
    /**
     * The titles of the each tab in order
     */
    private CharSequence[] titles = {"New Releases", "Recommendations", "DVD Releases"};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // Setting up the TabLayout
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this, R.drawable.ic_whatshot_24dp)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this, R.drawable.ic_trending_up_24dp)));
        tabLayout.addTab(tabLayout.newTab().setIcon(ContextCompat.getDrawable(this, R.drawable.ic_disc_full_24dp)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new SwipeAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabInteractionListener());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titles[0]);
        }
        signInIntent = new Intent(this, SigninActivity.class);
        viewUserProfileIntent = new Intent(this, UserProfileActivity.class);
        adminIntent = new Intent(this, AdminActivity.class);
    }

    private class TabInteractionListener implements TabLayout.OnTabSelectedListener {


        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            final int full = 255;
            viewPager.setCurrentItem(tab.getPosition());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(titles[tab.getPosition()]);
            }
            if (tab.getPosition() == 1 && filter != null) {
                filter.setEnabled(true);
                filter.getIcon().setAlpha(full);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            final int dim = 130;
            if (tab.getPosition() == 1 && filter != null) {
                filter.setEnabled(false);
                filter.getIcon().setAlpha(dim);
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final int dim = 130;
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);
        filter = menu.getItem(1);
        filter.setEnabled(false);
        filter.getIcon().setAlpha(dim);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MovieSearchActivity.class)));
        searchView.setIconifiedByDefault(false);
        final MenuItem adminControl = menu.getItem(3);
        if (!ParseUser.getCurrentUser().getBoolean("admin")) {
            adminControl.setEnabled(false);
            adminControl.setVisible(false);
        }

        final MenuItem menuItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //get focus
                item.getActionView().requestFocus();
                //get input method
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_profile:
                startActivity(viewUserProfileIntent);
                return true;
            case R.id.logout:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            //displays Parse Error in a toast
                            Log.e(TAG, e.getMessage());
                            final Context context = getApplicationContext();
                            final CharSequence text = e.getMessage();
                            final int duration = Toast.LENGTH_SHORT;
                            final Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
                startActivity(signInIntent);
                finish();
                return true;
            case R.id.admin_options:
                startActivity(adminIntent);
                return true;
            case R.id.filter:
                final ArrayList<String> mSelectedItems = new ArrayList<>();  // Where we track the selected items
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Set the dialog title
                final String[] preList = getResources().getStringArray(R.array.major_list);
                Arrays.sort(preList);
                final String[] list = preList;
                builder.setTitle("Filter")
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setMultiChoiceItems(list, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        if (isChecked) {
                                            // If the user checked the item, add it to the selected items
                                            mSelectedItems.add(list[which]);
                                        } else if (mSelectedItems.contains(list[which])) {
                                            // Else, if the item is already in the array, remove it
                                            mSelectedItems.remove(list[which]);
                                        }
                                    }
                                })
                                // Set the action buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                final SwipeAdapter swipeAdapter = (SwipeAdapter) viewPager.getAdapter();
                                final MovieFragment mf = (MovieFragment) swipeAdapter.getRegisteredFragment(viewPager.getCurrentItem());
                                mf.filterOut(mSelectedItems);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.create();
                final AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
        final Intent movieDetail = new Intent(this, MovieDetailActivity.class);
        movieDetail.putExtra("title", item.getTitle());
        movieDetail.putExtra("poster", item.getPoster());
        movieDetail.putExtra("rating", item.getRating());
        startActivity(movieDetail);

    }
}
