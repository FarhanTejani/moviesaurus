package edu.team2348.moviesaurus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    Intent intent;
    TextView userWelcome;
    Button searchMoviesButton;
    Intent intentSearch;
    Intent newReleasesIntent;
    Intent newDVDReleasesIntent;
    Intent viewUserProfileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = new Intent(this, SigninActivity.class);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a snackbar for logging out
                Snackbar.make(view, "Are you sure you want to logout?", Snackbar.LENGTH_LONG)
                        .setAction("YES", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ParseUser.logOutInBackground(new LogOutCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            startActivity(intent);
                                        } else {
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
                                startActivity(intent);
                            }
                        }).show();
            }
        });

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
}
