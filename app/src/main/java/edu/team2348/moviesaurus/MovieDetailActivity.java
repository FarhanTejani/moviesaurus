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
    private static final String TAG = "MovieDetailActivity";

    private AppCompatRatingBar ratingBar;
    private String poster;
    private CharSequence title;
    private float myRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poster = getIntent().getStringExtra("poster");
        title = getIntent().getCharSequenceExtra("title");

        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        Picasso.with(this).load(poster).resize(330, 450)
                .into((AppCompatImageView) findViewById(R.id.big_movie_poster));
        Button rateButton = (Button) findViewById(R.id.rate_button);
        ratingBar = (AppCompatRatingBar) findViewById(R.id.ratingBar_detail);
        myRating = getIntent().getFloatExtra("rating", 0);
        ratingBar.setRating(myRating);

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
                LayoutInflater inflater = MovieDetailActivity.this.getLayoutInflater();
                builder.setTitle("Movie Rating");
                builder.setView(inflater.inflate(R.layout.rating_dialog, null));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ratingBar.setRating(myRating);
                        ParseQuery<Movie> query = ParseQuery.getQuery(Movie.class);
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
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                AppCompatRatingBar bar = (AppCompatRatingBar) dialog.findViewById(R.id.dialog_rating);
                bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        myRating = rating;

                    }
                });
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

    }

}
