package edu.team2348.moviesaurus;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

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
        ParseObject.registerSubclass(Movie.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParsePush.subscribeInBackground("og", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
        ParseInstallation.getCurrentInstallation().saveInBackground();
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

    }
}
