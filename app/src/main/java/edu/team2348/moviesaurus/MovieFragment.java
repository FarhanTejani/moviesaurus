package edu.team2348.moviesaurus;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MovieFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String URL_ARG = "url";
    private static final String SORT = "sort";
    private static final String TAG = "MovieFragment";
    private static final String TITLE = "title";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String url;
    private List<Movie> mList;
    private MyMovieRecyclerViewAdapter mAdapter;
    private Comparator<Movie> movieComparator;
    private SwipeRefreshLayout swipeLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieFragment() {
    }

    /**
     * Static factory method that returns an instance of MovieFragment
     * @param url the REST call to be called
     * @param sort how the RecylerView should sort the items
     * @return the instance of MovieFragment
     */
    @SuppressWarnings("unused")
    public static MovieFragment newInstance(String url, String sort) {
        final MovieFragment fragment = new MovieFragment();
        final Bundle args = new Bundle();
        args.putString(URL_ARG, url);
        args.putString(SORT, sort);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL_ARG);
            final String sort = getArguments().getString(SORT);
            if ("rating".equals(sort)) {
                movieComparator = Movie.sortByRatingComp();
            } else {
                movieComparator = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mList = new ArrayList<>();

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.colorAccent);
        if ("recommendation".equals(url)) {
            getRecommendations();
        } else {
            callRottenTomatoes();
        }

        final Context context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.movie_list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        mAdapter = new MyMovieRecyclerViewAdapter(mList, mListener);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        return view;
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        if ("recommendation".equals(url)) {
            getRecommendations();
        } else {
            callRottenTomatoes();
        }

        mAdapter.notifyDataSetChanged();
        swipeLayout.setRefreshing(false);

    }

    /**
     * Method to get recommendations for the current user
     */
    private void getRecommendations() {
        mList.clear();
        final ParseQuery<Movie> query = ParseQuery.getQuery(Movie.class);
        query.whereEqualTo("rated", true).findInBackground(new FindCallback<Movie>() {
            @Override
            public void done(List<Movie> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        final Movie m = objects.get(i);
                        m.setTitle(m.getString("title"));
                        m.setPoster(m.getString("poster"));
                        m.setDescription(m.getString("description"));
                        mList.add(m);
                    }
                    if (movieComparator != null) {
                        Collections.sort(mList, movieComparator);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    /**
     * Method to get list of movies from Rotten Tomatoes
     */
    private void callRottenTomatoes() {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new rtHTTPResponseHandler());
    }

    private class rtHTTPResponseHandler extends JsonHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            JSONArray movies;
            try {
                movies = response.getJSONArray("movies");
                mList.clear();
                for (int i = 0; i < movies.length(); i++) {
                    mList.add(null);
                }
                final ArrayList<String> tList = new ArrayList<>();
                for (int i = 0; i < movies.length(); i++) {
                    final JSONObject cur = movies.getJSONObject(i);
                    final String title = cur.getString(TITLE);
                    tList.add(title);
                    final String synop = cur.getString("synopsis");
                    final String poster = cur.getJSONObject("posters").getString("original");
                    final ParseQuery<Movie> query = ParseQuery.getQuery(Movie.class);
                    query.whereEqualTo(TITLE, cur.getString(TITLE))
                            .whereEqualTo("description", cur.getString("synopsis"))
                            .findInBackground(new FoundMovieCallback(title, synop, poster, tList));
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class FoundMovieCallback implements FindCallback<Movie> {

        private final String title;
        private final String synopsis;
        private final String poster;
        private final List<String> tList;

        private FoundMovieCallback(String title, String synopsis, String poster, List<String> tList) {
            this.title = title;
            this.synopsis = synopsis;
            this.poster = poster;
            this.tList = tList;
        }

        @Override
        public void done(List<Movie> objects, ParseException e) {
            if (e == null) {
                if (objects.size() == 0) {
                    final Movie ele = new Movie(title, synopsis, poster);
                    ele.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    });
                    mList.set(tList.indexOf(title), ele);
                } else {
                    final Movie m = objects.get(0);
                    m.setTitle(m.getString(TITLE));
                    m.setPoster(m.getString("poster"));
                    m.setDescription(m.getString("description"));
                    m.setRated(m.getBoolean("rated"));
                    mList.set(tList.indexOf(title), m);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                Log.e("MovieFrag", e.getMessage());
            }

        }
    }

    /**
     * Method to filter movies by retaining the majors in parameter list
     * @param majors the list of majors to retain
     */
    public void filterOut(List<String> majors) {
        swipeLayout.setRefreshing(true);
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).filterByMajor(majors);
            if (!mList.get(i).isRated()) {
                mList.remove(i--);
            }
        }
        mAdapter.notifyDataSetChanged();
        swipeLayout.setRefreshing(false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new ImplementInteractionException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    private class ImplementInteractionException extends RuntimeException {
        public ImplementInteractionException(String message) {
            super(message);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Movie item);
    }
}
