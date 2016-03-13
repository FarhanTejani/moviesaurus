package edu.team2348.moviesaurus;


import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import edu.team2348.moviesaurus.MovieFragment.OnListFragmentInteractionListener;



import java.util.List;


public class MyMovieRecyclerViewAdapter extends RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder> {

    private List<Movie> movies;
    private final OnListFragmentInteractionListener mListener;
    private final String TAG = getClass().getSimpleName();

    public MyMovieRecyclerViewAdapter(List<Movie> movies, OnListFragmentInteractionListener listener) {
        this.movies = movies;
        mListener = listener;

    }

//    public void setMovies(List<Movie> moviesList) {
//        movies = moviesList;
//    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (movies.get(position) != null) {
            holder.mContentView.setText(movies.get(position).getTitle());
            holder.rating.setRating(movies.get(position).getRating());
            Picasso.with(holder.poster.getContext()).load(movies.get(position).getPoster()).resize(240, 350).into(holder.poster);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.

                    mListener.onListFragmentInteraction(holder);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final View mView;
        public final AppCompatTextView mContentView;
        public final AppCompatImageView poster;
        public final AppCompatRatingBar rating;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            rating = (AppCompatRatingBar) view.findViewById(R.id.rating);
            mContentView = (AppCompatTextView) view.findViewById(R.id.content);
            poster = (AppCompatImageView) view.findViewById(R.id.movie_poster);
        }
        /**
         * Returns the url of the picture for the position
         * @param position the position of the item in the RecyclerView
         * @return String for the URL of the for the item number
         */
        String getPicUrl(int position) {
            return movies.get(position).getPoster();
        }


        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

    }
}
