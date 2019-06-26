package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.nanodegree.android.stevenson.popularmovies.common.NoFavoriteMoviesExistException;
import com.nanodegree.android.stevenson.popularmovies.common.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;
import com.nanodegree.android.stevenson.popularmovies.model.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private final MoviesRepository mRepository;
    private final LiveData<List<Movie>> mFavoriteMovies;
    private final MediatorLiveData<Resource<List<Movie>>> mMovies = new MediatorLiveData<>();
    private final Map<SortOrder, List<Movie>> mNetworkMovies = new HashMap<>();
    private Disposable mNetworkDisposable;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mRepository = MoviesRepository.getInstance(application);
        mFavoriteMovies = mRepository.getFavoriteMovies();
    }

    @Override
    protected void onCleared() {
        if (mNetworkDisposable != null) {
            mNetworkDisposable.dispose();
        }
    }

    public LiveData<Resource<List<Movie>>> getMovies() {
        return mMovies;
    }

    public void retrieveMovies(SortOrder sortOrder) {
        if (SortOrder.FAVORITES == sortOrder) {
            mMovies.removeSource(mFavoriteMovies);
            mMovies.addSource(mFavoriteMovies, movies -> mMovies.setValue(getFavoriteMoviesResource(movies)));
        } else {
            mMovies.removeSource(mFavoriteMovies);
            retrieveMoviesFromTheNetwork(sortOrder);
        }
    }

    private Resource<List<Movie>> getFavoriteMoviesResource(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return Resource.error(new NoFavoriteMoviesExistException());
        }

        return Resource.success(movies);
    }

    private void retrieveMoviesFromTheNetwork(SortOrder sortOrder) {
        Single<List<Movie>> single = getNetworkMovies(sortOrder);

        mNetworkDisposable = single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mMovies.setValue(Resource.loading()))
                .subscribe(
                        response -> handleSuccessfulResponse(response, sortOrder),
                        error -> handleErrorResponse(error)
                );
    }

    private Single<List<Movie>> getNetworkMovies(SortOrder sortOrder) {
        List<Movie> movies = mNetworkMovies.get(sortOrder);

        if (movies == null || movies.isEmpty()) {
            return mRepository.getMovies(sortOrder);
        }

        return Single.just(movies);
    }

    private void handleErrorResponse(Throwable error) {
        mMovies.setValue(Resource.error(error));
        mNetworkDisposable.dispose();
    }

    private void handleSuccessfulResponse(List<Movie> response, SortOrder sortOrder) {
        mNetworkMovies.put(sortOrder, response);
        mMovies.setValue(Resource.success(response));
        mNetworkDisposable.dispose();
    }
}
