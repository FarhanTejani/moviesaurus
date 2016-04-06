package edu.team2348.moviesaurus;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Activity that allows the user to search for a movie they desire
 * @author Faizan Virani
 * @author Thomas Lilly
 * @version 1.0
 */
public class MovieSearchActivity extends AppCompatActivity implements MovieFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MovieSearchActivity";

    private List<Movie> movieList;
    private String url = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=yedukp76ffytfuy24zsqk7f5&q=";
    private MyMovieRecyclerViewAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MovieSearch", "search triggered");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_search);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * Method to perform search after the Intent is filtered for ACTION SEARCH
     * @param intent the Intent from MainActivity
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Searched \"" + query + "\"");
            }
            final AsyncHttpClient client = new AsyncHttpClient();
            movieList = new ArrayList<>();
            final RecyclerView view = (RecyclerView) findViewById(R.id.fragment_container);
            client.get(url + query.replace(" ", "+"), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    JSONArray movies;
                    try {
                        movies = response.getJSONArray("movies");
                        movieList.clear();
                        for (int i = 0; i < movies.length(); i++) {
                            final JSONObject cur = movies.getJSONObject(i);
                            movieList.add(new Movie(cur.getString("title"), cur.getString("synopsis"),
                                    cur.getJSONObject("posters").getString("original")));
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

            if (view != null) {
                final Context context = view.getContext();
                view.setLayoutManager(new LinearLayoutManager(context));
                mAdapter = new MyMovieRecyclerViewAdapter(movieList, this);
                view.setAdapter(mAdapter);
                view.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST));
            }
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
