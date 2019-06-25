package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;

public class MovieDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MoviesRepository mRepository;
    private final String mMovieId;

    public MovieDetailsViewModelFactory(MoviesRepository repository, String movieId) {
        this.mRepository = repository;
        this.mMovieId = movieId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieDetailsViewModel(mRepository, mMovieId);
    }
}
