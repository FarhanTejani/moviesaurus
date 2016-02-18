package edu.team2348.moviesaurus;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

public class MovieSearchActivity extends AppCompatActivity {

    Button searchByNameButton;
    EditText searchQuery;
    AsyncHttpClient client;
    ListView listView;
    LinkedList<String> movieList;
    String url = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=yedukp76ffytfuy24zsqk7f5&q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = new AsyncHttpClient();

        searchQuery = (EditText) findViewById(R.id.movie_search_query);

        searchByNameButton = (Button) findViewById(R.id.search_by_name_button);
        movieList = new LinkedList<>();

//        final String uri = Uri.parse("http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=yedukp76ffytfuy24zsqk7f5&")
//                .buildUpon()
//                .appendQueryParameter("q", searchQuery.getText().toString())
//                .build().toString();
//        Log.d("URL", uri);

        searchByNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Param", searchQuery.getText().toString().replace(" ", "+"));
                client.get(url + searchQuery.getText().toString().replace(" ", "+"), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        JSONArray movies;
                        try {
                            movies = response.getJSONArray("movies");
                            movieList.clear();
                            for (int i = 0; i < movies.length(); i++) {
                                JSONObject cur = movies.getJSONObject(i);
                                movieList.add(cur.getString("title"));
                            }
                            updateListView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void updateListView() {
        listView = (ListView) findViewById(R.id.results_list_view);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, movieList);
        listView.setAdapter(adapter);
    }

}
