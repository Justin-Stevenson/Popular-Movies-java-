package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;

public class MovieDetailsViewModel extends ViewModel {

    private final MoviesRepository mRepository;
    private final LiveData<Movie> mMovie;

    public MovieDetailsViewModel(MoviesRepository repository, String movieId) {
        mRepository = repository;
        mMovie = mRepository.getFavoriteMovieById(movieId);
    }

    public LiveData<Movie> getMovie() {
        return mMovie;
    }
}
