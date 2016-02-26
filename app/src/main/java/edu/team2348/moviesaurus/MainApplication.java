package edu.team2348.moviesaurus;

import android.app.Application;

import com.parse.Parse;

/**
 * Class the represent the lifetime of the application.  Allows to be persistent throughout the
 * lifetime of the application.
 * @author Faizan Virani
 * @version 1.0
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
