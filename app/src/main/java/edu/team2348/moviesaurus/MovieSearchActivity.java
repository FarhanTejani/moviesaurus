package edu.team2348.moviesaurus;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Activity that allows the user to search for a movie they desire
 * @author Faizan Virani
 * @version 1.0
 */
public class MovieSearchActivity extends AppCompatActivity implements MovieFragment.OnListFragmentInteractionListener {

    Button searchByNameButton;
    EditText searchQuery;
    AsyncHttpClient client;
    ListView listView;
    List<Movie> movieList;
    String url = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=yedukp76ffytfuy24zsqk7f5&q=";
    MyMovieRecyclerViewAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MovieSearch", "search triggered");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        handleIntent(getIntent());
        Intent intent = getIntent();
        Log.d("MovieSearch", "tries to handle intent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Searched \"" + query + "\"");
            }
//            MovieFragment mFrag = MovieFragment.newInstance(1, url + query.replace(" ", "+"));
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mFrag).commit();
            AsyncHttpClient client = new AsyncHttpClient();
            movieList = new ArrayList<>();
            RecyclerView view = (RecyclerView) findViewById(R.id.fragment_container);
            client.get(url + query.replace(" ", "+"), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    JSONArray movies;
                    try {
                        movies = response.getJSONArray("movies");
                        movieList.clear();
                        for (int i = 0; i < movies.length(); i++) {
                            JSONObject cur = movies.getJSONObject(i);
                            movieList.add(new Movie(cur.getString("title"), cur.getString("synopsis"),
                                    cur.getJSONObject("posters").getString("original")));
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (view != null) {
                Context context = view.getContext();
                view.setLayoutManager(new LinearLayoutManager(context));
                mAdapter = new MyMovieRecyclerViewAdapter(movieList, this);
                view.setAdapter(mAdapter);
                view.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST));
            }

        }

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d("MovieSearch", "tries to handle intent");
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            client = new AsyncHttpClient();
//            movieList = new LinkedList<>();
//            client.get(url + query.replace(" ", "+"), new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    super.onSuccess(statusCode, headers, response);
//                    JSONArray movies;
//                    try {
//                        movies = response.getJSONArray("movies");
//                        movieList.clear();
//                        for (int i = 0; i < movies.length(); i++) {
//                            JSONObject cur = movies.getJSONObject(i);
//                            movieList.add(cur.getString("title"));
//                        }
//                        updateListView();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        }
    }


    @Override
    public void onListFragmentInteraction(MyMovieRecyclerViewAdapter.ViewHolder item) {

        Log.d("MainActivty", "clicked item" + item.getLayoutPosition());
        Intent movieDetail = new Intent(this, MovieDetailActivity.class);
        movieDetail.putExtra("title", item.mContentView.getText());
        movieDetail.putExtra("poster", item.getPicUrl(item.getLayoutPosition()));
        startActivity(movieDetail);

    }

}
