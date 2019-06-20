package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MoviesRepository mRepository;

    public MainViewModelFactory(MoviesRepository repository) {
        this.mRepository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(mRepository);
    }
}
