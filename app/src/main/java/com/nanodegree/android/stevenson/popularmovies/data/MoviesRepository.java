package com.nanodegree.android.stevenson.popularmovies.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.nanodegree.android.stevenson.popularmovies.common.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.local.MoviesDao;
import com.nanodegree.android.stevenson.popularmovies.data.local.MoviesDatabase;
import com.nanodegree.android.stevenson.popularmovies.data.network.MoviesService;
import com.nanodegree.android.stevenson.popularmovies.data.network.ServiceFactory;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Review;
import com.nanodegree.android.stevenson.popularmovies.model.Trailer;

import java.util.List;

import io.reactivex.Single;

public class MoviesRepository {

    private static final String TAG = "MoviesRepository";
    private static final Object LOCK = new Object();

    private static MoviesRepository sInstance;

    private final MoviesService mMoviesService;
    private final MoviesDao mMoviesDao;

    private MoviesRepository(Application application) {
        mMoviesService = ServiceFactory.getService(MoviesService.class);
        MoviesDatabase database = MoviesDatabase.getInstance(application);
        mMoviesDao = database.moviesDao();
    }

    public static MoviesRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: creating new repository instance");
                sInstance = new MoviesRepository(application);
            }
        }
        Log.d(TAG, "getInstance: getting the repository instance");
        return sInstance;
    }

    public Single<List<Movie>> getMovies(SortOrder sortOrder) {
        if (SortOrder.POPULAR == sortOrder) {
            Log.d(TAG, "getMovies: retrieving popular movies from network");
            return mMoviesService.getPopularMovies();
        }

        Log.d(TAG, "getMovies: retrieving top rated movies from network");
        return mMoviesService.getTopRatedMovies();
    }

    public Single<List<Trailer>> getMovieTrailers(String key) {
        Log.d(TAG, "getMovieTrailers: retrieving movie trailers from network");
        return mMoviesService.getMovieTrailers(key);
    }

    public Single<List<Review>> getMovieReviews(String key) {
        Log.d(TAG, "getMovieReviews: retrieving movie reviews from network");
        return mMoviesService.getMovieReviews(key);
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        Log.d(TAG, "getFavoriteMovies: retrieving movies from db");
        return mMoviesDao.getMovies();
    }

    public LiveData<Movie> getFavoriteMovieById(String id) {
        Log.d(TAG, "getFavoriteMovieById: retrieving favorite movie");
        return mMoviesDao.getMovieById(id);
    }

    public void removeFavoriteMovie(Movie movie) {
        Log.d(TAG, "removeFavoriteMovie: removing movie from favorites");
        mMoviesDao.deleteMovie(movie);
    }

    public void addFavoriteMovie(Movie movie) {
        Log.d(TAG, "addFavoriteMovie: add movie to favorites");
        mMoviesDao.insertMovie(movie);
    }
}
