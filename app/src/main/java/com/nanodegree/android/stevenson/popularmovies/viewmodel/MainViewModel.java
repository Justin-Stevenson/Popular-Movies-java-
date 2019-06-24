package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;

import java.util.List;

public class MainViewModel extends ViewModel {

    private final MoviesRepository mRepository;
    private LiveData<List<Movie>> mFavoriteMovies;

    public MainViewModel(MoviesRepository mRepository) {
        this.mRepository = mRepository;
        mFavoriteMovies = mRepository.getFavoriteMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }
}
