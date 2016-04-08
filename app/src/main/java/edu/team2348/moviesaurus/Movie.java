package edu.team2348.moviesaurus;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to represent the model of a Movie
 * @author Thomas Lilly
 * @version 1.0
 */
@ParseClassName("Movie")
class Movie extends ParseObject {

    /**
     * TAG to identify this class in the logger
     */
    private static final String TAG = "Movie";

    /**
     * The title of the movie
     */
    private String title;

    /**
     * Map that associates ParseUser ids to rating doubles
     */
    private Map<String, Double> rating;

    /**
     * The description of the movie
     */
    private String description;

    /**
     * The url for the movie poster
     */
    private String poster;

    /**
     * boolean to tell whether the movie has been rated or not
     */
    private boolean rated;

    /**
     * Default Movie constructor necessary for ParseObject
     */
    public Movie() {

    }

    /**
     * Constructor for Movie with all parameters
     * @param pTitle The title of the movie
     * @param pDescription The description of the movie
     * @param pPoster The url of the movie's poster
     */
    public Movie(String pTitle, String pDescription, String pPoster) {
        this.title = pTitle;
        put("title", this.title);
        this.description = pDescription;
        put("description", this.description);
        this.poster = pPoster;
        put("poster", this.poster);
        put("rated", rated);
        rating = new HashMap<>();
        put("ratings", new JSONArray());
    }


    /**
     * Gets title of the Movie
     * @return the title of the movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the whether the Movie is rated or not
     * @param pRated the status of whether the movie is rated or not
     */
    public void setRated(boolean pRated) {
        this.rated = pRated;
    }

    /**
     * Sets the title of the Movie
     * @param pTitle the title of the Movie
     */
    public void setTitle(String pTitle) {
        this.title = pTitle;
    }

    /**
     * Sets the poser url of the Movie
     * @param pPoster the url of the Movie poster
     */
    public void setPoster(String pPoster) {
        this.poster = pPoster;
    }

    /**
     * Sets the description of the Movie
     * @param pDescription the description (synopsis) of the movie
     */
    public void setDescription(String pDescription) {
        this.description = pDescription;
    }

    /**
     * Gets a HashMap<String, Double> from the JSON array stored in Parse
     * @return the rating HashMap of users and their ratings
     */
    private Map<String, Double> fromJSON() {
        final JSONArray storeRating = getJSONArray("ratings");
        final HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < storeRating.length(); i++) {
            JSONObject o;
            try {
                o = storeRating.getJSONObject(i);
                map.put(o.getString("user"), o.getDouble("rating"));
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
        return map;
    }

    /**
     * Converts the Map of strings to doubles to the
     * @return the JSONArray representation of the Map
     */
    private JSONArray toJSON() {
        final JSONArray array = new JSONArray();
        for (final Map.Entry<String, Double> item : rating.entrySet()) {
            final JSONObject o = new JSONObject();
            try {
                o.put("user", item.getKey());
                o.put("rating", item.getValue());
                array.put(o);
            } catch (JSONException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
        return array;
    }

    /**
     * Method to return whether the movie is rated or not
     * @return the status of whether the movie is rated
     */
    public boolean isRated() {
        return rating.size() > 0 && rated;
    }

    /**
     * Method to add rating to the Movie's map of ratings
     * @param user The users objectID making the rating
     * @param score The rating (max of 5) of the movie
     */
    public void addRating(String user, double score) {
        restoreRatings();
        rating.put(user, score);
        rated = true;
        put("ratings", toJSON());
        put("rated", rated);
    }

    /**
     * Method to calculate and return the average rating of the Movie
     * @return the Movie's rating
     */
    public float getRating() {
        float sum = 0;
        restoreRatings();
        for (final Double d : rating.values()) {
            sum += d;
        }
        return sum / rating.size();
    }

    /**
     * Getter to return the Movie's poster URL
     * @return the url of the poster
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Filters the ratings map by only keeping the majors in the parameter List
     * @param major the list of majors to be retained
     */
    public void filterByMajor(final List<String> major) {
        final Set<String> keys = rating.keySet();
        for (final String k : keys) {
            final ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            try {
                final ParseUser u = query.get(k);
                if(!major.contains(u.getString("major"))) {
                    rating.remove(k);
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    /**
     * Method to restore ratings map to what is currently in Parse
     */
    public void restoreRatings() {
        rating = fromJSON();
    }


    /**
     * Method to get a comparator that will sort movies in descending order
     * @return the descending sort Comparator
     */
    public static Comparator<Movie> sortByRatingComp() {
        return new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                final double  r = rhs.getRating() - lhs.getRating();
                if (r < 0) {
                    return -1;
                } else if (r > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Movie)) {
            return false;
        }
        final Movie other = (Movie) o;
        return title.equals(other.title) && poster.equals(other.poster) && description.equals(other.description);
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }


    @Override
    public String toString() {
        return title;
    }




}
