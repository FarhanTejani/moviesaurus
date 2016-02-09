package edu.team2348.moviesaurus;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by faizanv on 2/8/16.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
