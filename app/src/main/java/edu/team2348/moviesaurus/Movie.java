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
public class Movie extends ParseObject {
    private static final String TAG = "Movie";

    private String title;
    private Map<String, Double> rating;
    private String description;
    private String poster;
    private boolean rated;

    /**
     * Default Movie constructor necessary for ParseObject
     */
    public Movie() {

    }

    public Movie(String title, String description, String poster) {
        this.title = title;
        put("title", this.title);
        this.description = description;
        put("description", this.description);
        this.poster = poster;
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
     * @param rated the status of whether the movie is rated or not
     */
    public void setRated(boolean rated) {
        this.rated = rated;
    }

    /**
     * Sets the title of the Movie
     * @param title the title of the Movie
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the poser url of the Movie
     * @param poster the url of the Movie poster
     */
    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     * Sets the description of the Movie
     * @param description the description (synopsis) of the movie
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Double> getRatingMap() {
        return rating;
    }

    /**
     * Gets a HashMap<String, Double> from the JSON array stored in Parse
     * @return the rating HashMap of users and their ratings
     */
    private Map<String, Double> fromJSON() {
        JSONArray storeRating = getJSONArray("ratings");
        HashMap<String, Double> map = new HashMap<>();
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


    private JSONArray toJSON() {
        JSONArray array = new JSONArray();
        for (Map.Entry<String, Double> item : rating.entrySet()) {
            JSONObject o = new JSONObject();
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
        for (Double d : rating.values()) {
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
        Set<String> keys = rating.keySet();
        for (final String k : keys) {
            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            try {
                ParseUser u = query.get(k);
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
     * Method to get the description (synopsis) of the Movie
     * @return the description of the Movie
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method to get a comparator that will sort movies in descending order
     * @return the descending sort Comparator
     */
    public static Comparator<Movie> sortByRatingComp() {
        return new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                double  r = rhs.getRating() - lhs.getRating();
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
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Movie)) return false;
        Movie other = (Movie) o;
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
