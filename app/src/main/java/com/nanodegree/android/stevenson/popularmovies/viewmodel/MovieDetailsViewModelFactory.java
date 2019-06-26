package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MovieDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Application mApplication;
    private final String mMovieId;

    public MovieDetailsViewModelFactory(Application application, String movieId) {
        this.mApplication = application;
        this.mMovieId = movieId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieDetailsViewModel(mApplication, mMovieId);
    }
}
