package edu.team2348.moviesaurus;

/**
 * Class to represent the model of a movie
 * @author Thomas Lilly
 * @version 1.0
 */
public class Movie {
    private final String name;
    private final String rating;
    private final String description;

    public Movie(String name, String rating, String description) {
        this.name = name;
        this.rating = rating;
        this.description = description;
    }

}
