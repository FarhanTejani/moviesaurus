package edu.team2348.moviesaurus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitTests {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void mapConversion() throws JSONException, ParseException {
        JsonArray storeRating = new JsonArray();
        Random rand = new Random();
        ParseObject.registerSubclass(Movie.class);
        Map<String, Double> expected = new HashMap<>();
        for (int i = 0; i < 2; i++) {
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
}