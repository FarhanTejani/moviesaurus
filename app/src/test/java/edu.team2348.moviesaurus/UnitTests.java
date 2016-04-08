package edu.team2348.moviesaurus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.Integer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dalvik.annotation.TestTargetClass;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitTests {

    @Test
    public void mapConversion() throws JSONException, ParseException {
        JsonArray storeRating = new JsonArray();
        Random rand = new Random();
        Map<String, Double> expected = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            double r = rand.nextDouble() * 4 + 1;
            JsonObject o = new JsonObject();
            o.addProperty("user", String.valueOf((char) (i + 65)));
            o.addProperty("rating", r);
            storeRating.add(o);
            expected.put(String.valueOf((char)(i + 65)), r);
        }
        final HashMap<String, Double> actual = new HashMap<>();
        for (int i = 0; i < storeRating.size(); i++) {
            JsonObject o = (JsonObject)storeRating.get(i);
            String user = o.get("user").getAsString();
            Double rating = o.get("rating").getAsDouble();
            actual.put(user, rating);
        }
        assertEquals(expected, actual);
    }

    @Test
    public void movieEqualsTest() {
        ParseObject.registerSubclass(Movie.class);
        Movie divergent = new Movie("Divergent", "The best movie.", "www.google.com");
        Movie stillDivergent = new Movie("Divergent", "The best movie.", "www.google.com");
        Movie duplicate = divergent;
        Movie nullMovie = null;
        assertFalse(divergent.equals(nullMovie));
        assertTrue(divergent.equals(duplicate));
        assertFalse(divergent.equals(new String("hello")));
        assertTrue(divergent.equals(stillDivergent));
    }

    @Test
    public void movieComparatorTest() {
        ParseObject.registerSubclass(Movie.class);
        Comparator<Movie> comparator = Movie.sortByRatingComp();
        Movie a = new Movie("A", "movie", "www.google.com");
        Movie b = new Movie("B", "movie", "www.google.com");
        //both unrated
        assertEquals(comparator.compare(a, b), 0);
        //only one rated
        a.addRating("carina", 5.0);
        int result = comparator.compare(a, b);
        assertTrue(result == 0);
        //both same rating
        b.addRating("carina", 5.0);
        result = comparator.compare(a, b);
        assertTrue(result == 0);
        //both rated differently
        b.addRating("carina", 4.0);
        result = comparator.compare(a, b);
        assertTrue(result < 0);
        //rated oppositely
        a.addRating("carina", 3.0);
        result = comparator.compare(a, b);
        assertTrue(result > 0);
    }
}