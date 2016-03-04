package edu.team2348.moviesaurus;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    AppCompatRatingBar ratingBar;
    float myRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
        Picasso.with(this).load(getIntent().getStringExtra("poster")).resize(330, 450)
                .into((AppCompatImageView) findViewById(R.id.big_movie_poster));
        Button rateButton = (Button) findViewById(R.id.rate_button);
        ratingBar = (AppCompatRatingBar) findViewById(R.id.ratingBar_detail);
        myRating = 0;

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
            getSupportActionBar().setTitle(getIntent().getCharSequenceExtra("title"));
        }



    }

}
