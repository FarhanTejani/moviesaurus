package edu.team2348.moviesaurus;

import android.util.Log;

import com.parse.GetCallback;
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
 * Class to represent the model of a movie
 * @author Thomas Lilly
 * @version 1.0
 */
@ParseClassName("Movie")
public class Movie extends ParseObject {
    private String title;
    private HashMap<String, Double> rating;
    private String description;
    private String poster;
    private boolean rated;

    public Movie() throws JSONException {

//        title = getString("title");
//        description = getString("description");
//        poster = getString("poster");
//        rated = getBoolean("rated");

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



    public String getTitle() {
        return title;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Double> getRatingMap() {
        return rating;
    }

    private HashMap<String, Double> fromJSON() {
        JSONArray storeRating = getJSONArray("ratings");
        HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < storeRating.length(); i++) {
            JSONObject o;
            try {
                o = storeRating.getJSONObject(i);
                map.put(o.getString("user"), o.getDouble("rating"));
            } catch (JSONException e) {
                e.printStackTrace();
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
        Log.d(getClass().getSimpleName(), "Made JSON length " + array.length());
        return array;
    }

    public boolean isRated() {
        return rating.size() > 0 && rated;
    }

    public void addRating(String user, double score) {

        Log.d(getClass().getSimpleName(), "Added Rating");
        rating = fromJSON();
        rating.put(user, score);
        rated = true;
        put("ratings", toJSON());
        put("rated", rated);
    }



    public float getRating() {
        float sum = 0;
        rating = fromJSON();
        for (Double d : rating.values()) {
            sum += d;
        }
        return sum / rating.size();
    }

    public String getPoster() {
        return poster;
    }

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
                Log.e("Movie", e.getMessage());
            }
        }

    }

    public void restoreRatings() {
        rating = fromJSON();
    }



    public String getDescription() {
        return description;
    }

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
    public String toString() {
        return title;
    }




}
