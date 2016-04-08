package edu.team2348.moviesaurus;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Activity to show the details of the selected Movie
 * @author Thomas
 * @version 1.0
 */
public class MovieDetailActivity extends AppCompatActivity {

    /**
     * The String TAG for the Logger if there is an error or exception
     */
    private static final String TAG = "MovieDetailActivity";

    /**
     * The rating bar of the movie being shown
     */
    private AppCompatRatingBar ratingBar;

    /**
     * The url of the poster image for the movie being shown
     */
    private String poster;

    /**
     * The title of the movie
     */
    private CharSequence title;

    /**
     * The rating of the movie
     */
    private float myRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poster = getIntent().getStringExtra("poster");
        title = getIntent().getCharSequenceExtra("title");

        final int width = 330;
        final int height = 450;
        setContentView(R.layout.activity_movie_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        Picasso.with(this).load(poster).resize(width, height)
                .into((AppCompatImageView) findViewById(R.id.big_movie_poster));
        final Button rateButton = (Button) findViewById(R.id.rate_button);
        ratingBar = (AppCompatRatingBar) findViewById(R.id.ratingBar_detail);
        myRating = getIntent().getFloatExtra("rating", 0);
        ratingBar.setRating(myRating);

        rateButton.setOnClickListener( new RatingClickListener());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

    }

    private class RatingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
            final LayoutInflater inflater = MovieDetailActivity.this.getLayoutInflater();
            builder.setTitle("Movie Rating");
            builder.setView(inflater.inflate(R.layout.rating_dialog, null));

            builder.setPositiveButton("OK", new PositiveClickListener());
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            final AppCompatRatingBar bar = (AppCompatRatingBar) dialog.findViewById(R.id.dialog_rating);
            bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar pRatingBar, float rating, boolean fromUser) {
                    myRating = rating;

                }
            });
        }
    }

    /**
     * Class to handle when a user clicks the OK button in the dialog
     */
    private class PositiveClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int id) {
            ratingBar.setRating(myRating);
            final ParseQuery<Movie> query = ParseQuery.getQuery(Movie.class);
            query.whereEqualTo("title", title.toString())
                    .whereEqualTo("poster", poster)
                    .findInBackground(new FindCallback<Movie>() {
                        @Override
                        public void done(List<Movie> objects, ParseException e) {
                            if (e == null) {
                                objects.get(0).restoreRatings();
                                objects.get(0).addRating(ParseUser.getCurrentUser().getObjectId(), myRating);
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    });
            dialog.dismiss();
        }
    }

}
