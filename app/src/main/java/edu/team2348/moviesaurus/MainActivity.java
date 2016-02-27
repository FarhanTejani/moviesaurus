package edu.team2348.moviesaurus;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Home/Main Activity of the application. It allows you to select form the available functionality
 * of the application
 * @author Faizan Virani
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    TextView userWelcome;
    Button searchMoviesButton;
    Intent intentSearch;
    Intent newReleasesIntent;
    Intent newDVDReleasesIntent;
    Intent viewUserProfileIntent;
    Intent signInIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();


        signInIntent = new Intent(this, SigninActivity.class);


        userWelcome = (TextView) findViewById(R.id.logged_in_user);
        userWelcome.setText("Welcome " + ParseUser.getCurrentUser().getUsername());
        intentSearch = new Intent(this, MovieSearchActivity.class);
        searchMoviesButton = (Button) findViewById(R.id.search_movies_button);
        searchMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentSearch);
            }
        });

        newReleasesIntent = new Intent(this, NewReleasesActivity.class);
        findViewById(R.id.new_releases_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(newReleasesIntent);
            }
        });

        newDVDReleasesIntent = new Intent(this, NewDVDReleasesActivity.class);
        findViewById(R.id.new_dvd_releases_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(newDVDReleasesIntent);
            }
        });

        viewUserProfileIntent = new Intent(this, UserProfileActivity.class);
        findViewById(R.id.view_user_profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(viewUserProfileIntent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MovieSearchActivity.class)));
        searchView.setIconifiedByDefault(false);

        MenuItem menuItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //get focus
                item.getActionView().requestFocus();
                //get input method
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                            Log.e("ParseError", e.getMessage());
                            Context context = getApplicationContext();
                            CharSequence text = e.getMessage();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
                startActivity(signInIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
