package edu.team2348.moviesaurus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

/**
 * Activity that allows user to see a list of new DVD releases
 * @author Faizan Virani
 * @version 1.0
 */

public class NewDVDReleasesActivity extends AppCompatActivity {

    AsyncHttpClient client;
    ListView listView;
    LinkedList<String> movieList;
    String url = "http://api.rottentomatoes.com/api/public/v1.0/lists/dvds/new_releases.json?apikey=yedukp76ffytfuy24zsqk7f5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dvdreleases);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = new AsyncHttpClient();
        movieList = new LinkedList<>();

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray movies;
                try {
                    movies = response.getJSONArray("movies");
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

    private void updateListView() {
        listView = (ListView) findViewById(R.id.new_dvd_releases_results_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, movieList);
        listView.setAdapter(adapter);
    }


}
