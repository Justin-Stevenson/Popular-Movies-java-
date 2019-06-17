package com.nanodegree.android.stevenson.popularmovies.data;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.local.MoviesDao;
import com.nanodegree.android.stevenson.popularmovies.data.network.MoviesService;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MoviesRepository {

    private static final String TAG = "MoviesRepository";
    private static final Object LOCK = new Object();

    private static MoviesRepository sInstance;

    private MoviesService mMoviesService;
    private MoviesDao mMoviesDao;

    private MoviesRepository(MoviesService moviesService, MoviesDao moviesDao) {
        mMoviesService = moviesService;
        mMoviesDao = moviesDao;
    }

    public static MoviesRepository getInstance(MoviesService moviesService, MoviesDao moviesDao) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: creating new repository instance");
                sInstance = new MoviesRepository(moviesService, moviesDao);
            }
        }
        Log.d(TAG, "getInstance: getting the repository instance");
        return sInstance;
    }

    public void getMovies(@SortOrder int sortOrder, Callback<List<Movie>> callback) {
        final Call<List<Movie>> request;

        if (SortOrderType.POPULAR == sortOrder) {
            request = mMoviesService.getPopularMovies();
        } else {
            request = mMoviesService.getTopRatedMovies();
        }


        request.enqueue(callback);
    }

    public void getMovieTrailers(String key, Callback<List<Trailer>> callback) {
        final Call<List<Trailer>> request;

        request = mMoviesService.getMovieTrailers(key);

        request.enqueue(callback);
    }

    public void getMovieReviews(String key, Callback<List<Review>> callback) {
        final Call<List<Review>> request;

        request = mMoviesService.getMovieReviews(key);

        request.enqueue(callback);
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mMoviesDao.getMovies();
    }

    public Movie getFavoriteMovieById(String id) {
        return mMoviesDao.getMovieById(id);
    }

    public void removeFavoriteMovie(Movie movie) {
        mMoviesDao.deleteMovie(movie);
    }

    public void addFavoriteMovie(Movie movie) {
        mMoviesDao.insertMovie(movie);
    }
}
