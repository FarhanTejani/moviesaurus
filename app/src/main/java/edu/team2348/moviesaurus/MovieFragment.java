package edu.team2348.moviesaurus;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

    /**
     * The url literal to be used to add to the intent
     */
    private static final String URL_ARG = "url";
    /**
     * The sort literal to be used to add a sorting method to the intent
     */
    private static final String SORT = "sort";
    /**
     * The tag variable to be used by the Logger in case of an error
     */
    private static final String TAG = "MovieFragment";
    /**
     * The title literal which is used several times
     */
    private static final String TITLE = "title";
    /**
     * The listener for interactions between the fragment and the activity
     */
    private OnListFragmentInteractionListener mListener;
    /**
     * The url being queried by the. Could be REST call or recommendations
     */
    private String url;
    /**
     * The list of movies displayed in the fragment
     */
    private List<Movie> mList;
    /**
     * The adapter for viewing Movie objects in a RecyclerView
     */
    private MyMovieRecyclerViewAdapter mAdapter;
    /**
     * The Comparator that compares two movies
     */
    private Comparator<Movie> movieComparator;
    /**
     * The layout that allows for swiping down to refresh the RecyclerView with data
     */
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
     * @param sort how the RecyclerView should sort the items
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
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MyMovieRecyclerViewAdapter(mList, mListener);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
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
        /**
         * Method that specifies actions taken when user interacts with the fragment
         * @param item the Movie that was touched
         */
        void onListFragmentInteraction(Movie item);
    }

    /**
     * Exception thrown when class doesn't implement the interaction listener
     */
    private class ImplementInteractionException extends RuntimeException {

        /**
         * The constructor for the exception that takes in a String message
         * @param message the exception message
         */
        public ImplementInteractionException(String message) {
            super(message);
        }
    }

    /**
     * Class that handles response from Rotten Tomatoes JSON response
     */
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
                    final String synopsis = cur.getString("synopsis");
                    final String poster = cur.getJSONObject("posters").getString("original");
                    final ParseQuery<Movie> query = ParseQuery.getQuery(Movie.class);
                    query.whereEqualTo(TITLE, cur.getString(TITLE))
                            .whereEqualTo("description", cur.getString("synopsis"))
                            .findInBackground(new FoundMovieCallback(title, synopsis, poster, tList));
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Class that handles when movies are found from a Parse query
     */
    private final class FoundMovieCallback implements FindCallback<Movie> {
        /**
         * The title of the movie
         */
        private final String title;
        /**
         * The synopsis of the movie
         */
        private final String synopsis;
        /**
         * The url of the poster for the movie
         */
        private final String poster;
        /**
         * The url of the
         */
        private final List<String> tList;

        /**
         * Constructor for FoundMovieCallback that takes in all parameters
         * @param pTitle the title of the movie
         * @param pSynopsis the synopsis of the movie
         * @param pPoster the poster url of the movie
         * @param pTList the list of movie titles in order
         */
        private FoundMovieCallback(String pTitle, String pSynopsis, String pPoster, List<String> pTList) {
            this.title = pTitle;
            this.synopsis = pSynopsis;
            this.poster = pPoster;
            this.tList = pTList;
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

}
