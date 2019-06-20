package com.nanodegree.android.stevenson.popularmovies.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nanodegree.android.stevenson.popularmovies.common.SortOrderType.SortOrder;
import com.nanodegree.android.stevenson.popularmovies.data.MoviesRepository;
import com.nanodegree.android.stevenson.popularmovies.data.network.helpers.NetworkConnectionException;
import com.nanodegree.android.stevenson.popularmovies.model.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    private final MoviesRepository mRepository;
    private MediatorLiveData<List<Movie>> mMovies = new MediatorLiveData<>();
    private @SortOrder int mSortOrder;
    private boolean mNetworkConnectionException;

    public MainViewModel(MoviesRepository mRepository) {
        this.mRepository = mRepository;
    }

    public void loadMovies(@SortOrder int sortOrder) {
        mNetworkConnectionException = false;
        this.mRepository.getMovies(sortOrder, getMoviesCallback());
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovies;
    }

    public boolean isNetworkConnectionException() {
        return mNetworkConnectionException;
    }

    private Callback<List<Movie>> getMoviesCallback() {
        return new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                mMovies.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                if (t instanceof NetworkConnectionException) {
                    Log.e(TAG, "onFailure: error retrieving movie data due to no network connection", t);
                    mNetworkConnectionException = true;
                } else {
                    Log.e(TAG, "onFailure: error retrieving movie data", t);
                    mNetworkConnectionException = false;
                }
                mMovies.setValue(null);
            }
        };
    }
}
