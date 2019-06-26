package com.nanodegree.android.stevenson.popularmovies.common;

public class NoFavoriteMoviesExistException extends RuntimeException {

    public NoFavoriteMoviesExistException() {
        super("No favorite movies exist in the database");
    }
}
